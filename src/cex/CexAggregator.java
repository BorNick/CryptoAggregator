package cex;

import base.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
//import org.codehaus.jackson.map.ObjectMapper;

public class CexAggregator extends AbstractAggregator {

    private String depthUrl = "https://cex.io/api/order_book/";
    private String orderUrl = "https://cex.io/api/trade_history/";

    /**
     * Constructor
     *
     * @param properties
     * @param lastOrderId
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public CexAggregator(Properties properties, long lastOrderId) throws ClassNotFoundException, SQLException {
        super(properties, lastOrderId);
        super.properties.typeStock = AbstractAggregator.Stocks.CEXIO;
        depthUrl += this.convertPair(properties.typePair);
        orderUrl += this.convertPair(properties.typePair);
    }
    
    /**
     * Constructor
     *
     * @param properties
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public CexAggregator(Properties properties) throws ClassNotFoundException, SQLException {
        super(properties, AbstractAggregator.Stocks.CEXIO);
        super.properties.typeStock = AbstractAggregator.Stocks.CEXIO;
        depthUrl += this.convertPair(properties.typePair);
        orderUrl += this.convertPair(properties.typePair);
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
            case 2:
                return "BTC/USD";
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
        String url = this.orderUrl + "/?since=" + id;
        String response = HttpRequest.send(url, "GET");
        List<BaseOrder> res = new ArrayList<>();
        CexOrder[] cexOrder = (CexOrder[]) this.getObjectMapper().readValue(response, CexOrder[].class);
        for (CexOrder i : cexOrder) {
            BaseOrder baseOrder = i.toBaseOrder();
            baseOrder.typePair = properties.typePair;
            baseOrder.typeStock = properties.typeStock;
            res.add(baseOrder);
        }
        super.setOrders(res);
        if (res.size() > 0) {
            //BaseOrder last = res.get(res.size() - 1);
            //return last.id + 1;
            BaseOrder first = res.get(0);
            return first.id + 1;
        } else {
            return id;
        }
    }

    /**
     * Does request to server to get depth
     *
     * @throws java.io.IOException
     */
    @Override
    public void depthRequest() throws IOException {
        String response = HttpRequest.send(this.depthUrl, "GET");
        CexDepth cexDepth = (CexDepth) this.getObjectMapper().readValue(response, CexDepth.class);
        BaseDepth res = cexDepth.toBaseDepth();
        res.typePair = properties.typePair;
        res.typeStock = properties.typeStock;
        super.setDepth(res);
    }

}
