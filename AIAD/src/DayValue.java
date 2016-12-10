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

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public void setDayHigh(double dayHigh) {
        this.dayHigh = dayHigh;
    }

    public void setDayLow(double dayLow) {
        this.dayLow = dayLow;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getData() {
        return closePrice + "," + dayHigh + "," +dayLow + "," +volume;
    }
}
