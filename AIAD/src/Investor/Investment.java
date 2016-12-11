package Investor;

import Shared.Company;

import java.util.Random;


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
                return MACDInvestmentBuy(company, investMoney);
            case 3:
                return RandomInvestmentBuy(company, investMoney);
            default:
                return -1;
        }

    }

    private double getHistogramSlope(Company company){
        double sumx=0;
        double sumy=0;
        double sumxSq=0;
        double sumxy=0;
        for(int x = 1 ; x < 4 ; x++){
            double y = company.getHistogram(x-1);
            sumy += y;
            sumx += x;
            sumxSq += x*x;
            sumxy += x*y;
        }

        return (6*sumxy - sumx*sumy)/(6*sumxSq - sumx*sumx);
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

    public boolean shouldSell(int type, Company company, double boughtPrice){
        if(company == null || !company.haveEnoughInfo())
            return false;

        switch (type){
            case 0:
                return RSIInvestmentSell(company);
            case 1:
                return MACDInvestmentSell(company);
            case 2:
                return SafeMACDInvestmentSell(company, boughtPrice);
            case 3:
                return RandomInvestmentSell();
            default:
                return false;
        }

    }

    private int MACDInvestmentBuy(Company company, double investMoney) {

        double signalLineToday = company.getSignal();

        double macdToday = company.getMACD();

        double histogramToday = macdToday - signalLineToday;

        double slope = getHistogramSlope(company);
        if(slope >= -0.05 && slope <= 0.05 && histogramToday < 0){
            double shareValue = company.getLastClose();
            return (int)(investMoney / shareValue);
        }
        return  0;
    }

    private boolean MACDInvestmentSell(Company company) {
        double signalLineToday = company.getSignal();

        double macdToday = company.getMACD();

        double histogramToday = macdToday - signalLineToday;

        double slope = getHistogramSlope(company);

        if(slope >= -0.05 && slope <= 0.05 && histogramToday >= 0)
            return true;
        return false;


    }

    private int RandomInvestmentBuy(Company company, double investMoney) {

        Random rn = new Random();
        int n = rn.nextInt(10);



        if(n == 0){
            double shareValue = company.getLastClose();
            return (int)(investMoney / shareValue);
        }
        return  0;
    }

    private boolean RandomInvestmentSell() {
        Random rn = new Random();
        int n = rn.nextInt(100);

        if(n == 0)
            return true;
        return false;


    }

    private boolean SafeMACDInvestmentSell(Company company, double boughtPrice) {
        double signalLineToday = company.getSignal();

        double macdToday = company.getMACD();

        double histogramToday = macdToday - signalLineToday;

        double slope = getHistogramSlope(company);

        if(slope >= -0.05 && slope <= 0.05 && histogramToday <= 0 && company.getLastClose() > boughtPrice)
            return true;
        return false;


    }

    private int RSIInvestmentBuy(Company company, double investMoney){

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
