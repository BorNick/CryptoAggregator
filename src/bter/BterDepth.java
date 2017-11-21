package bter;

import base.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
//import org.codehaus.jackson.annotate.JsonProperty;
//import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder({
    "result",
    "asks",
    "bids"
})
public class BterDepth {

    @JsonProperty("result")
    String result;

    @JsonProperty("asks")
    List<List<Double>> asks = new ArrayList<>();

    @JsonProperty("bids")
    List<List<Double>> bids = new ArrayList<>();

    /**
     * Converts this class object into BaseDepth object
     *
     * @return BaseDepth object
     */
    public BaseDepth toBaseDepth() {
        BaseDepth res = new BaseDepth();

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
