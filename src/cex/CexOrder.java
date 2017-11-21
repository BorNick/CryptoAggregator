package cex;

import base.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({
    "type",
    "date",
    "amount",
    "price",
    "tid"
})
public class CexOrder {
    @JsonProperty ("type")
    String type;
    
    @JsonProperty ("date")
    String date;
    
    @JsonProperty ("price")
    String price;
    
    @JsonProperty ("amount")
    String amount;
    
    @JsonProperty ("tid")
    String tid;
    
    /**
     * Converts this class object into BaseOrder object
     * @return BaseOrder object
    */
    public BaseOrder toBaseOrder () {
        BaseOrder res = new BaseOrder();
        res.timestamp = Long.parseLong (this.date);
        res.amount = Double.parseDouble (this.amount);
        res.id = Long.parseLong (this.tid);
        res.price = Double.parseDouble (this.price);
        if ("buy".equals(type)) {
            res.typeOrder = AbstractAggregator.OrderTypes.BUY;
        } else if ("sell".equals(type)) {
            res.typeOrder = AbstractAggregator.OrderTypes.SELL;
        }
        return res;
    }
}
