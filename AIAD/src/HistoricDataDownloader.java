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

        String csvDir = "resources/caps/MarketCaps2.csv";
        BufferedReader br = null;
        String marketCapLine = "";
        String csvSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(csvDir));
            int i=0;
            while((marketCapLine = br.readLine()) != null) {
                i++;
                String[] marketCapData = marketCapLine.split(csvSplitBy);

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

                String splitInfoUrl = "http://chart.finance.yahoo.com/x?s=" + marketCapData[0] +
                        "&a=" + start.get(Calendar.MONTH) +
                        "&b=" + start.get(Calendar.DAY_OF_MONTH) +
                        "&c=" + start.get(Calendar.YEAR) +
                        "&d=" + end.get(Calendar.MONTH) +
                        "&e=" + end.get(Calendar.DAY_OF_MONTH) +
                        "&f=" + end.get(Calendar.YEAR) +
                        "&g=v&y=0&z=30000&ignore=.csv";

                boolean hasSplit = false;

                try {
                    URL yahooFinSplit = new URL(splitInfoUrl);
                    URLConnection splitData = yahooFinSplit.openConnection();
                    Scanner input = new Scanner(splitData.getInputStream());


                    if (input.hasNext())
                        input.nextLine();

                    while (input.hasNextLine()) {
                        String line = input.nextLine();
                        String[] info = line.split(",");

                        if (info.length > 0) {
                            if (info[0].equals("SPLIT")) {
                                hasSplit = true;
                                break;
                            }
                        }

                    }

                    input.close();


                } catch (Exception e) {
                    System.err.println("ERR: " + e);
                }


                if (!hasSplit)
                {

                    System.out.println("Exporting file number\t" + i);

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
                            } catch (Exception e) {
                                System.err.println("ERR: " + e);
                            }

                        }

                        wr.close();

                    } catch (Exception e) {
                        System.err.println("ERR: " + e);
                    }
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
