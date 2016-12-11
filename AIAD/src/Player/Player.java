package Player;

import GUI.InvestmentChart;
import GUI.PlayerForm;
import Investor.InvestorAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;


/**
 * Created by andremachado on 21/11/2016.
 */
public class Player extends InvestorAgent {


    private AID[] investorAgents;
    private double[] investorAgentsRatio;
    private ArrayList<AID> following = new ArrayList<AID>();

    private PlayerPortfolio portfolio = new PlayerPortfolio();

    private MessageTemplate mt=null; // The template to receive replies
    private int step = 0;
    private int replies = 0;

    private double trustLimit = 1.1;

    boolean newDay = true;

    private PlayerForm form;

    FollowersPQ fpq;
    Follower[] top;

    public void setup() {

        System.out.println("Agent Player Created");
        // Register the player in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("player");
        sd.setName("JADE-player");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }


        chart =  new InvestmentChart(this.getName());
        chart.openPanel();

        form = new PlayerForm();
        addBehaviour(new TickerBehaviour(this, 500) {
            @Override
            protected void onTick() {
                if ((form.isCancelOpt() ^ form.isFormValid()) && !infoValid) {
                    if (form.isFormValid()) {
                        System.out.println("Form info received");
                        totalMA = form.getTotalMA();
                        investMA = form.getInvestedMA();
                        capitMA = form.getCapitalMA();
                        portfolio.setInitialCapital(form.getInitialCapital());
                        investAmount = form.getInvestementAmount();
                        trustLimit = form.getInvestementThreshold();
                        infoValid = true;
                    } else if (form.isCancelOpt()) {
                        chart.dispose();
                        myAgent.doDelete();
                    }
                }
            }
        });


        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd2 = new ServiceDescription();
        sd2.setType("investor");
        template.addServices(sd2);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Player found the following investor agents:");
            investorAgents = new AID[result.length];
            investorAgentsRatio = new double[result.length];
            for (int i = 0; i < result.length; ++i) {
                investorAgents[i] = result[i].getName();
                investorAgentsRatio[i] = 0;
                System.out.println(investorAgents[i].getName() + "  -  " + result.length);
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        if(investorAgents == null || investorAgents.length == 0){
            form.close();
            chart.dispose();
            this.doDelete();
        }

        addBehaviour(new dataReceiver());
        addBehaviour(new SuggestionReceiver());
        addBehaviour(new TickerBehaviour(this, 500) {
            protected void onTick() {
/*
                Random rn = new Random();
                int invIndex =  rn.nextInt(investorAgents.length);
*/


                switch (step) {
                    case 0:
                        // Request success rate to all random investor
                        ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
                        for(int i = 0 ; i < investorAgents.length ; i++) {
                            cfp.addReceiver(investorAgents[i]);
                        }

                        cfp.setContent("rate-request");
                        cfp.setConversationId("rate-req");

                        cfp.setReplyWith("ratereq" + System.currentTimeMillis()); // Unique value
                        myAgent.send(cfp);
                        // Prepare the template to get confirmations
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("rate-req"),
                                MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

                        fpq = new FollowersPQ(investorAgents.length);
                        top = new Follower[2];

                        step = 1;
                        break;
                    case 1:
                        // Check if investor received the information
                        MessageTemplate mm = MessageTemplate.and(MessageTemplate.MatchConversationId("rate-req"),
                                MessageTemplate.MatchPerformative(ACLMessage.INFORM));

                        while (replies < investorAgents.length) {
                            ACLMessage reply = myAgent.receive(mm);
                            if (reply != null) {
                                // Reply received
                                double rate = Double.parseDouble(reply.getContent());

                                for(int i = 0 ; i < investorAgents.length ; i++){
                                    if(investorAgents[i].equals(reply.getSender())){
                                        fpq.add(i, rate);
                                        break;
                                    }
                                }
                                replies++;
                            } else {
                                block();
                            }
                        }
                        Follower[] temp = fpq.getTop2();
                        for(int i = 0 ; i < 2 ; i++){
                            top[i] = temp[i];
                           // System.out.println(top[i]);
                        }
                        replies = 0;
                        step = 2;
                        break;
                    case 2:

                        ACLMessage cfp3 = new ACLMessage(ACLMessage.REQUEST);

                        for(int i = 0 ; i < 2 ; i++){
                            if(top[i] != null && !isFollowing(investorAgents[top[i].getIndex()]) && top[i].getVal() >= trustLimit) {
                                cfp3.addReceiver(investorAgents[top[i].getIndex()]);
                                follow(investorAgents[top[i].getIndex()]);
                                System.out.println("======================FOLLOW======================");
                            }
                            else if(top[i] == null) break;
                        }

                        cfp3.setContent("follow-request");
                        cfp3.setConversationId("follow-req");

                        cfp3.setReplyWith("followreq" + System.currentTimeMillis()); // Unique value
                        myAgent.send(cfp3);
                        // Prepare the template to get confirmations
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("follow-req"),
                                MessageTemplate.MatchInReplyTo(cfp3.getReplyWith()));

                        step = 3;

                        break;
                    case 3:
                        ACLMessage cfp2 = new ACLMessage(ACLMessage.REQUEST);

                        for(int i = 0 ; i < following.size() ; i++) {
                            boolean found = false;

                            for(int j = 0 ; j < 2 ; j++) {
                                if(top[j] == null) break;

                                if(investorAgents[top[j].getIndex()].equals( following.get(i) )){
                                    found = true;
                                    break;
                                }
                            }
                            if(!found) {
                                cfp2.addReceiver(following.get(i));
                                unfollow(following.get(i));
                                System.out.println("======================UNFOLLOW======================");
                            }
                        }

                        cfp2.setContent("unfollow-request");
                        cfp2.setConversationId("unfollow-req");

                        cfp2.setReplyWith("unfollowreq" + System.currentTimeMillis()); // Unique value
                        myAgent.send(cfp2);
                        // Prepare the template to get confirmations
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("unfollow-req"),
                                MessageTemplate.MatchInReplyTo(cfp2.getReplyWith()));

                        System.out.println("Following:");
                        for(int i = 0 ; i< following.size(); i++)
                            System.out.println("\t"+following.get(i).getName());

                        step = 0;

                }
            }
        });


    }


    private class SuggestionReceiver extends CyclicBehaviour {
        public void action() {
            if(newDay){
                MessageTemplate templ = MessageTemplate.and(MessageTemplate.MatchConversationId("invSug"),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                ACLMessage msg = myAgent.receive(templ);
                if (msg != null) {
                    String[] info = msg.getContent().split(",");

                    if (info.length == 4) {
                        if (info[0].equals("buy")) {
                            portfolio.buyShare(info[1], Double.parseDouble(info[2]), stringToDate(info[3]));
                        } else if (info[0].equals("sell")) {
                            portfolio.sellShare(info[1], Double.parseDouble(info[2]), stringToDate(info[3]));
                        } else {
                            System.out.println("Invalid Suggestion msg - 1");
                        }
                    } else {
                        System.out.println("Invalid Suggestion msg - 2");
                    }

                } else {
                    block();
                }
            }
        }
    }


    private class dataReceiver extends CyclicBehaviour {
        public void action() {
            MessageTemplate templ = MessageTemplate.and(MessageTemplate.MatchConversationId("cmpData"),
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM));

            ACLMessage msg = myAgent.receive(templ);
            if (msg != null) {
                // INFORM Message received. Process i
                String str = msg.getContent();
                ACLMessage reply = msg.createReply();


                String[] data = str.split("/");

                if (data[0] != null) {
                    // Received the date
                    update(data);
                    updateHistory(portfolio.getCurrentCapital(), portfolio.getPortfolioValue());
                    chart.addData(stringToDate(data[0]), getPortfolioValueHistoryMA(totalMA) +getCurrentCapitalHistoryMA(totalMA) ,getPortfolioValueHistoryMA(investMA) ,getCurrentCapitalHistoryMA(capitMA));
                    day+=1;
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

        private void update(String data[]){
                //key - 0
                //close - 1
            for(int i = 1 ; i < data.length ; i++){
                String[] info = data[i].split(",");
                if(info.length > 2)
                     portfolio.update(info[0], Double.parseDouble(info[1]));
            }

        }
    }

    public void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Printout a dismissal message
        System.out.println("Player " + getAID().getName() + " terminating.");
    }


    private boolean isFollowing(AID sender) {
        for (int i = 0 ; i < following.size() ; i++){
            if(following.get(i).equals(sender))
                return true;
        }
        return false;
    }

    private void follow(AID sender){
        for (int i = 0 ; i < following.size() ; i++){
            if(following.get(i).equals(sender))
                return;
        }
        following.add(sender);
    }

    private void unfollow(AID sender){
        for (int i = 0 ; i < following.size() ; i++){
            if(following.get(i).equals(sender)){
                following.remove(i);
                return;
            }
        }
    }



    private static class FollowersPQ{

        private PriorityQueue<Follower> pq;

        public FollowersPQ(int size){
            Comparator<Follower> comparator = new FollowerComparator();
            pq = new PriorityQueue(size, comparator);
        }

        public void add(int index, double val){
            Follower f = new Follower(index,val);
            pq.add(f);
        }

        public Follower[] getTop2(){
            Follower[] arr = new Follower[2];

            for(int i = 0 ; i < 2 ; i++){
                if(pq.size() > 0)
                    arr[i] = pq.poll();
                else
                    arr[i] = null;
            }

            return arr;
        }


        private class FollowerComparator implements Comparator<Follower> {

            @Override
            public int compare(Follower x, Follower y)
            {
                // Assume neither string is null. Real code should
                // probably be more robust
                // You could also just return x.length() - y.length(),
                // which would be more efficient.
                if (x.getVal() > y.getVal())
                {
                    return -1;
                }
                if (x.getVal() < y.getVal())
                {
                    return 1;
                }
                return 0;
            }
        }
    }
    private static class Follower {
        private int index;
        private double val;

        public Follower(int index , double val){
            this.index = index;
            this.val = val;
        }

        public int getIndex() {
            return index;
        }

        public double getVal() {
            return val;
        }
    }

}
