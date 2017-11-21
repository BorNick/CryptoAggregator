package bter;

import base.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BterAggregator extends AbstractAggregator {

    private String depthUrl = "http://data.bter.com/api2/1/orderBook/";
    private String orderUrl = "http://data.bter.com/api2/1/tradeHistory/";

    /**
     * Constructor
     *
     * @param properties Properties of stock
     * @param lastOrderId 
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public BterAggregator(Properties properties, long lastOrderId) throws ClassNotFoundException, SQLException {
        super(properties, lastOrderId);
        super.properties.typeStock = AbstractAggregator.Stocks.BTER;
        String convertPair = this.convertPair(properties.typePair);
        this.depthUrl += convertPair;
        this.orderUrl += convertPair;
    }
    
    /**
     * Constructor
     *
     * @param properties Properties of stock
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public BterAggregator(Properties properties) throws ClassNotFoundException, SQLException {
        super(properties, AbstractAggregator.Stocks.BTER);
        super.properties.typeStock = AbstractAggregator.Stocks.BTER;
        String convertPair = this.convertPair(properties.typePair);
        this.depthUrl += convertPair;
        this.orderUrl += convertPair;
    }

    /**
     * Converts pair string into correct string for URL.
     *
     * @param pair Pair of crypto-currency
     * @return Correct pair string
     */
    @Override
    public String convertPair(int pair) {
        switch (pair) {
            case 1:
                return "ltc_btc";
        }
        throw new RuntimeException("Error: currency pair is not supported");
    }

    /**
     * Does request to server to get orders list.
     *
     * @param id Order id from which need start to get orders history from
     * server
     * @return End order id + 1 if list not empty, else id
     * @throws java.io.IOException
     */
    @Override
    public long ordersRequest(long id) throws IOException {
        String url = this.orderUrl + "/" + id;
        String response = HttpRequest.send(url, "GET");
        BterHistory bterHistory = this.getObjectMapper().readValue(response, BterHistory.class);
        List<BaseOrder> res = bterHistory.toBaseOrderList(properties.typePair);
        super.setOrders(res);
        if (res.size() > 0) {
            BaseOrder last = res.get(res.size() - 1);
            return last.id;
        } else {
            return id;
        }
    }

    /**
     * Does request to server to get depth
     * @throws java.io.IOException
     */
    @Override
    public void depthRequest() throws IOException {
        String response = HttpRequest.send(this.depthUrl, "GET");
        BterDepth bterDepth = (BterDepth) this.getObjectMapper().readValue(response, BterDepth.class);
        BaseDepth res = bterDepth.toBaseDepth();
        res.timestamp = System.currentTimeMillis() / 1000;
        res.typePair = properties.typePair;
        res.typeStock = properties.typeStock;
        super.setDepth(res);
    }

}
