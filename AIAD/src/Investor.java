import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;

import java.util.*;

/**
 * Created by andremachado on 26/10/2016.
 */
public class Investor extends jade.core.Agent {

    ArrayList<Company> companies;
    private AID[] informerAgents;
    private boolean infoReceived = true;
    private Date currentDate;
    private Date limitDate;
    private GregorianCalendar calendar;

    private Portfolio portfolio;

    private HashMap<String, Company> knownInfo = new HashMap<>();

    private int investmentType = 0;



    public void createPortfolio() {
        portfolio = new Portfolio();
    }

    public void setup() {

        System.out.println("Agent Investor Created");

        Object[] args = getArguments();
        int inv = Integer.parseInt(args[0].toString());

        investmentType = inv;


        // Register the book-selling service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("investor");
        sd.setName("JADE-investor");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        createPortfolio();

        addBehaviour(new dataReceiver());

    }

    public void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Printout a dismissal message
        System.out.println("Investor " + getAID().getName() + " terminating.");
    }


    private class dataReceiver extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // INFORM Message received. Process it
                String str = msg.getContent();
                ACLMessage reply = msg.createReply();

                String[] data = str.split("/");

                if (data[0] != null) {
                    // Received the date
                    currentDate = processReceivedMessage(str);
                    portfolio.update();
                    System.out.println(this.getAgent().getName() + ": Current Capital: " + portfolio.getCurrentCapital()+ "\t+\t" + portfolio.getPortfolioValue() + "\n");
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent(data[0]);
                } else {
                    System.out.println("Shit just got serious");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }

        private Date processReceivedMessage(String msg) {
            if (msg == null) return null;
            String[] data = msg.split("/");

            Date date = stringToDate(data[0]);

            for (int i = 1; i < data.length; i++) {
                String[] cmpData = data[i].split(",");
                if (!knownInfo.containsKey(cmpData[0])) {
                    knownInfo.put(cmpData[0], new Company(cmpData[0], date));
                }

                knownInfo.get(cmpData[0])
                        .addInfo(
                             Double.parseDouble(cmpData[1]),
                             Double.parseDouble(cmpData[2]),
                             Double.parseDouble(cmpData[3]),
                             Integer.parseInt(cmpData[4])
                        );


                analyseCompany(knownInfo.get(cmpData[0]), date);

            }

            return date;
        }

    }  // End of inner class parseReceivedMessage

    private void analyseCompany(Company company, Date date) {
        if(company.haveEnoughInfo()){

            Investment investment = new Investment();
            int n;

                if(!portfolio.boughtShare(company.getCompanyId()) && (n = investment.investAmount(investmentType,company,portfolio.getCurrentCapital()/100)) > 0 && portfolio.getCurrentCapital() > portfolio.getCurrentCapital()/100){
                    portfolio.buyShare(company, n, date);
                    //System.out.println("Bought" + n + "shares from " + company.getCompanyId() + " @" + date);
                    return;
                }

                else if(portfolio.boughtShare(company.getCompanyId()) && investment.shouldSell(investmentType, company)){
                    n = portfolio.getShare(company.getCompanyId()).getAmount();
                    portfolio.sellShare(company, n, date);
                }

        }
    }


    private Date stringToDate(String info) {
        String[] dateInfo = info.split("-");

        return new GregorianCalendar(
                Integer.parseInt(dateInfo[0]),
                Integer.parseInt(dateInfo[1]),
                Integer.parseInt(dateInfo[2])
        ).getTime();
    }


    private String dateToString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);

    }

}