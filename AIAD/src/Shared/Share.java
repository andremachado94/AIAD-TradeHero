package Shared;

import Shared.Company;

import java.util.Date;

/**
 * Created by andremachado on 28/10/2016.
 */
public class Share {


    private Company company;
    private String companyId;
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

        this.dateBought = date;
    }

    public Share(String companyId, int amount, double sharePrice, Date date){
        this.companyId = companyId;
        this.amount = amount;
        this.boughtPrice = sharePrice;
        this.currentPrice = sharePrice;


        this.dateBought = date;
    }


    public void update(double price){
        this.currentPrice = price;
    }

    public void update(){
        this.currentPrice = company.getLastClose();
    }

    public int getAmount() {
        return amount;
    }

    public void sell(Date date) {
        this.dateSold = date;
    }

    public double getShareCost() {
        return amount*currentPrice;
    }

    public double getBoughtPrice() {
        return boughtPrice;
    }
}
