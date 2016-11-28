import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by andremachado on 28/10/2016.
 */
public class Share {

    private boolean active;
    private Company company;
    private int amount;
    private double boughtPrice;
    private double soldPrice;
    private double currentPrice;
    private Date dateBought;
    private Date dateSold;



    public Share(Company company, int amount, double sharePrice, Date date){
        this.company = company;
        this.amount = amount;
        this.boughtPrice = sharePrice;
        this.currentPrice = sharePrice;
        this.active = true;

        this.dateBought = date;
    }


    public void update(){
        this.currentPrice = company.getLastClose();
    }

    public int getAmount() {
        return amount;
    }

    public double getSharePrice() {
        return currentPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void sell(Date date) {
        this.active = false;
        this.dateSold = date;
    }

    public double getShareCost() {
        return amount*currentPrice;
    }

    public double getShareCurrentCost(){
        return currentPrice;
    }
}
