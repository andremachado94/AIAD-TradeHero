/**
 * Created by andremachado on 26/10/2016.
 */
public class Stock {

    private String companyID;
    private String companyName;
    private String currency;

    private double ask;
    private double bid;
    private int askSize;
    private int bidSize;

    private double bookValue;
    private double marketValue;

    private double dividendPerShare;
    private double eps;
    private double epsEstimateCurYear;
    private double epsEstimateNextYear;
    private double epsEstimateNextQuarter;

    private double sma50;   //50day moving average
    private double sma200;  //200day moving average

   //companyID, companyName, currency, ask, bid, askSize, bidSize, bookValue, marketValue, dividends, eps, epsEstimateCurYear, epsEstimateNextYear, epsEstimateNextQuarter, sma50, sma200
    public Stock(String companyID, String companyName, String currency, double ask, double bid, int askSize, int bidSize, double bookValue, double marketValue, double dividendPerShare, double eps, double epsEstimateCurYear, double epsEstimateNextYear, double epsEstimateNextQuarter, double sma50, double sma200)
    {
        this.companyID=companyID;
        this.companyName=companyName;
        this.currency=currency;
        this.ask=ask;
        this.bid=bid;
        this.askSize=askSize;
        this.bidSize=bidSize;
        this.bookValue=bookValue;
        this.marketValue=marketValue;
        this.dividendPerShare=dividendPerShare;
        this.eps=eps;
        this.epsEstimateCurYear=epsEstimateCurYear;
        this.epsEstimateNextYear=epsEstimateNextYear;
        this.epsEstimateNextQuarter=epsEstimateNextQuarter;
        this.sma50=sma50;
        this.sma200=sma200;
    }


    public String getCompanyID() {
        return companyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCurrency() {
        return currency;
    }

    public double getAsk() {
        return ask;
    }

    public double getBid() {
        return bid;
    }

    public int getAskSize() {
        return askSize;
    }

    public int getBidSize() {
        return bidSize;
    }

    public double getBookValue() {
        return bookValue;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public double getDividendPerShare() {
        return dividendPerShare;
    }

    public double getEps() {
        return eps;
    }

    public double getEpsEstimateCurYear() {
        return epsEstimateCurYear;
    }

    public double getEpsEstimateNextYear() {
        return epsEstimateNextYear;
    }

    public double getEpsEstimateNextQuarter() {
        return epsEstimateNextQuarter;
    }

    public double getSma50() {
        return sma50;
    }

    public double getSma200() {
        return sma200;
    }
}
