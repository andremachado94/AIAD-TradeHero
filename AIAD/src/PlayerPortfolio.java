import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by andremachado on 28/11/2016.
 */
public class PlayerPortfolio {

    private static double initialCapital = 1000.00;

    private static String currencyUsed = "USD";
    private double currentCapital;
    private HashMap<String,Share> activeShares = new HashMap<String,Share>();
    private ArrayList<Share> soldShares = new ArrayList<Share>();



    public PlayerPortfolio(){
        currentCapital = initialCapital;
    }


    public void buyShare(String cmpId, double price, Date date){
        int n = (int)(currentCapital/(price*100));
        System.out.println("Buy " + cmpId);

        if(!activeShares.containsKey(cmpId)){
            if(n>0) {
                Share share = new Share(cmpId, n, price, date);
                activeShares.put(cmpId, share);
                currentCapital -= n*price;
            }
        }
    }

    public void update(String key, double val){
        if(activeShares.containsKey(key)) {
            activeShares.get(key).update(val);
        }
    }

    public void sellShare(String cmpId, double price, Date date){
        System.out.println("Sell " + cmpId);
        if(activeShares.containsKey(cmpId)){
            Share share = activeShares.get(cmpId);
            share.sell(date);
            share.update(price);
            currentCapital += share.getAmount()*price;
            soldShares.add(share);
            activeShares.remove(cmpId);
        }
    }

    public double getPortfolioValue(){
        double sum = 0.0;

        for (String key: activeShares.keySet()) {
            sum += activeShares.get(key).getShareCost();
        }
        return sum;
    }

    public double getCurrentCapital(){
        return currentCapital;
    }


}
