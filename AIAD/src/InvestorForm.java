import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

/**
 * Created by andremachado on 05/12/2016.
 */
public class InvestorForm extends JFrame {

    String[] investmentTypes = new String[] {"RSI", "MACD", "Inv. MACD", "Random"};
    private final int N_INV = 4;

    private JPanel FormMainPanel;
    private JPanel FormWestPanel;
    private JPanel FormNorthPanel;
    private JPanel FormSouthPanel;
    private JButton CreateButton;
    private JButton CancelButton;
    private JLabel AgentNameLabel;
    private JPanel FormEastPanel;
    private JTextField InitialCapText;
    private JLabel IniCapLabel;
    private JLabel InvAmountLabel;
    private JLabel TotAmountMA;
    private JLabel InvestedMA;
    private JLabel CapMA;
    private JLabel InvType;
    private JSlider InvAmountSlider;
    private JSlider TorAmountMASlider;
    private JSlider InvestedMASlider;
    private JSlider CapMASlider;
    private JComboBox InvTypeCBox;

    private double initialCapital = 0;
    private int investementAmount = 1;
    private int totalMA = 1;
    private int investedMA = 1;
    private int capitalMA = 1;
    private int investementType = 0;

    private static JFrame frame;

    private boolean formValid = false;
    private boolean cancelOpt = false;


    public InvestorForm(){
        frame = new JFrame("InvestorForm");
        frame.setContentPane(this.FormMainPanel);

        setComboBoxLabels();
        setSlidersLabel();
        setSlidersLabelListeners();

        frame.setVisible(true);

        CreateButton.addActionListener(new ActionListener() {
            /**
             * Invoked when user presses create button
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String cap = InitialCapText.getText();

                if(isNumeric(cap)){
                    initialCapital = Double.parseDouble(cap);
                    setInvestementType();
                    investementAmount = InvestedMASlider.getValue();
                    totalMA = TorAmountMASlider.getValue();
                    investedMA = InvAmountSlider.getValue();
                    capitalMA = CapMASlider.getValue();

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


        CancelButton.addActionListener(new ActionListener() {
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

    private void close(){
      //  WindowEvent winClosingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
      //  frame.dispatchEvent(winClosingEvent);
        frame.setVisible(false);
    }

    private void setComboBoxLabels(){
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(investmentTypes);
        InvTypeCBox.setModel(model);
    }

    private void setSlidersLabel(){
        InvAmountSlider.setMajorTickSpacing(499);
        InvAmountSlider.setMinorTickSpacing(100);
        InvAmountSlider.setPaintTicks(true);
        InvAmountSlider.setPaintLabels(true);

        TorAmountMASlider.setMajorTickSpacing(249);
        TorAmountMASlider.setMinorTickSpacing(50);
        TorAmountMASlider.setPaintTicks(true);
        TorAmountMASlider.setPaintLabels(true);

        InvestedMASlider.setMajorTickSpacing(249);
        InvestedMASlider.setMinorTickSpacing(50);
        InvestedMASlider.setPaintTicks(true);
        InvestedMASlider.setPaintLabels(true);

        CapMASlider.setMajorTickSpacing(249);
        CapMASlider.setMinorTickSpacing(50);
        CapMASlider.setPaintTicks(true);
        CapMASlider.setPaintLabels(true);

    }

    private void setSlidersLabelListeners(){


        InvAmountSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider)ce.getSource();
                if (!slider.getValueIsAdjusting()) {
                    slider.setToolTipText( "" + InvAmountSlider.getValue());
                }
            }
        });

        TorAmountMASlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider)ce.getSource();
                if (!slider.getValueIsAdjusting()) {
                    slider.setToolTipText( "" + TorAmountMASlider.getValue());
                }
            }
        });


        InvestedMASlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider)ce.getSource();
                if (!slider.getValueIsAdjusting()) {
                    slider.setToolTipText( "" + InvestedMASlider.getValue());
                }
            }
        });


        CapMASlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider)ce.getSource();
                if (!slider.getValueIsAdjusting()) {
                    slider.setToolTipText( "" + CapMASlider.getValue());
                }
            }
        });


    }

    private static boolean isNumeric(String str)
    {
        return str.matches("\\d+(\\.\\d+)?");
    }

    private void setInvestementType() {
        String val = InvTypeCBox.getSelectedItem().toString();

        for(int i = 0 ; i < N_INV ; i++){
            if(val.equals(investmentTypes[i])){
                System.out.println( i + " - " + val);
                investementType = i;
                break;
            }
        }

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

    public int getInvestementType() {
        return investementType;
    }

    public boolean isFormValid() {
        return formValid;
    }

    public boolean isCancelOpt() {
        return cancelOpt;
    }
}
