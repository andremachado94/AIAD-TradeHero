import java.util.regex.Pattern;

/**
 * Created by andremachado on 26/10/2016.
 */

public class StockDataHandler {

    public StockDataHandler() {}

    public double handleDouble(String str) {
        double val;
        if (Pattern.matches("N/A", str)) {
            val = -1.00;
        } else {
            val = Double.parseDouble(str);
        }
        return val;
    }

    public int handleInt(String str) {
        int val;
        if (Pattern.matches("N/A", str)) {
            val = -1;
        } else {
            val = Integer.parseInt(str);
        }
        return val;
    }


}
