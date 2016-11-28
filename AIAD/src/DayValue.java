/**
 * Created by andremachado on 07/11/2016.
 */
public class DayValue {
    private double closePrice;
    private double dayHigh;
    private double dayLow;
    private int volume;

    public DayValue(){

    }


    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(double dayHigh) {
        this.dayHigh = dayHigh;
    }

    public double getDayLow() {
        return dayLow;
    }

    public void setDayLow(double dayLow) {
        this.dayLow = dayLow;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getData() {
        return closePrice + "," + dayHigh + "," +dayLow + "," +volume;
    }
}
