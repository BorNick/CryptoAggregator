package poloniex;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import base.AbstractAggregator.OrderTypes;
import base.AbstractAggregator.Properties;
import base.BaseOrder;
import com.fasterxml.jackson.core.type.TypeReference;

public class PoloniexOrder {

    /**
     *
     */
    public static final TypeReference<ArrayList<PoloniexOrder>> ArrayListType = new TypeReference<ArrayList<PoloniexOrder>>(){};

    /**
     *
     */
    public long globalTradeID;

    /**
     *
     */
    public long tradeID;

    /**
     *
     */
    public String date = "";

    /**
     *
     */
    public String type = "";

    /**
     *
     */
    public double rate;

    /**
     *
     */
    public double amount;

    /**
     *
     */
    public double total;

    /**
     * To convert to a BaseOrder object
     * @param properties To see AbstractAggregator.Properties
     * @return The BaseOrder object
     */
    public BaseOrder getBaseOrder(Properties properties) {
        int typeOrder = 0;
        if (this.type.equals("buy")) {
            typeOrder = OrderTypes.BUY;
        } else if (this.type.equals("sell")) {
            typeOrder = OrderTypes.SELL;
        } else {
            throw new RuntimeException("unknown type order");
        }
        long timestamp = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = dateFormat.parse(this.date);
            timestamp = date.getTime() / 1000;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        BaseOrder bo = new BaseOrder();
        bo.typeStock = properties.typeStock;
        bo.typePair = properties.typePair;
        bo.typeOrder = typeOrder;
        bo.id = this.globalTradeID;
        bo.timestamp = timestamp;
        bo.amount = this.amount;
        bo.price = this.rate;
        return bo;
    }
}
