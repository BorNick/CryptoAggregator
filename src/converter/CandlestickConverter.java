package converter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CandlestickConverter extends Thread{
    
    public class OnePeriodData{
        long timestamp;
        double open, high, low, close, volume;
        
        public OnePeriodData(){
            this(0, 0, 0, 0, 0, 0);
        }
        
        public OnePeriodData(long timestamp, double open, double high, double low, double close, double volume){
            this.timestamp = timestamp;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }
        
        public String toJSON(){
            String json = "[" + (timestamp) * 1000 + ", " +
                    open + ", " + high + ", " + low + ", " +
                    close + ", " + volume + "]";
            return json;
        }
    }
    
    public static final long PERIODDURATION = 5 * 60;//5min
    
    private static Connection connection;
    
    private long lastTimestamp;
    
    public CandlestickConverter(String url, String login, String password) throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        this.connection = DriverManager.getConnection (url, login, password);
        updateLastTimestamp();
    }
    
    public CandlestickConverter(String url, String login, String password, long lastTimestamp) throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        this.connection = DriverManager.getConnection (url, login, password);
        this.lastTimestamp = lastTimestamp - lastTimestamp % PERIODDURATION;
    }
    
    private void updateLastTimestamp() throws SQLException{
        Statement stmt = this.connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT timestamp FROM candlestick ORDER BY timestamp DESC LIMIT 1");
        long prevLastTs = this.lastTimestamp;
        if(rs.isBeforeFirst()){
            rs.next();
            this.lastTimestamp = rs.getLong("timestamp");
        }else{
            this.lastTimestamp = 0;
        }
        if(prevLastTs != lastTimestamp){
            System.out.println("Last timestamp updated: " + this.lastTimestamp + ".");
        }
        stmt.close();
    }
    
    private LinkedList<OnePeriodData> getListFromDatabase(int pair, int stock) throws SQLException{
        LinkedList<OnePeriodData> ll = new LinkedList<OnePeriodData>();
        long curTimestamp = System.currentTimeMillis()/1000;
        curTimestamp -= curTimestamp % PERIODDURATION;
        String query = "SELECT * FROM orders WHERE timestamp > " +
                (lastTimestamp + PERIODDURATION) + " AND timestamp <= " +
                curTimestamp + " AND pair = " + pair + " AND stock = " + stock + " ORDER BY timestamp ASC";
        Statement stmt = this.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        OnePeriodData curPeriod = new OnePeriodData();
        if(rs.isBeforeFirst()){
            rs.next();
            curTimestamp = rs.getLong("timestamp");
            curTimestamp -= curTimestamp % PERIODDURATION;
            curPeriod.open = curPeriod.high = curPeriod.low = rs.getDouble("price");
            rs.beforeFirst();
        }else{
            return ll;
        }
        curPeriod.timestamp = curTimestamp;
        double prevPrice = 0;
        while(!rs.isLast()){
            rs.next();
            if(curTimestamp + PERIODDURATION < rs.getLong("timestamp")){
                curPeriod.close = prevPrice;
                prevPrice = 0;
                if(curPeriod.volume != 0){
                    ll.add(curPeriod);
                }
                curPeriod = new OnePeriodData();
                while(curTimestamp + PERIODDURATION < rs.getLong("timestamp")){
                    curTimestamp += PERIODDURATION;
                }
                curPeriod.timestamp = curTimestamp;
                curPeriod.open = curPeriod.high = curPeriod.low = rs.getDouble("price");
            }
            curPeriod.volume += rs.getDouble("total");
            if(rs.getDouble("price") > curPeriod.high){
                curPeriod.high = rs.getDouble("price");
            }
            if(rs.getDouble("price") < curPeriod.low){
                curPeriod.low = rs.getDouble("price");
            }
            prevPrice = rs.getDouble("price");
        }
        curPeriod.close = prevPrice;
        if(curPeriod.volume != 0){
            ll.add(curPeriod);
        }
        rs.close();
        stmt.close();
        return ll;
    }
    
    private void saveListToDatabase(LinkedList<OnePeriodData> ll, int pair, int stock) throws SQLException{
        ListIterator<OnePeriodData> iter = ll.listIterator();
        String candleSql = "INSERT INTO `candlestick` (`pair`, `stock`, `timestamp`, `open`, `high`, `low`, `close`, `volume`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = this.connection.prepareStatement(candleSql);
        while(iter.hasNext()){
            OnePeriodData curPeriod = iter.next();
            ps.setInt(1, pair);
            ps.setInt(2, stock);
            ps.setLong(3, curPeriod.timestamp);
            ps.setDouble(4, curPeriod.open);
            ps.setDouble(5, curPeriod.high);
            ps.setDouble(6, curPeriod.low);
            ps.setDouble(7, curPeriod.close);
            ps.setDouble(8, curPeriod.volume);
            ps.executeUpdate();
        }
        if(!ll.isEmpty()){
            System.out.println(ll.size() + " candles added to database for pair " + pair + " and stock " + stock + ".");
        }
        ps.close();
    }
    
    @Override
    public void run() {
        while (this.isInterrupted() == false) {
            for (int i = 1; i <= 4; i++) {
                for (int j = 1; j <= 4; j++) {
                    try {
                        LinkedList<OnePeriodData> ll = getListFromDatabase(i, j);
                        //System.out.println("i=" + i + "; j=" + j + "; size = " + ll.size() + ";");
                        saveListToDatabase(ll, i, j);
                    } catch (SQLException ex) {
                        Logger.getLogger(CandlestickConverter.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
            try {
                updateLastTimestamp();
            } catch (SQLException ex) {
                Logger.getLogger(CandlestickConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                sleep(PERIODDURATION * 1000 - (System.currentTimeMillis() % PERIODDURATION * 1000) + 1000);
            } catch (InterruptedException ex) {
                this.interrupt();
            }
        }
    }
}
