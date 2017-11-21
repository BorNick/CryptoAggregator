package bter;

import base.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({
    "result",
    "data",
    "elapsed"
})
public class BterHistory {

    @JsonProperty("result")
    //@JsonIgnore
    String result;

    @JsonProperty("data")
    List<BterOrder> data = new ArrayList<>();

    @JsonProperty("elapsed")
    String elapsed;

    /**
     * Converts list of this class objects into BaseOrder objects list
     *
     * @param pairType Stock pair type
     * @return List of BaseOrder objects
     */
    public List<BaseOrder> toBaseOrderList(int pairType) {
        List<BaseOrder> res = new ArrayList<>();
        for (BterOrder i : data) {
            BaseOrder baseOrder = (BaseOrder) i.toBaseOrder();
            baseOrder.typeStock = AbstractAggregator.Stocks.BTER;
            baseOrder.typePair = pairType;
            res.add(baseOrder);
        }
        return res;
    }
}
