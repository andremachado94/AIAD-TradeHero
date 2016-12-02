/**
 * Created by andremachado on 17/11/2016.
 */
public class Investment {

    public int investAmount(int type, Company company, double investMoney){

        if(company == null || !company.haveEnoughInfo())
            return -1;

        switch (type){
            case 0:
                return RSIInvestmentBuy(company, investMoney);
            case 1:
                return MACDInvestmentBuy(company, investMoney);
            case 2:
                break;
            default:
                return -1;
        }

        return -1;
    }

    private double getRSI(Company company){
        double avarageLosses14Days = company.getAvarageLoss(14);
        double avarageGains14Days = company.getAvarageGain(14);


        if(avarageLosses14Days == 0)
            return 100;
        else{
            double relativeStrength = avarageGains14Days / avarageLosses14Days;
            return 100 - (100/(1+relativeStrength));
        }

    }

    public boolean shouldSell(int type, Company company){
        if(company == null || !company.haveEnoughInfo())
            return false;

        switch (type){
            case 0:
                return RSIInvestmentSell(company);
            case 1:
                return MACDInvestmentSell(company);
            case 2:
                break;
            default:
                return false;
        }

        return false;
    }

    private int MACDInvestmentBuy(Company company, double investMoney) {

        double signalLineToday = company.getSignal();
        double signalLineYesterday = company.getPreviousSignal();

        double macdToday = company.getMACD();
        double macdYesterday = company.getPreviousMACD();

        double histogramToday = macdToday - signalLineToday;
        double histogramYesterday = macdYesterday -signalLineYesterday;

        if(histogramToday > 0 && histogramYesterday < 0){
            double shareValue = company.getLastClose();
            return (int)(investMoney / shareValue);
        }
        return  0;
    }

    private boolean MACDInvestmentSell(Company company) {
        double signalLineToday = company.getSignal();
        double signalLineYesterday = company.getPreviousSignal();

        double macdToday = company.getMACD();
        double macdYesterday = company.getPreviousMACD();

        double histogramToday = macdToday - signalLineToday;
        double histogramYesterday = macdYesterday -signalLineYesterday;

        if(histogramToday < 0 && histogramYesterday >0)
            return true;
        return false;


    }


    private int RSIInvestmentBuy(Company company, double investMoney){

        /*
        double movAva200 = company.getMA(200);
        double movAva50 = company.getMA(50);
        double high200 = company.getHighs(200);
        double high50 = company.getHighs(50);
        double low200 = company.getLows(200);
        double low50 = company.getLows(50);
        double todayClose = company.getLastClose();
        */

        double relativeStrengthIndex = getRSI(company);

        if(relativeStrengthIndex < 35){
            double shareValue = company.getLastClose();
            return (int)(investMoney / shareValue);

        }
        else
            return 0;
    }

    private boolean RSIInvestmentSell(Company company){

        double relativeStrengthIndex = getRSI(company);

        if(relativeStrengthIndex > 55)
            return true;
        else return false;

    }

}
