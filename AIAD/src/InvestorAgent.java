import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by andremachado on 01/12/2016.
 */
public class InvestorAgent extends jade.core.Agent {
    private ArrayList<Double> currentCapitalHistory = new ArrayList<>();
    private ArrayList<Double> portfolioValueHistory = new ArrayList<>();

    protected InvestmentChart chart;

    protected double day = 0;


    protected boolean infoValid = false;

    protected int totalMA = 1;
    protected int capitMA = 1;
    protected int investMA = 1;

    protected int investAmount = 100;

    protected void updateHistory(double currentCapital, double portfolioValue){
        if(currentCapitalHistory.size() == 300){
            currentCapitalHistory.remove(299);
        }
        if(portfolioValueHistory.size() == 300){
            portfolioValueHistory.remove(299);
        }

        currentCapitalHistory.add(0,currentCapital);
        portfolioValueHistory.add(0,portfolioValue);
    }

    protected double getPortfolioValueHistoryMA(int num) {
        int i;
        double sum = 0;
        for(i = 0 ; i < portfolioValueHistory.size() && i < num ; i++){
            sum += portfolioValueHistory.get(i);
        }
        return sum/i;
    }

    protected double getCurrentCapitalHistoryMA(int num) {
        int i;
        double sum = 0;
        for(i = 0 ; i < currentCapitalHistory.size() && i < num ; i++){
            sum += currentCapitalHistory.get(i);
        }
        return sum/i;
    }


    protected Date stringToDate(String info) {
        String[] dateInfo = info.split("-");

        return new GregorianCalendar(
                Integer.parseInt(dateInfo[0]),
                Integer.parseInt(dateInfo[1]),
                Integer.parseInt(dateInfo[2])
        ).getTime();
    }

    protected String dateToString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);

    }


}
