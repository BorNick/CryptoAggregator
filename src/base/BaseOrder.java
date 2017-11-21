package base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseOrder {

    /**
     * The order identifier
     */
    public long id;

    /**
     * The order timestamp
     */
    public long timestamp;

    /**
     * The currency pair from AbstractAggregator.Pairs
     */
    public int typePair;

    /**
     * The type of stock from AbstractAggregator.Stocks
     */
    public int typeStock;

    /**
     * The type of order from AbstractAggregator.OrderTypes
     */
    public int typeOrder;

    /**
     * The amount of currency
     */
    public double amount;

    /**
     * The currency price
     */
    public double price;

    /**
     * To get string with a json view of BaseOrder object
     * 
     * @param mapper The ObjectMapper object which is needed for serializing
     * @return The string with a json view
     * @throws JsonProcessingException
     */
    public String toJson(ObjectMapper mapper) throws JsonProcessingException {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper.writeValueAsString(this);
    }

    /**
     *
     * @return
     */
    public String toString() {
        return "id = " + this.id
                + "; timestamp = " + this.timestamp
                + "; typePair = " + this.typePair
                + "; typeStock = " + this.typeStock
                + "; typeOrder = " + this.typeOrder
                + "; amount = " + this.amount
                + "; price = " + this.price + ";";
    }
}
