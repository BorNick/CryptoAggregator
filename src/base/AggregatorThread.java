package base;

import base.AbstractAggregator.Properties;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 */
public class AggregatorThread extends Thread {

    /**
     * The link of a AbstractAggregator child
     */
    protected AbstractAggregator aggregator;

    /**
     *
     * @param aggregator The link of a AbstractAggregator child
     */
    public AggregatorThread(AbstractAggregator aggregator) {
        this.aggregator = aggregator;
    }

    /**
     * The run function is executed into a separate thread. 
     * This function calls the process function of the AbstractAggregator and saves 
     * a new data to the data base. The time delay between processing is set with help
     * of AbstractAggregator Properties class.
     */
    @Override
    public void run() {
        Properties properties = this.aggregator.getProperties();
        while (this.isInterrupted() == false) {
            long t = new Date().getTime();
            try {
                this.aggregator.process();
                this.aggregator.saveToBase();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            t = new Date().getTime() - t;

            if (t < properties.timeDelay) {
                try {
                    Thread.sleep(properties.timeDelay - t);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
