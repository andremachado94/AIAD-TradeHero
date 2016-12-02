import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

/**
 * Created by andremachado on 01/12/2016.
 */
public class InvestmentChart extends JFrame{

    JFreeChart chart;
    XYSeriesCollection data = new XYSeriesCollection();
    String agentName;
    String ty="Value (USD)";
    String tx="Days since 1st January 2000";


    XYSeries s = new XYSeries("Total");
    XYSeries h = new XYSeries("Holding");
    XYSeries i = new XYSeries("Invested");


    final static int LINEAR = 1;
    final static int TIMESERIES = 6;

    public InvestmentChart(String agentName){
        super(agentName);
        this.agentName = agentName;


        chartType(LINEAR);
    }

    private void chartType(int type){
        switch (type){
            case LINEAR:
                chart = ChartFactory.createXYLineChart(agentName,tx,ty,data, PlotOrientation.VERTICAL,true,true,true);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case TIMESERIES:
                chart = ChartFactory.createTimeSeriesChart(agentName,tx,ty,data,true,true,true);
                break;
            case 7:
                break;
        }
    }

    public void addData(double x, double sum, double inv, double hol){

        s.add(x, sum);
        i.add(x, inv);
        h.add(x, hol);
    }

    public JPanel getPanel(){
        data.addSeries(s);
        data.addSeries(i);
        data.addSeries(h);
        return new ChartPanel(chart);

    }

    public void openPanel(){
        JPanel panel = getPanel();
        this.setSize(600,600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);
        this.setVisible(true);
    }
}
