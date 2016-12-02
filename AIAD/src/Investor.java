import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

/**
 * Created by andremachado on 26/10/2016.
 */
public class Investor extends InvestorAgent {

    ArrayList<Company> companies;

    private ArrayList<AID> followers = new ArrayList<AID>();

    private Date currentDate;


    private InvestorPortfolio portfolio;

    private HashMap<String, Company> knownInfo = new HashMap<>();

    private int investmentType = 0;

    private double day = 0;
    InvestmentChart chart;

    public void createPortfolio() {
        portfolio = new InvestorPortfolio();
    }

    public void setup() {

        System.out.println("Agent Investor Created");

        Object[] args = getArguments();
        int inv = Integer.parseInt(args[0].toString());

        investmentType = inv;

        chart =  new InvestmentChart(this.getName());


        chart.openPanel();

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

        //new InvestmentChart(this.getName()).setVisible(true);

        createPortfolio();

        addBehaviour(new dataReceiver());
        addBehaviour(new manageFollowers());
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

    private class SuggestCompany extends OneShotBehaviour {

        private String suggestion;

        public SuggestCompany(String suggestion){
            this.suggestion = suggestion;
        }

        public void action() {

            //System.out.println("\n\n\n------------------------\nTrying to get company Info:\n------------------------\n\n");

            // Send the information to all investors
           // System.out.println(suggestion);

            if(followers.size() > 0) {
                System.out.println("Suggesting a buy");
                //System.out.println(suggestion);
                ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
                for (int i = 0; i < followers.size(); ++i) {
                    cfp.addReceiver(followers.get(i));
                }

                cfp.setContent(suggestion);
                cfp.setConversationId("invSug");

                cfp.setReplyWith("iS" + System.currentTimeMillis()); // Unique value
                myAgent.send(cfp);
            }

        }
    }

    private class manageFollowers extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null && msg.getConversationId().equals("rate-req")) {
                // INFORM Message received. Process it
                //String str = msg.getContent();
                System.out.println("RATE REQUESTED");
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("" + (portfolio.getPortfolioValue() + portfolio.getCurrentCapital())/portfolio.getInitialCapital());

                myAgent.send(reply);
            }
            else if (msg != null && msg.getConversationId().equals("follow-req")) {
                // INFORM Message received. Process it
                addFollower(msg.getSender());
                //System.out.println(myAgent.getName() + " followers:");
                //printFollowers();
                // TODO Add to Followers
            }
            else if (msg != null && msg.getConversationId().equals("unfollow-req")) {

                removeFollower(msg.getSender());
                //System.out.println(myAgent.getName() + " followers:");
                //printFollowers();
                // TODO Remove from Followers
            }else {
                block();
            }
        }

        private void addFollower(AID follower){
            if(!followers.contains(follower))
                followers.add(follower);
        }

        private void removeFollower(AID follower){
            if(followers.contains(follower))
                followers.remove(follower);
        }

        private void printFollowers(){
            for(int i = 0 ; i < followers.size() ; i++){
                System.out.println(followers.get(i).getName());
            }
        }
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

                    chart.addData(day, portfolio.getPortfolioValue() ,portfolio.getCurrentCapital());
                    day+=1;
                    portfolio.update();
                    //System.out.println(this.getAgent().getName() + ": Current Capital: " + portfolio.getCurrentCapital()+ "\t+\t" + portfolio.getPortfolioValue() + "\n");
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

                if(!portfolio.boughtShare(company.getCompanyId()) && (n = investment.investAmount(investmentType,company,portfolio.getCurrentCapital()/100)) > 0){
                    portfolio.buyShare(company, n, date);
                    addBehaviour(new SuggestCompany("buy," + company.getCompanyId() + "," + company.getLastClose() + "," +dateToString(date)));

                    //System.out.println("\nBought" + n + "shares from " + company.getCompanyId() + " @" + date);
                    //System.out.println("Current Capital: " + portfolio.getCurrentCapital()+ "\t+\t" + portfolio.getPortfolioValue() + "\n");

                    return;
                }

                else if(portfolio.boughtShare(company.getCompanyId()) && investment.shouldSell(investmentType, company)){
                    n = portfolio.getShare(company.getCompanyId()).getAmount();
                    System.out.println(this.getName() + " sold " + n + " shares from " + company.getCompanyId() + " at " + company.getLastClose() + " each ");

                    portfolio.sellShare(company, n, date);
                   // System.out.println("\nSold" + n + "shares from " + company.getCompanyId() + " @" + date);
                    //System.out.println("Current Capital: " + portfolio.getCurrentCapital()+ "\t+\t" + portfolio.getPortfolioValue() + "\n");

                    addBehaviour(new SuggestCompany("sell," + company.getCompanyId() + "," + company.getLastClose() + "," +dateToString(date)));
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