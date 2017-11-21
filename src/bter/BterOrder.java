package bter;

import base.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

@JsonPropertyOrder({
    "tradeID",
    "date",
    "type",
    "rate",
    "amount",
    "total"
})
public class BterOrder extends BaseOrder {

    @JsonProperty("tradeID")
    int tradeId;

    @JsonProperty("date")
    String date;

    @JsonProperty("type")
    String type;

    @JsonProperty("rate")
    double rate;

    @JsonProperty("amount")
    double amount;

    @JsonProperty("total")
    double total;

    /**
     * Converts this class object into BaseOrder object
     *
     * @return BaseOrder object
     */
    public BaseOrder toBaseOrder() {
        BaseOrder res = new BaseOrder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        Date parseDate = null;
        try {
            parseDate = simpleDateFormat.parse(this.date);
        } catch (java.text.ParseException err) {
            err.printStackTrace();
        }
        res.timestamp = parseDate.getTime() / 1000;
        res.amount = this.amount;
        res.id = (long) this.tradeId;
        res.price = this.rate;
        if ("buy".equals(type)) {
            res.typeOrder = AbstractAggregator.OrderTypes.BUY;
        } else if ("sell".equals(type)) {
            res.typeOrder = AbstractAggregator.OrderTypes.SELL;
        }
        return res;
    }
}
