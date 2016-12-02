import sun.security.provider.SHA;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by andremachado on 01/11/2016.
 */
public class InvestorPortfolio extends Investment{

    private static double initialCapital = 1000.00;

    private static String currencyUsed = "USD";
    private double currentCapital;
    private HashMap<String,Share> activeShares = new HashMap<String,Share>();
    private ArrayList<Share> soldShares = new ArrayList<Share>();



    public InvestorPortfolio(){
        currentCapital = initialCapital;
    }

    public void update(){
        for (String key: activeShares.keySet()) {
            activeShares.get(key).update();
        }
    }

    public Share getShare(String companyId){
        return activeShares.containsKey(companyId) ? activeShares.get(companyId): null;
    }

    public void buyShare(Company company, int amount, Date date){
        Share share = new Share(company, amount, company.getLastClose(), date);
        if(share.getShareCost() <= currentCapital) {
            activeShares.put(company.getCompanyId(), share);
            currentCapital-=share.getShareCost();
        }
    }

    public boolean boughtShare(String companyId){
        return activeShares.containsKey(companyId);
    }

    public void sellShare(Company company, int amount, Date date){
        if(boughtShare(company.getCompanyId())) {
            //activeShares.get(company.getCompanyId());

            if(activeShares.get(company.getCompanyId()).getAmount() == amount) {
                double earnings = company.getLastClose() * amount;
                //Remove from active
                activeShares.get(company.getCompanyId()).sell(date);
                soldShares.add(activeShares.get(company.getCompanyId()));
                activeShares.remove(company.getCompanyId());
               // System.out.println("Sold 50 shares from " + company.getCompanyId() + " @" + date);
                currentCapital+=earnings;

            }
            else{
                //TODO
                System.out.println("TODO: Compras parciais");
            }

        }
    }

    public double getCurrentCapital(){
        return currentCapital;
    }

    public double getPortfolioValue(){
        double sum = 0.0;

        for (String key: activeShares.keySet()) {
            sum += activeShares.get(key).getShareCost();
        }
        return sum;
    }

    public double getInitialCapital(){
        return initialCapital;
    }



}
