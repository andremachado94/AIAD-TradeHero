package GUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by andremachado on 09/12/2016.
 */
public class PlayerForm {

    private JPanel FormMainPanel;
    private JPanel FormSouthPanel;
    private JPanel FormNorthPanel;
    private JPanel FormEastPanel;
    private JPanel FormWestPanel;
    private JTextField InitialCapitalTextField;
    private JSlider InvestmentAmountSlider;
    private JSlider TotalAmountMASlider;
    private JSlider TotalInvestedAmountMASlider;
    private JSlider CurrentCapitalMASlider;
    private JTextField InvestmentThresholdTextField;
    private JButton createButton;
    private JButton cancelButton;
    private JLabel initialCapitalLabel;
    private JLabel investmentAmountLabel;
    private JLabel totalAmontMALabel;
    private JLabel totalInvestedAmountMALabel;
    private JLabel currentCapitalMALabel;
    private JLabel investmentThresholdLabel;
    private JLabel titleLabel;


    private double initialCapital = 0;
    private int investementAmount = 1;
    private int totalMA = 1;
    private int investedMA = 1;
    private int capitalMA = 1;
    private double investementThreshold = 0;

    private static JFrame frame;

    private boolean formValid = false;
    private boolean cancelOpt = false;

    public PlayerForm() {

        frame = new JFrame("InvestorForm");
        frame.setContentPane(this.FormMainPanel);

        setSlidersLabel();
        setSlidersLabelListeners();

        frame.setVisible(true);

        createButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String cap = InitialCapitalTextField.getText();
                String ths = InvestmentThresholdTextField.getText();

                if(isNumeric(cap) && isNumeric(ths)){
                    initialCapital = Double.parseDouble(cap);
                    investementThreshold = Double.parseDouble(ths);
                    investementAmount = InvestmentAmountSlider.getValue();
                    totalMA = TotalAmountMASlider.getValue();
                    investedMA = TotalInvestedAmountMASlider.getValue();
                    capitalMA = CurrentCapitalMASlider.getValue();

                    JOptionPane.showMessageDialog(null, "Agent created");

                    formValid = true;

                    close();

                }
                else {
                    JOptionPane.showMessageDialog(null, "ERROR\nInvalid Stuff");
                    cancelOpt = true;
                    close();
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelOpt = true;
                close();
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }

    public void close(){
        frame.setVisible(false);
    }

    private void setSlidersLabel(){
        InvestmentAmountSlider.setMajorTickSpacing(499);
        InvestmentAmountSlider.setMinorTickSpacing(100);
        InvestmentAmountSlider.setPaintTicks(true);
        InvestmentAmountSlider.setPaintLabels(true);

        TotalAmountMASlider.setMajorTickSpacing(249);
        TotalAmountMASlider.setMinorTickSpacing(50);
        TotalAmountMASlider.setPaintTicks(true);
        TotalAmountMASlider.setPaintLabels(true);

        TotalInvestedAmountMASlider.setMajorTickSpacing(249);
        TotalInvestedAmountMASlider.setMinorTickSpacing(50);
        TotalInvestedAmountMASlider.setPaintTicks(true);
        TotalInvestedAmountMASlider.setPaintLabels(true);

        CurrentCapitalMASlider.setMajorTickSpacing(249);
        CurrentCapitalMASlider.setMinorTickSpacing(50);
        CurrentCapitalMASlider.setPaintTicks(true);
        CurrentCapitalMASlider.setPaintLabels(true);

    }

    private void setSlidersLabelListeners(){


        InvestmentAmountSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider)ce.getSource();
                if (!slider.getValueIsAdjusting()) {
                    slider.setToolTipText( "" + InvestmentAmountSlider.getValue());
                }
            }
        });

        TotalAmountMASlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider)ce.getSource();
                if (!slider.getValueIsAdjusting()) {
                    slider.setToolTipText( "" + TotalAmountMASlider.getValue());
                }
            }
        });


        TotalInvestedAmountMASlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider)ce.getSource();
                if (!slider.getValueIsAdjusting()) {
                    slider.setToolTipText( "" + TotalInvestedAmountMASlider.getValue());
                }
            }
        });


        CurrentCapitalMASlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider)ce.getSource();
                if (!slider.getValueIsAdjusting()) {
                    slider.setToolTipText( "" + CurrentCapitalMASlider.getValue());
                }
            }
        });


    }

    private static boolean isNumeric(String str)
    {
        return str.matches("\\d+(\\.\\d+)?");
    }

    public double getInitialCapital() {
        return initialCapital;
    }

    public int getInvestementAmount() {
        return investementAmount;
    }

    public int getTotalMA() {
        return totalMA;
    }

    public int getInvestedMA() {
        return investedMA;
    }

    public int getCapitalMA() {
        return capitalMA;
    }

    public double getInvestementThreshold() {
        return investementThreshold;
    }

    public boolean isFormValid() {
        return formValid;
    }

    public boolean isCancelOpt() {
        return cancelOpt;
    }
}
