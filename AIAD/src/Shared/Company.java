package Shared;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by andremachado on 06/10/16.
 */
public class Company{

    private String companyId;
    private Date openDate;
    private String sector;
    private ArrayList<Double> last200closes = new ArrayList<Double>();
    private ArrayList<Double> last200highs = new ArrayList<Double>();
    private ArrayList<Double> last200lows = new ArrayList<Double>();
    private ArrayList<Integer> last200volumes = new ArrayList<Integer>();
    private ArrayList<Double> signalLine = new ArrayList<Double>();
    private ArrayList<Double> macd = new ArrayList<Double>();
    private ArrayList<Double> histogram = new ArrayList<Double>();


    public Company(String companyId, Date openDate){
        this.companyId = companyId;
        this.openDate = openDate;
    }

    public void addInfo(double close, double high, double low, int volume){
        if(last200closes.size() == 200){
            last200closes.remove(199);
            last200highs.remove(199);
            last200lows.remove(199);
            last200volumes.remove(199);
        }

        if(macd.size() == 26){
            macd.remove(25);
        }

        if(signalLine.size() == 10){
            signalLine.remove(9);
        }

        if(histogram.size() == 10){
            histogram.remove(9);
        }

        last200closes.add(0, close);
        last200highs.add(0, high);
        last200lows.add(0, low);
        last200volumes.add(0, volume);

        if(last200closes.size() > 150)
            macd.add(0, getMA(12) - getMA(26));

        if(macd.size() >= 25){
            signalLine.add(0,getMACDMA(9));
        }
        if(signalLine.size() > 0)
            histogram.add(0, macd.get(0)-signalLine.get(0));
    }

    public double getMA(int n){
        double sum = 0;

        for(int i = 0 ; i<n ; i++){
            sum += last200closes.get(i);
        }
        return sum/n;
    }

    public double getMACD(int n){
        double sum = 0;

        for(int i = 0 ; i<n ; i++){
            sum += last200closes.get(i);
        }
        return sum/n;
    }

    public double getHighs(int n){
        double max = -1;

        for(int i = 0 ; i<n ; i++){
            if(last200highs.get(i) > max)
                max = last200highs.get(i);
        }
        return max;
    }

    public double getLows(int n){
        double min = 999999;

        for(int i = 0 ; i<n ; i++){
            if(last200lows.get(i) < min)
                min = last200lows.get(i);
        }
        return min;
    }

    public double getAvarageGain(int n){

        double sum = 0.0;
        int cont=0;

        for (int i=0 ; i < n ; i++){
            if(last200closes.get(i)-last200closes.get(i+1) > 0){
                cont++;
                sum += (last200closes.get(i)-last200closes.get(i+1));
            }
        }
        if(cont != 0)
            return sum/cont;
        else
            return 0.0;
    }

    public double getAvarageLoss(int n){

        double sum = 0.0;
        int cont=0;

        for (int i=0 ; i < n ; i++){
            if(last200closes.get(i+1)-last200closes.get(i) > 0){
                cont++;
                sum += (last200closes.get(i+1)-last200closes.get(i));
            }
        }
        if(cont != 0)
            return sum/cont;
        else
            return 0.0;
    }

    public boolean haveEnoughInfo(){
        if(last200highs.size() >= 200) return true;
        else return false;
    }


    public String getCompanyId() {
        return companyId;
    }

    public String getSector() {
        return sector;
    }

    public double getLastClose() {
        return last200closes.get(0);
    }

    public Double getMACDMA(int n) {
        double sum = 0;

        for(int i = 0 ; i<n ; i++){
            sum += macd.get(i);
        }
        return sum/n;
    }

    public Double getMACD() {
        return macd.get(0);
    }

    public Double getPreviousMACD() {
        return macd.get(1);
    }

    public double getSignal() {
        return signalLine.get(0);
    }
    public ArrayList<Double> getSignalLine() {
        return signalLine;
    }

    public double getPreviousSignal() {
        return signalLine.get(1);
    }

    public double getHistogram(int n){
        if(histogram.size() > n)
            return histogram.get(n);

        return  0;
    }
}
