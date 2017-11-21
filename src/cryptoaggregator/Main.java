package cryptoaggregator;

import base.AbstractAggregator;
import base.AbstractAggregator.Properties;
import base.AggregatorThread;
import bter.BterAggregator;
import cex.CexAggregator;
import com.sun.org.apache.xml.internal.security.encryption.AgreementMethod;
import converter.CandlestickConverter;
import java.util.LinkedList;
import java.util.Scanner;
import poloniex.PoloniexAggregator;

public class Main {
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Properties cexProperties = new Properties(AbstractAggregator.Pairs.BTC_USD, 5000);
        //Properties polProperties = new Properties(AbstractAggregator.Pairs.BTC_USD, 5000);
        //properties.dataBasePassword = "root";
        cexProperties.dataBaseName = "trading2";
        //polProperties.dataBaseName = "trading2";
        //PoloniexAggregator pa = new PoloniexAggregator(polProperties);
        CexAggregator ca = new CexAggregator(cexProperties);
        //AggregatorThread atPol = new AggregatorThread(pa);
        AggregatorThread atCex = new AggregatorThread(ca);
        CandlestickConverter cc = new CandlestickConverter("jdbc:mysql://localhost:3306/trading2", "root", "");
        
        //atPol.start();
        atCex.start();
        Thread.sleep(1000);
        cc.start();
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Aggregator started working\nType @help for help");
        while (true) {
            String s = scanner.nextLine();
            if (s.equals("@stop")) {
                break;
            }
            if (s.charAt(0) == '@') {
                switch (s){
                    case "@help":
                        System.out.println("Commands:\n@help - help message\n"
                                + "@stop - stop work of the aggregator\n"
                                + "@restartConverter - interrupts converter thread and starts it again");
                        break;
                    case "@restartConverter":
                        cc.interrupt();
                        cc.join();
                        cc = new CandlestickConverter("jdbc:mysql://localhost:3306/trading2", "root", "");
                        cc.start();
                        break;
                    default:
                        System.out.println("Unknown command");
            }
            }
        }
        
        atCex.interrupt();
        //atPol.interrupt();
        atCex.join();
        //atPol.join();
        cc.interrupt();
        cc.join();
        
        System.out.println("the end!!!");
    }
}
