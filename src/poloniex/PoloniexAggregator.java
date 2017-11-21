package poloniex;

import java.util.ArrayList;
import java.sql.SQLException;
import java.io.IOException;
import base.AbstractAggregator;
import base.HttpRequest;
import base.BaseDepth;
import base.BaseOrder;

public class PoloniexAggregator extends AbstractAggregator {

    /**
     * The url address for getting a trade history
     */
    protected String urlGetTradeHistory;

    /**
     * The url address for getting a order book
     */
    protected String urlGetOrderBook;

    /**
     * To return the url address for getting trade history.
     * In the first call, the function builds url address string and saves it into a local field.
     * In the next calls, the function returns value from a local field.
     * @return The string with a url address for getting a trade history
     */
    protected String getUrlGetTradeHistory() {
        if (this.urlGetTradeHistory == null) {
            this.urlGetTradeHistory = "https://poloniex.com/public?command=returnTradeHistory&currencyPair=" + this.convertPair(this.properties.typePair);
        }
        return this.urlGetTradeHistory;
    }

    /**
     * To return the url address for getting a order book.
     * In the first call, the function builds url address string and saves it into a local field.
     * In the next calls, the function returns value from a local field.
     * @return The string with a url address for getting a order book
     */
    protected String getUrlGetOrderBook() {
        if (this.urlGetOrderBook == null) {
            this.urlGetOrderBook = "https://poloniex.com/public?command=returnOrderBook&depth=1000000&currencyPair=" + this.convertPair(this.properties.typePair);
        }
        return this.urlGetOrderBook;
    }

    /**
     * see constructor of the AbstractAggregator
     * @param properties
     * @param lastOrderId
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public PoloniexAggregator(Properties properties, long lastOrderId) throws ClassNotFoundException, SQLException {
        super(properties, lastOrderId);
        this.properties.typeStock = AbstractAggregator.Stocks.POLONIEX;
    }
    
    /**
     * see constructor of the AbstractAggregator
     * @param properties
     * @param lastOrderId
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public PoloniexAggregator(Properties properties) throws ClassNotFoundException, SQLException {
        super(properties, AbstractAggregator.Stocks.POLONIEX);
        this.properties.typeStock = AbstractAggregator.Stocks.POLONIEX;
    }

    /**
     * see the AbstractAggregator
     * @param pair 
     * @return
     */
    @Override
    public String convertPair(int pair) {
        String r;
        switch (pair) {
            case Pairs.BTC_USD: {
                r = "USDT_BTC";
                break;
            }

            default:
                throw new RuntimeException("currency pair is not supported");
        }
        return r;
    }

    //TODO:: какое нужно число записей чтобы в результате присутствовал previousId?

    /**
     * see the AbstractAggregator
     * @param previousId
     * @return
     * @throws IOException
     */
    @Override
    public long ordersRequest(long previousId) throws IOException {
        String s = HttpRequest.send(this.getUrlGetTradeHistory(), "GET");
        ArrayList<PoloniexOrder> poloniexOrders = this.getObjectMapper().readValue(s, PoloniexOrder.ArrayListType);
        ArrayList<BaseOrder> baseOrders = new ArrayList();
        long maxId = previousId;
        for (PoloniexOrder po : poloniexOrders) {
            if (po.globalTradeID > previousId) {
                baseOrders.add(po.getBaseOrder(this.properties));
            }
            if (po.globalTradeID > maxId) {
                maxId = po.globalTradeID;
            }
        }
        this.setOrders(baseOrders);
        return maxId;
    }

    /**
     * see the AbstractAggregator
     * @throws IOException
     */
    @Override
    public void depthRequest() throws IOException {
        String s = HttpRequest.send(this.getUrlGetOrderBook(), "GET");
        PoloniexDepth pd = this.getObjectMapper().readValue(s, PoloniexDepth.class);
        pd.setTimestamp();
        BaseDepth bd = pd.getBaseDepth(this.getProperties());
        this.setDepth(bd);
    }
}
