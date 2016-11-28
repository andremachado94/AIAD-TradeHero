import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by andremachado on 26/10/2016.
 */
public class StockDataFetcher {


    /*
	* Returns a Stock Object that contains info about a specified stock.
	* @param 	symbol the company's stock symbol
	* @return 	a stock object containing info about the company's stock
	* @see Stock
	*/
    static Stock getStockData(String symbol) {

        String companyID = symbol;

        String companyName = new String();  //n
        String currency = new String();     //c4

        double ask;                         //a
        double bid;                         //b

        int askSize;                        //a5
        int bidSize;                        //b6

        double bookValue;                   //b4
        double marketValue;

        double dividendPerShare;            //d
        double eps;                         //e - Earnings Per Share
        double epsEstimateCurYear;          //e7
        double epsEstimateNextYear;         //e8
        double epsEstimateNextQuarter;      //e9

        double sma50;                       //m3 - 50day moving average
        double sma200;                      //m4 - 200day moving average

        try {

            // Retrieve CSV File

            URL yahoo = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + companyID + "&f=nc4aba5b6b4b4dee7e8e9m3m4");
            URLConnection connection = yahoo.openConnection();
            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(is);

            // Parse CSV Into Array
            String line = br.readLine();
            //Only split on commas that aren't in quotes
            String[] stockinfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            System.out.println(line);
            // Handle Our Data
            StockDataHandler handler = new StockDataHandler();

            companyName = stockinfo[0].replace("\"", "");

            if(companyName.equals("N/A")){
                System.out.println("Company ID does not exist");
                return null;
            }

            currency = stockinfo[1].replace("\"", "");

            ask = handler.handleDouble(stockinfo[2]);
            bid = handler.handleDouble(stockinfo[3]);

            askSize = handler.handleInt(stockinfo[4]);
            bidSize = handler.handleInt(stockinfo[5]);

            bookValue = handler.handleDouble(stockinfo[6]);
            marketValue = handler.handleDouble(stockinfo[7]);

            dividendPerShare = handler.handleDouble(stockinfo[8]);
            eps = handler.handleDouble(stockinfo[9]);
            epsEstimateCurYear = handler.handleDouble(stockinfo[10]);
            epsEstimateNextYear = handler.handleDouble(stockinfo[11]);
            epsEstimateNextQuarter = handler.handleDouble(stockinfo[12]);

            sma50 = handler.handleDouble(stockinfo[13]);
            sma200 = handler.handleDouble(stockinfo[14]);

        } catch (IOException e) {
            //Logger log = Logger.getLogger(StockDataFetcher.class.getName());
            //log.log(Level.SEVERE, e.toString(), e);
            return null;
        }

        //return new Stock();

        return new Stock(companyID, companyName, currency, ask, bid, askSize, bidSize, bookValue, marketValue, dividendPerShare, eps, epsEstimateCurYear, epsEstimateNextYear, epsEstimateNextQuarter, sma50, sma200);
    }
}
