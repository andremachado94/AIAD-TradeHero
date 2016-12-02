import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by andremachado on 07/11/2016.
 */
public class Informer extends jade.core.Agent {

    private HashMap<String,HashMap<Date , DayValue>> map  = new HashMap<String, HashMap<Date, DayValue>>();
    private ArrayList<String> mapKeys = new ArrayList<>();

    private Date currentDate;
    private Date limitDate;
    private GregorianCalendar calendar;


    private ArrayList<AID> investorAgents;
    //private AID[] playerAgents;


    public void setup(){

        //TODO USE SUBSCRIPTION INITIATOR AND RESPONDER FOR COM
        //System.out.println("Agent Informer Created");

        calendar = new GregorianCalendar(1990,11,25);
        currentDate = calendar.getTime();

        limitDate= new GregorianCalendar(2016,11,1).getTime();

        setCompaniesMap();

        // Register the informer service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("informer");
        sd.setName("JADE-informer");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }


        // Search for all Investor Agents

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd2 = new ServiceDescription();
        sd2.setType("investor");
        template.addServices(sd2);
        int nInv = 0;
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Found the following investor agents:");
            investorAgents = new ArrayList<AID>();
            for (nInv = 0; nInv < result.length; ++nInv) {
                investorAgents.add(result[nInv].getName());
                System.out.println(investorAgents.get(nInv).getName() + "  -  " + investorAgents.size());
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        DFAgentDescription template2 = new DFAgentDescription();
        ServiceDescription sd3 = new ServiceDescription();
        sd3.setType("player");
        template2.addServices(sd3);
        try {
            DFAgentDescription[] result = DFService.search(this, template2);
            System.out.println("Found the following player agents:");
            for (int i = 0; i < result.length; ++i) {
                investorAgents.add(result[i].getName());
                System.out.println(investorAgents.get(i+nInv).getName() + "  -  " + investorAgents.size());
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new InformerAnnouncement());

    }

    public void takeDown(){
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Printout a dismissal message
        //System.out.println("Informer "+getAID().getName()+" terminating.");
    }

    private DayValue getCompanyDayValue(String companyID, Date day){

        return (map.containsKey(companyID) && map.get(companyID).containsKey(day)) ? map.get(companyID).get(day) : null;

    }


    private void setCompaniesMap(){
        String csvDir = "resources/caps/MarketCaps2.csv";
        BufferedReader br = null;
        String marketCapLine = "";
        String csvSplitBy = ",";
        String dateSplitBy = "-";

        try {
            br = new BufferedReader(new FileReader(csvDir));
            while ((marketCapLine = br.readLine()) != null) {
                String[] marketCapData = marketCapLine.split(csvSplitBy);

                String csvDirComp = "resources/historicalData/daily/" + marketCapData[0] + "_dailyInfo.csv";
                BufferedReader brComp = null;


                try {
                    if(!(new File(csvDirComp).exists())) continue;
                    brComp = new BufferedReader(new FileReader(csvDirComp));

                    HashMap<Date, DayValue> internalHash = new HashMap<Date, DayValue>();

                    while ((marketCapLine = brComp.readLine()) != null) {
                        String[] marketData = marketCapLine.split(csvSplitBy);
                        if(marketData.length == 7){
                            String[] dateInfo = marketData[0].split(dateSplitBy);

                            Date date =
                                    new GregorianCalendar(
                                            Integer.parseInt(dateInfo[0]),
                                            Integer.parseInt(dateInfo[1])-1,
                                            Integer.parseInt(dateInfo[2])
                                    ).getTime();

                            DayValue dv = new DayValue();
                            dv.setDayHigh(Double.parseDouble(marketData[2]));
                            dv.setDayLow(Double.parseDouble(marketData[3]));
                            dv.setClosePrice(Double.parseDouble(marketData[4]));
                            dv.setVolume(Integer.parseInt(marketData[5]));

                            internalHash.put(date,dv);

                        }
                    }
                    map.put(marketCapData[0] , internalHash);
                    mapKeys.add(marketCapData[0]);
                    brComp.close();

                    System.out.println("Parsed " + marketCapData[0] + " data");

                }
                catch (Exception e){
                    System.err.println("ERR: " + e);
                    System.out.println("Error loading company " + marketCapData[0]);
                }

            }

            br.close();

        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    private class InformerAnnouncement extends OneShotBehaviour {


        private MessageTemplate mt; // The template to receive replies
        private int step = 0;

        private String buildMessage(){
            String msg = dateToString(currentDate);

            for (int i = 0 ; i < mapKeys.size() ; i++){
                if(map.containsKey(mapKeys.get(i)) && map.get(mapKeys.get(i)).containsKey(currentDate))
                    msg += "/" + mapKeys.get(i) + "," + map.get(mapKeys.get(i)).get(currentDate).getData();
            }

            return msg;
        }

        private boolean allConfirmed(boolean[] arr){
            int i;
            for(i = 0 ; i<arr.length ; i++){
                if(!arr[i]) return false;
            }
            return true;
        }

        public void action() {

            //System.out.println("\n\n\n------------------------\nTrying to get company Info:\n------------------------\n\n");

            boolean[] replyConfirmations = new boolean[investorAgents.size()];

            for(int i = 0 ; i < replyConfirmations.length ; i++){
                replyConfirmations[i] = false;
            }

            while (currentDate.before(limitDate)) {

                switch (step) {
                    case 0:
                        // Send the information to all investors
                        ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
                        for (int i = 0; i < investorAgents.size(); ++i) {
                            cfp.addReceiver(investorAgents.get(i));
                        }
                        cfp.setContent(buildMessage());
                        cfp.setConversationId("cmpData");

                        cfp.setReplyWith("inf" + System.currentTimeMillis()); // Unique value
                        myAgent.send(cfp);
                        // Prepare the template to get confirmations
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("cmpData"),
                                MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                        step = 1;
                        break;
                    case 1:
                        // Check if all investors received the information
                        ACLMessage reply = myAgent.receive(mt);
                        if (reply != null) {
                            // Reply received
                            if (reply.getPerformative() == ACLMessage.CONFIRM) {

                                //System.out.println("Received reply from " + reply.getSender().getName());

                                for (int i = 0; i < investorAgents.size(); ++i) {
                                    if(investorAgents.get(i).equals(reply.getSender()) && reply.getContent().equals(dateToString(currentDate))){
                                        replyConfirmations[i] = true;
                                    }
                                }

                                if(allConfirmed(replyConfirmations)) {
                                    step = 2;
                                }
                            }
                        } else {
                            block();
                        }
                        break;
                    case 2:
                        //It's a new fucking day! Rise and shine!
                        calendar.add(calendar.DAY_OF_MONTH , 1);
                        currentDate = calendar.getTime();

                        for(int i = 0 ; i < replyConfirmations.length ; i++){
                            replyConfirmations[i] = false;
                        }

                        step = 0;
                        break;
                }

            }


        }
    }



    private Date stringToDate(String info){
            String[] dateInfo = info.split("-");

            return new GregorianCalendar(
                            Integer.parseInt(dateInfo[0]),
                            Integer.parseInt(dateInfo[1]),
                            Integer.parseInt(dateInfo[2])
                    ).getTime();
    }


    private String dateToString(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);

    }



}