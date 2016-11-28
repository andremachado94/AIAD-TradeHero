import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
 * Created by andremachado on 26/10/2016.
 */
public class HistoricDataDownloader {
    //http://chart.finance.yahoo.com/table.csv?s=IBM&a=9&b=7&c=2011&d=9&e=7&f=2016&g=d&ignore=.csv

    public HistoricDataDownloader() {

        String csvDir = "src/MarketCaps.csv";
        BufferedReader br = null;
        String marketCapLine = "";
        String csvSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(csvDir));
            int i=0;
            while((marketCapLine = br.readLine()) != null){
                i++;
                String[] marketCapData = marketCapLine.split(csvSplitBy);

                System.out.println("Exporting file number\t" + i);

                Calendar start = new GregorianCalendar(1800, 1, 1);
                Calendar end = new GregorianCalendar(2020, 1, 1);

                String url = "http://chart.finance.yahoo.com/table.csv?s=" + marketCapData[0] +
                        "&a=" + start.get(Calendar.MONTH) +
                        "&b=" + start.get(Calendar.DAY_OF_MONTH) +
                        "&c=" + start.get(Calendar.YEAR) +
                        "&d=" + end.get(Calendar.MONTH) +
                        "&e=" + end.get(Calendar.DAY_OF_MONTH) +
                        "&f=" + end.get(Calendar.YEAR) +
                        "&g=d&ignore=.csv";


                try {
                    URL yahooFin = new URL(url);
                    URLConnection data = yahooFin.openConnection();
                    Scanner input = new Scanner(data.getInputStream());

                    String csv = "resources/historicalData/daily/" + marketCapData[0] + "_dailyInfo.csv";

                    //try {
                        FileWriter wr = new FileWriter(csv);
                   // }
                    //catch (Exception e){
                     //   System.err.println("ERR: " + e);
                    //}


                    if (input.hasNext())
                        input.nextLine();

                    while (input.hasNextLine()) {
                        String line = input.nextLine();

                        try {
                            wr.append(line);
                            wr.append("\n");
                        }
                        catch (Exception e){
                            System.err.println("ERR: " + e);
                        }

                    }

                } catch (Exception e) {
                    System.err.println("ERR: " + e);
                }


            }

            br.close();

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }


    }
}
