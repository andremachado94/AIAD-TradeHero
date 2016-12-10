import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
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

    private ArrayList<AID> followers = new ArrayList<AID>();

    private Date currentDate;

    private InvestorPortfolio portfolio = new InvestorPortfolio();

    private HashMap<String, Company> knownInfo = new HashMap<>();

    private int investmentType = 3;

    private InvestorForm form;


    public void setup() {

        System.out.println("Agent Investor Created");

        chart =  new InvestmentChart(this.getName());
        chart.openPanel();

        form = new InvestorForm();
        addBehaviour(new TickerBehaviour(this, 200) {
            @Override
            protected void onTick() {
                if ((form.isCancelOpt() ^ form.isFormValid()) && !infoValid) {
                    if (form.isFormValid()) {
                        System.out.println("Form info received");
                        investmentType = form.getInvestementType();
                        totalMA = form.getTotalMA();
                        investMA = form.getInvestedMA();
                        capitMA = form.getCapitalMA();
                        portfolio.setInitialCapital(form.getInitialCapital());
                        investAmount = form.getInvestementAmount();
                        infoValid = true;
                    } else if (form.isCancelOpt()) {
                        myAgent.doDelete();
                    }
                }
            }
        });

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

        form.dispose();
        chart.dispose();

    }

    private class SuggestCompany extends OneShotBehaviour {

        private String suggestion;

        public SuggestCompany(String suggestion){
            this.suggestion = suggestion;
        }

        public void action() {

            if(followers.size() > 0) {
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
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("" + (portfolio.getPortfolioValue() + portfolio.getCurrentCapital())/portfolio.getInitialCapital());

                myAgent.send(reply);
            }
            else if (msg != null && msg.getConversationId().equals("follow-req")) {
                addFollower(msg.getSender());
            }
            else if (msg != null && msg.getConversationId().equals("unfollow-req")) {
                removeFollower(msg.getSender());
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
                    updateHistory(portfolio.getCurrentCapital(), portfolio.getPortfolioValue());

                    chart.addData(currentDate, getPortfolioValueHistoryMA(totalMA) + getCurrentCapitalHistoryMA(totalMA) ,getPortfolioValueHistoryMA(investMA) ,getCurrentCapitalHistoryMA(capitMA));
                    day+=1;
                    portfolio.update();
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

                if(!portfolio.boughtShare(company.getCompanyId()) && (n = investment.investAmount(investmentType,company,portfolio.getCurrentCapital()/investAmount)) > 0){
                    portfolio.buyShare(company, n, date);
                    //Uncomment for printing the investment Log
                    //System.out.println("|\t" + date + "\t|\tBuy \t|\t" + company.getCompanyId() + "\t|\t" + n + "\t|\t" + company.getLastClose() + "\t|");
                    addBehaviour(new SuggestCompany("buy," + company.getCompanyId() + "," + company.getLastClose() + "," +dateToString(date)));

                    return;
                }

                else if(portfolio.boughtShare(company.getCompanyId()) && investment.shouldSell(investmentType, company, portfolio.getShare(company.getCompanyId()).getBoughtPrice())){
                    n = portfolio.getShare(company.getCompanyId()).getAmount();
                    //Uncomment for printing the investment Log
                    //System.out.println("|\t" + date + "\t|\tSell\t|\t" + company.getCompanyId() + "\t|\t" + n + "\t|\t" + company.getLastClose() + "\t|");
                    portfolio.sellShare(company, n, date);
                    addBehaviour(new SuggestCompany("sell," + company.getCompanyId() + "," + company.getLastClose() + "," +dateToString(date)));
                }

        }
    }
}