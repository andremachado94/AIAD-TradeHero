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
    private ArrayList<AID> following = new ArrayList<AID>();

    private PlayerPortfolio portfolio = new PlayerPortfolio();

    private MessageTemplate mt=null; // The template to receive replies
    private int step = 0;

    private double trustLimit =0;

    boolean newDay = true;

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


        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd2 = new ServiceDescription();
        sd2.setType("investor");
        template.addServices(sd2);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Player found the following investor agents:");
            investorAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                investorAgents[i] = result[i].getName();
                System.out.println(investorAgents[i].getName() + "  -  " + result.length);
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        Object[] args = getArguments();
        trustLimit = Double.parseDouble(args[0].toString());

        addBehaviour(new dataReceiver());
        addBehaviour(new SuggestionReceiver());
        addBehaviour(new TickerBehaviour(this, 200) {
            protected void onTick() {

                switch (step) {
                    case 0:
                        // Request success rate to a random investor
                        ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
                        //TODO
                        cfp.addReceiver(investorAgents[0]);

                        cfp.setContent("rate-request");
                        cfp.setConversationId("rate-req");

                        cfp.setReplyWith("ratereq" + System.currentTimeMillis()); // Unique value
                        myAgent.send(cfp);
                        // Prepare the template to get confirmations
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("rate-req"),
                                MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                        step = 1;
                        break;
                    case 1:
                        // Check if investor received the information
                        MessageTemplate mm = MessageTemplate.and(MessageTemplate.MatchConversationId("rate-req"),
                                MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                        ACLMessage reply = myAgent.receive(mm);
                        if (reply != null) {
                            // Reply received
                            if (true) {
                                double rate = Double.parseDouble(reply.getContent());
                                if(rate > trustLimit){
                                    if(!isFollowing(reply.getSender())){
                                        step = 2;
                                        System.out.println("\nSuccess rate de " + reply.getContent());
                                    }
                                    else{
                                        step = 0;
                                       // System.out.println("Já estou a seguir");
                                    }

                                }
                                else{
                                    if(isFollowing(reply.getSender())) {
                                        step = 3;
                                        System.out.println("\nSuccess rate de " + reply.getContent());
                                    }
                                    else{
                                        step = 0;
                                       // System.out.println("Não sigo nem tenho interesse");
                                    }
                                }
                            }
                        } else {
                            block();
                        }
                        break;
                    case 2:
                        ACLMessage cfp3 = new ACLMessage(ACLMessage.REQUEST);
                        //TODO
                        cfp3.addReceiver(investorAgents[0]);

                        cfp3.setContent("follow-request");
                        cfp3.setConversationId("follow-req");

                        cfp3.setReplyWith("followreq" + System.currentTimeMillis()); // Unique value
                        myAgent.send(cfp3);
                        // Prepare the template to get confirmations
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("follow-req"),
                                MessageTemplate.MatchInReplyTo(cfp3.getReplyWith()));

                        System.out.println(myAgent.getName() + ": Hey, quero seguir-te");

                        //TODO
                        follow(investorAgents[0]);

                        step = 0;

                        break;
                    case 3:
                        ACLMessage cfp2 = new ACLMessage(ACLMessage.REQUEST);
                        //TODO
                        cfp2.addReceiver(investorAgents[0]);

                        cfp2.setContent("unfollow-request");
                        cfp2.setConversationId("unfollow-req");

                        cfp2.setReplyWith("unfollowreq" + System.currentTimeMillis()); // Unique value
                        myAgent.send(cfp2);
                        // Prepare the template to get confirmations
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("unfollow-req"),
                                MessageTemplate.MatchInReplyTo(cfp2.getReplyWith()));

                        System.out.println(myAgent.getName() + ": Hey, cheiras mal e já não gosto de ti");

                        //TODO
                        unfollow(investorAgents[0]);
                        step = 0;
                        break;
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
                    // INFORM Message received. Process it
                  //  System.out.println("SUGGESTION RECEIVED");
                    String[] info = msg.getContent().split(",");

                    if (info.length == 4) {
                        if (info[0].equals("buy")) {
                            portfolio.buyShare(info[1], Double.parseDouble(info[2]), stringToDate(info[3]));
                        } else if (info[0].equals("sell")) {
                            portfolio.sellShare(info[1], Double.parseDouble(info[2]), stringToDate(info[3]));
                        } else {
                            System.out.println("Invalid Suggestion msg - 1");
                        }
                     //   System.out.println("Wallet :\t" + portfolio.getPortfolioValue());
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
                    chart.addData(day, portfolio.getCurrentCapital() + portfolio.getPortfolioValue() ,getPortfolioValueHistoryMA(250) ,getCurrentCapitalHistoryMA(250));
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
                //TODO Meter esta merda direito
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

/*
    private class InvestorCommunication extends TickerBehaviour {


        public void onTick() {

            MessageTemplate mt; // The template to receive replies
            int step = 0;

            switch (step) {
                case 0:
                    // Request success rate to a random investor
                    ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
                    //TODO
                    cfp.addReceiver(investorAgents[0]);

                    cfp.setContent("rate-request");
                    cfp.setConversationId("rate-req");

                    cfp.setReplyWith("ratereq" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get confirmations
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("rate-req"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    // Check if investor received the information
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.INFORM && reply.getConversationId().equals("rate-req")) {
                            double rate = Double.parseDouble(reply.getContent());
                            if(rate > 0.5){
                                if(!isFollowing(reply.getSender()))
                                    step = 2;
                                else{
                                    step = 0;
                                    System.out.println("Já estou a seguir");
                                }

                            }
                            else{
                                if(isFollowing(reply.getSender()))
                                    step = 3;
                                else{
                                    step = 0;
                                    System.out.println("Não sigo nem tenho interesse");
                                }
                            }
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    ACLMessage cfp3 = new ACLMessage(ACLMessage.REQUEST);
                    //TODO
                    cfp3.addReceiver(investorAgents[0]);

                    cfp3.setContent("follow-request");
                    cfp3.setConversationId("follow-req");

                    cfp3.setReplyWith("followreq" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp3);
                    // Prepare the template to get confirmations
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("follow-req"),
                            MessageTemplate.MatchInReplyTo(cfp3.getReplyWith()));

                    System.out.println("Hey, quero seguir-te");

                    //TODO
                    follow(investorAgents[0]);

                    step = 0;

                    break;
                case 3:
                    ACLMessage cfp2 = new ACLMessage(ACLMessage.REQUEST);
                    //TODO
                    cfp2.addReceiver(investorAgents[0]);

                    cfp2.setContent("unfollow-request");
                    cfp2.setConversationId("unfollow-req");

                    cfp2.setReplyWith("unfollowreq" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp2);
                    // Prepare the template to get confirmations
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("unfollow-req"),
                            MessageTemplate.MatchInReplyTo(cfp2.getReplyWith()));

                    System.out.println("Hey, cheiras mal e já não gosto de ti");

                    //TODO
                    unfollow(investorAgents[0]);
                    step = 0;
                    break;
            }
        }
    }
*/
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

}
