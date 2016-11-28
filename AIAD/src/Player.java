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
 * Created by andremachado on 21/11/2016.
 */
public class Player extends  jade.core.Agent {


    private AID[] investorAgents;
    private ArrayList<AID> following;


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

       // addBehaviour(new Investor.dataReceiver());

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


    private class investorCommunication extends CyclicBehaviour {

        private MessageTemplate mt; // The template to receive replies
        private int step = 0;

        public void action() {
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
                        if (reply.getPerformative() == ACLMessage.AGREE) {
                            double rate = Double.parseDouble(reply.getContent());
                            if(rate > 0){
                                if(ifFollowing(reply.getSender())){
                                    //TODO REQUEST Portfolio
                                    step = 2;
                                }
                                else {
                                    //TODO follow
                                    step = 4;
                                }
                            }
                            else{
                                if(ifFollowing(reply.getSender())){
                                    //TODO unfollow
                                    step = 6;
                                }
                                else {
                                    //TODO
                                    step = 0;
                                }
                            }
                        }
                        else if(reply.getPerformative() == ACLMessage.REFUSE){
                            //TODO
                            step = 0;
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    ACLMessage cfp2 = new ACLMessage(ACLMessage.REQUEST);
                    //TODO
                    cfp2.addReceiver(investorAgents[0]);

                    cfp2.setContent("portfolio-request");
                    cfp2.setConversationId("portfolio-req");

                    cfp2.setReplyWith("portreq" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp2);
                    // Prepare the template to get confirmations
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("portfolio-req"),
                            MessageTemplate.MatchInReplyTo(cfp2.getReplyWith()));

                    step = 3;

                    break;

                case 3:
                    // Check if investor received the information
                    ACLMessage reply2 = myAgent.receive(mt);
                    if (reply2 != null) {
                        // Reply received
                        if (reply2.getPerformative() == ACLMessage.AGREE) {
                            String info = reply2.getContent();
                            //TODO Process info received
                            step = 4;

                        }
                        else if(reply2.getPerformative() == ACLMessage.REFUSE){
                            //TODO
                            step = 0;
                        }
                    } else {
                        block();
                    }
                    break;
                case 4:
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

                    step = 5;

                    break;
                case 5:
                    // Check if investor received the information
                    ACLMessage reply3 = myAgent.receive(mt);
                    if (reply3 != null) {
                        // Reply received
                        if (reply3.getPerformative() == ACLMessage.AGREE) {
                            //TODO Add to follow
                            step = 2;

                        }
                        else if(reply3.getPerformative() == ACLMessage.REFUSE){
                            //TODO
                            step = 0;
                        }
                    } else {
                        block();
                    }
                    break;
                case 6:
                    ACLMessage cfp4 = new ACLMessage(ACLMessage.REQUEST);
                    //TODO
                    cfp4.addReceiver(investorAgents[0]);

                    cfp4.setContent("unfollow-request");
                    cfp4.setConversationId("unfollow-req");

                    cfp4.setReplyWith("unfreq" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp4);
                    // Prepare the template to get confirmations
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("unfollow-req"),
                            MessageTemplate.MatchInReplyTo(cfp4.getReplyWith()));

                    //TODO UNFOLLOW

                    step = 3;

                    break;
            }
        }
    }

    private boolean ifFollowing(AID sender) {
        for (int i = 0 ; i < following.size() ; i++){
            if(following.get(i) == sender)
                return true;
        }
        return false;
    }

    private void follow(AID sender){
        for (int i = 0 ; i < following.size() ; i++){
            if(following.get(i) == sender)
                return;
        }
        following.add(sender);
    }

    private void unfollow(AID sender){
        for (int i = 0 ; i < following.size() ; i++){
            if(following.get(i) == sender){
                following.remove(i);
                return;
            }
        }
    }
}
