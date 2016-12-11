package GUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.util.Date;

/**
 * Created by andremachado on 01/12/2016.
 */
public class InvestmentChart extends JFrame{

    JFreeChart chart;
    TimeSeriesCollection data = new TimeSeriesCollection();
    String agentName;
    String ty="Value (USD)";
    String tx="Date";


    TimeSeries s = new TimeSeries("Total");
    TimeSeries h = new TimeSeries("Holding");
    TimeSeries i = new TimeSeries("Invested");

    public InvestmentChart(String agentName){
        super(agentName);
        this.agentName = agentName;

        chart = ChartFactory.createTimeSeriesChart(agentName,tx,ty,data,true,true,true);

    }

    public void addData(Date date, double sum, double inv, double hol){

        Day x = new Day(date);
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
        this.setSize(600,400);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.add(panel);
        this.setVisible(true);
    }
}
