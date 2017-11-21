package base;

import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseDepth {

    /**
     * The timestamp of a stock exchange data
     */
    public long timestamp;

    /**
     * The type of a currency pair from AbstractAggregator.Pairs
     */
    public int typePair;

    /**
     * The type of a stock exchange from AbstractAggregator.Stocks
     */
    public int typeStock;

    /**
     * The list of ask items
     */
    public List<BaseDepthItem> asks;

    /**
     * The list of bid items
     */
    public List<BaseDepthItem> bids;

    /**
     * To get a json view of BaseDepth object
     * 
     * @param mapper The ObjectMapper object which is needed for serializing
     * @return The string of a json view
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
        return "timestamp = " + this.timestamp
                + "; typePair = " + this.typePair
                + "; typeStock = " + this.typeStock
                + "; asks = " + this.asks
                + "; bids = " + this.bids + ";";
    }
}
