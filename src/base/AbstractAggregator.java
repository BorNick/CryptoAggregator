package base;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * AbstractAggregator class is the base class for all stock exchange aggregators
 *
 */
public abstract class AbstractAggregator {

    /**
     * The properties of any aggregators of stock exchange
     */
    public static class Properties {

        /**
         * The type of a currency pair from Pairs class
         */
        public int typePair;

        /**
         * The type of a stock exchange from Stocks class
         */
        public int typeStock;

        /**
         * The time delay of AggregatorThread between requests
         */
        public int timeDelay;

        /**
         * The name of the data base
         */
        public String dataBaseName;

        /**
         * The type of the data base
         */
        public String dataBaseType;

        /**
         * The database user login
         */
        public String dataBaseLogin;

        /**
         * The database user password
         */
        public String dataBasePassword;

        /**
         * The host address of the data base
         */
        public String dataBaseHost;

        /**
         * The port of the data base
         */
        public int dataBasePort;

        /**
         *
         * @param typePair The type of a currency pair from Pairs class
         * @param timeDelay The time delay of AggregatorThread between requests
         */
        public Properties(int typePair, int timeDelay) {
            this.typePair = typePair;
            this.timeDelay = timeDelay;
            this.dataBaseName = "trading";
            this.dataBaseType = "mysql";
            this.dataBaseHost = "localhost";
            this.dataBasePort = 3306;
            this.dataBaseLogin = "root";
            this.dataBasePassword = "";
        }
    }

    /**
     * The types of orders
     */
    public static class OrderTypes {

        /**
         * The buy order
         */
        public static final int BUY = 1;

        /**
         * The sell order
         */
        public static final int SELL = 2;
    }

    /**
     * The types of stock exchanges
     */
    public static class Stocks {

        /**
         *
         */
        public static final int BTER = 1;

        /**
         *
         */
        public static final int CEXIO = 2;

        /**
         *
         */
        public static final int POLONIEX = 3;

        /**
         *
         */
        public static final int BITTREX = 4;
    }

    /**
     * The types of currency pairs
     */
    public static class Pairs {

        /**
         *
         */
        public static final int LTC_BTC = 1;

        /**
         *
         */
        public static final int BTC_USD = 2;
    }

    private List<BaseOrder> orders;
    private BaseDepth depth;

    /**
     * The local copy of property object
     */
    protected Properties properties;
    private Connection connection;
    private ObjectMapper mapper;
    private long lastOrderId;

    /**
     * The getObjectMapper function returns the ObjectMapper object of the stock exchange aggregator object.
     * In the first call, the function allocates new ObjectMapper and saves it into local field mapper.
     * In the next calls, the function returns the value of local field mapper.
     * 
     * @return ObjectMapper object of this object
     */
    protected ObjectMapper getObjectMapper() {
        if (this.mapper == null) {
            this.mapper = new ObjectMapper();
        }
        return this.mapper;
    }

    /**
     *
     * @param properties
     * @param lastOrderId the value of last order identifier from data base
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public AbstractAggregator(Properties properties, long lastOrderId) throws ClassNotFoundException, SQLException {
        this.properties = properties;
        this.lastOrderId = lastOrderId;

        Class.forName("com.mysql.jdbc.Driver");
        StringBuilder url = new StringBuilder();
        url.append("jdbc:mysql://");
        url.append(properties.dataBaseHost);
        url.append(":");
        url.append(properties.dataBasePort);
        url.append("/");
        url.append(properties.dataBaseName);

        this.connection = DriverManager.getConnection(
                url.toString(),
                properties.dataBaseLogin,
                properties.dataBasePassword
        );
        /*Connection con = DriverManager.getConnection (
                "jdbc:mysql://localhost:3306/trading", "root", "");*/
    }
    
    /**
     * Does not require lastOrderId. It is taken from the database
     * @param properties
     * @param stockType
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public AbstractAggregator(Properties properties, int stockType) throws ClassNotFoundException, SQLException {
        this.properties = properties;
        //this.lastOrderId = lastOrderId;

        Class.forName("com.mysql.jdbc.Driver");
        StringBuilder url = new StringBuilder();
        url.append("jdbc:mysql://");
        url.append(properties.dataBaseHost);
        url.append(":");
        url.append(properties.dataBasePort);
        url.append("/");
        url.append(properties.dataBaseName);

        this.connection = DriverManager.getConnection(
                url.toString(),
                properties.dataBaseLogin,
                properties.dataBasePassword
        );
        Statement stmt = this.connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT order_id FROM orders WHERE stock = \"" + stockType + "\" ORDER BY order_id DESC LIMIT 1");
        if(rs.isBeforeFirst()){
            rs.next();
            this.lastOrderId = rs.getLong("order_id") + 1;
        }else{
            this.lastOrderId = 0;
        }
        System.out.println("Last order_id = " + this.lastOrderId);
        rs.close();
    }

    /**
     * The getOrders function returns last saved list of base orders
     * 
     * @return The list of base orders
     */
    synchronized public List<BaseOrder> getOrders() {
        return this.orders;
    }

    /**
     * The setOrders function sets last saved list of base orders
     * 
     * @param orders The list of base orders
     */
    synchronized public void setOrders(List<BaseOrder> orders) {
        this.orders = orders;
    }

    /**
     * The getDepth function returns last saved base depth
     * 
     * @return The base depth
     */
    synchronized public BaseDepth getDepth() {
        return this.depth;
    }

    /**
     * The setDepth function sets last saved base depth
     * 
     * @param depth The base depth
     */
    synchronized public void setDepth(BaseDepth depth) {
        this.depth = depth;
    }

    /**
     * The getProperties() function returns Properties object which was got from constructor
     * 
     * @return The Properties object
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * The ordersRequest function requests new orders from stock exchange
     * 
     * @param id The last order identifier from data base
     * @return The maximal order identifier among new orders
     * @throws IOException
     */
    public abstract long ordersRequest(long id) throws IOException;

    /**
     * The depthRequest function requests new depth from stock exchange
     * 
     * @throws IOException
     */
    public abstract void depthRequest() throws IOException;

    /**
     * The convertPair function converts a pair of Pairs class to a stock exchange pair
     * 
     * @param pair The currency pair of Pairs class
     * @return The string of stock exchange pair
     */
    public abstract String convertPair(int pair);

    /**
     * The saveToBase function saves new data to data base
     * 
     * @throws SQLException
     * @throws IOException
     */
    public void saveToBase() throws SQLException, IOException {
        if (this.depth != null) {
            this.saveDepthToBase();
        }
        if (this.orders != null) {
            this.saveOrderToBase();
        }
    }

    private void saveDepthToBase() throws SQLException, IOException {
        String depthSql = "INSERT INTO `depth` (`stock`, `pair`, `crtime`, `data`) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = this.connection.prepareStatement(depthSql);
        preparedStatement.setInt(1, this.depth.typeStock);
        preparedStatement.setInt(2, this.depth.typePair);
        preparedStatement.setLong(3, this.depth.timestamp);
        preparedStatement.setString(4, this.depth.toJson(this.getObjectMapper()));
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private void saveOrderToBase() throws SQLException {
        String orderSql = "INSERT INTO `orders` (`order_id`, `timestamp`, `pair`, `stock`, `action`, `amount`, `price`, `total`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = this.connection.prepareStatement(orderSql);
        for (BaseOrder order : this.orders) {
            preparedStatement.setLong(1, order.id);
            preparedStatement.setLong(2, order.timestamp);
            preparedStatement.setInt(3, order.typePair);
            preparedStatement.setInt(4, order.typeStock);
            preparedStatement.setInt(5, order.typeOrder);
            preparedStatement.setDouble(6, order.amount);
            preparedStatement.setDouble(7, order.price);
            preparedStatement.setDouble(8, order.amount * order.price);
            preparedStatement.executeUpdate();
        }
        preparedStatement.close();
    }

    /**
     * The process function executes some processing tasks
     * 
     * @throws IOException
     */
    public void process() throws IOException {
        this.lastOrderId = this.ordersRequest(this.lastOrderId);
        //System.out.println("Last order_id updated: " + this.lastOrderId);
        this.depthRequest();
        //System.out.println(this.lastOrderId);
    }

}
