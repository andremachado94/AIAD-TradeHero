import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * Created by andremachado on 09/12/2016.
 */
public class InformerForm extends JFrame{
    private JPanel FormMainPanel;
    private JPanel FormNorthPanel;
    private JPanel FormSouthPanel;
    private JButton cancelButton;
    private JButton startButton;

    private JDatePickerImpl datePicker;
    private UtilDateModel model = new UtilDateModel();
    private JDatePanelImpl datePanel;

    private JFrame frame;

    private boolean formValid = false;
    private boolean cancelOpt = false;

    private Date date;


    public InformerForm() {

        frame = new JFrame("InformerForm");
        frame.setContentPane(this.FormMainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model.setDate(2005, 8, 24);
        model.setSelected(true);

        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        frame.add(datePicker);


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
        startButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                date = (Date) datePicker.getModel().getValue();
                formValid = true;
                close();
                System.out.println(date);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    public Date getDate(){
        return date;
    }

    public void close(){
        frame.setVisible(false);
    }

    public static void main(String[] args) {
        InformerForm f = new InformerForm();
    }

    public class DateLabelFormatter extends AbstractFormatter {

        private String datePattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }

            return "";
        }

    }

    public boolean isFormValid() {
        return formValid;
    }

    public boolean isCancelOpt() {
        return cancelOpt;
    }
}
