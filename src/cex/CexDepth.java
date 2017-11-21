package cex;

import base.BaseDepth;
import base.BaseDepthItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({
    "timestamp",
    "bids",
    "asks",
    "pair",
    "id",
    "sell_total",
    "buy_total"
})
public class CexDepth {

    @JsonProperty("timestamp")
    long timestamp;

    @JsonProperty("bids")
    List<List<Double>> bids = new ArrayList<>();

    @JsonProperty("asks")
    List<List<Double>> asks = new ArrayList<>();

    @JsonProperty("pair")
    String pair;

    @JsonProperty("id")
    long id;

    @JsonProperty("sell_total")
    String sell_total;

    @JsonProperty("buy_total")
    String buy_total;

    /**
     * Converts this class object into BaseDepth object
     *
     * @return BaseDepth object
     */
    public BaseDepth toBaseDepth() {
        BaseDepth res = new BaseDepth();

        res.timestamp = this.timestamp;

        res.asks = new ArrayList<>();
        for (List i : asks) {
            BaseDepthItem depthItem = new BaseDepthItem();
            depthItem.amount = (double) i.get(1);
            depthItem.price = (double) i.get(0);
            res.asks.add(depthItem);
        }

        res.bids = new ArrayList<>();
        for (List i : bids) {
            BaseDepthItem depthItem = new BaseDepthItem();
            depthItem.amount = (double) i.get(1);
            depthItem.price = (double) i.get(0);
            res.bids.add(depthItem);
        }

        return res;
    }
}
