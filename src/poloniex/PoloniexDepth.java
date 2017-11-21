package poloniex;

import java.util.Date;
import java.util.ArrayList;
import base.BaseDepth;
import base.BaseDepthItem;
import base.AbstractAggregator.Properties;

public class PoloniexDepth {

    /**
     *
     */
    public String[][] asks;

    /**
     *
     */
    public String[][] bids;

    /**
     *
     */
    public String isFrozen;

    /**
     *
     */
    public long seq;

    /**
     *
     */
    public long timestamp;

    /**
     *
     */
    public void setTimestamp() {
        this.timestamp = new Date().getTime() / 1000;
    }

    /**
     * To convert to a BaseDepth object
     * @param properties To see AbstractAggregator.Properties
     * @return The BaseDepth object
     */
    public BaseDepth getBaseDepth(Properties properties) {
        ArrayList<BaseDepthItem> asks = new ArrayList(), bids = new ArrayList();
        for (String[] i : this.asks) {
            asks.add(new BaseDepthItem(Double.parseDouble(i[0]), Double.parseDouble(i[1])));
        }
        for (String[] i : this.bids) {
            bids.add(new BaseDepthItem(Double.parseDouble(i[0]), Double.parseDouble(i[1])));
        }
        BaseDepth baseDepth = new BaseDepth();
        baseDepth.typeStock = properties.typeStock;
        baseDepth.typePair = properties.typePair;
        baseDepth.timestamp = this.timestamp;
        baseDepth.asks = asks;
        baseDepth.bids = bids;
        return baseDepth;
    }
}
