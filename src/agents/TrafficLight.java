package agents;

import app.Main;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;


public class TrafficLight extends Agent {
    private AID aid;
    private ArrayList<AID>[] lanes = new ArrayList[4];
    private ArrayList<AID> lanesStillInAuction;

    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected void setup(){
        this.aid = getAID();

        for (int i = 0; i < 4; i++) {
            lanes[i] = new ArrayList<>();
        }

        registerYellowPages();

        addBehaviour(new Listen());
        addBehaviour(new StartAuction(this, Main.tick * 10));
    }

    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    protected void takeDown(){

        System.out.println(getAID().getName().substring(0, getAID().getName().indexOf("@")) + " has terminated!");
    }

    public AID getAid(){
        return this.aid;
    }

    /*
        Method that takes care of the Traffic Light's registration in the yellow pages.
     */
    private void registerYellowPages(){

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("communication_car_tl");
        sd.setName("JADE-tl");
        dfd.addServices(sd);
        try{

            DFService.register(this, dfd);
        }
        catch(FIPAException fe){

            fe.printStackTrace();
        }
    }

    /*
        Inner Class. Used to always be listening to vehicles INFORM messages
     */
    private class Listen extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate msgTemp = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(msgTemp);

            if (msg != null) {

                switch (msg.getContent()) {
                    case "NORTH":
                        if(!lanes[0].contains(msg.getSender()))
                            lanes[0].add(msg.getSender());
                        break;
                    case "EAST":
                        if(!lanes[1].contains(msg.getSender()))
                            lanes[1].add(msg.getSender());
                        break;
                    case "SOUTH":
                        if(!lanes[2].contains(msg.getSender()))
                            lanes[2].add(msg.getSender());
                        break;
                    case "WEST":
                        if(!lanes[3].contains(msg.getSender()))
                            lanes[3].add(msg.getSender());
                        break;
                }
            } else {
                block();
            }
        }
    }

    private class StartAuction extends TickerBehaviour {

        public StartAuction(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {

            lanesStillInAuction = new ArrayList<>();
            for(int i = 0; i < lanes.length; i++){

                if(lanes[i].size() != 0){

                    lanesStillInAuction.add(lanes[i].get(0));
                }
            }

            addBehaviour(new Auction());
        }
    }

    private class Auction extends Behaviour {

        int i = 0;
        int maxPP = 0;
        AID maxPPLane;
        boolean agreement = false;

        @Override
        public void action() {

            int step = 0;
            while(i < lanesStillInAuction.size()){

                switch(step){

                    case 0:

                        ACLMessage cfpMsg = new ACLMessage(ACLMessage.CFP);
                        cfpMsg.addReceiver(lanesStillInAuction.get(i));
                        cfpMsg.setConversationId("tl_car_auction");
                        cfpMsg.setReplyWith("cfp" + System.currentTimeMillis());

                        int lane = 0;
                        for(; lane < lanes.length; lane++){
                            if(lanes[lane].size() != 0) {
                                if ((lanesStillInAuction.get(i)).equals(lanes[lane].get(0)))
                                    break;
                            }
                        }
                        String content = maxPP + "|";
                        for (int k = 1; k < lanes[lane].size(); k++) {
                            content += lanes[lane].get(k).toString() + "|";
                        }
                        cfpMsg.setContent(content);
                        myAgent.send(cfpMsg);
                        System.out.println("TL sent CFP to " + lanesStillInAuction.get(i).getName().substring(0, lanesStillInAuction.get(0).getName().indexOf('@')));

                        step = 1;
                        break;


                    case 1:

                        MessageTemplate proposeTemp = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                        MessageTemplate refuseTemp = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);
                        MessageTemplate bothTemp = MessageTemplate.or(proposeTemp, refuseTemp);

                        ACLMessage reply = myAgent.receive(bothTemp);

                        if(reply != null) {

                            if(reply.getPerformative() == ACLMessage.PROPOSE) {

                                maxPP = Integer.parseInt(reply.getContent());
                                maxPPLane = reply.getSender();

                                System.out.println("TL received PROPOSE (" + reply.getContent() + ") by " + reply.getSender().getName().substring(0, reply.getSender().getName().indexOf('@')) + " to increase the max PP\n");
                            } else if(reply.getPerformative() == ACLMessage.REFUSE) {

                                lanesStillInAuction.remove(reply.getSender());
                                System.out.println("TL received REFUSE by " + reply.getSender().getName().substring(0, reply.getSender().getName().indexOf('@')) + " to increase the max PP\n");
                            }

                            step = 0;
                            i++;
                        }
                        else {

                            block();
                        }

                        break;
                }
            }

            if(lanesStillInAuction.size() == 1){

                ACLMessage acceptPropMsg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                acceptPropMsg.addReceiver(lanesStillInAuction.get(0));
                acceptPropMsg.setConversationId("accept_proposal_auction");
                acceptPropMsg.setReplyWith("accept_proposal" + System.currentTimeMillis());
                System.out.println("TL sent ACCEPT_PROPOSAL to " + lanesStillInAuction.get(0).getName());
                myAgent.send(acceptPropMsg);


                ACLMessage rejectPropMsg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                for(int j = 0; j < lanes.length; j++){

                    if(lanes[j].size() != 0){
                        if(!((lanes[j].get(0).equals(lanesStillInAuction.get(0))))){

                            rejectPropMsg.addReceiver(lanes[j].get(0));
                            System.out.println("TL sent REJECT_PROPOSAL to " + lanes[j].get(0).getName());
                        }
                        else{

                            lanes[j] = new ArrayList<>();
                        }
                    }
                }
                rejectPropMsg.setConversationId("reject_proposal_auction");
                rejectPropMsg.setReplyWith("reject_proposal" + System.currentTimeMillis());
                myAgent.send(rejectPropMsg);

                agreement = true;
            }
            else{

                i = 0;
            }
        }

        @Override
        public boolean done() {

            return agreement;
        }
    }

    /*
    private class AuctionCastro extends TickerBehaviour {
        int maxPriorityPoints = 0;
        boolean done = false;
        int chosen;

        AuctionCastro(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onTick() {
            int i;

            if(alternateLane)
                i = 0;
            else
                i = 3;

            while(!done){
                if(cars[i].size() != 0){
                    ACLMessage cfpMsg = new ACLMessage(ACLMessage.CFP);
                    cfpMsg.addReceiver(cars[i].get(0));
                    cfpMsg.setConversationId("tl_car_auction");
                    cfpMsg.setReplyWith("cfp" + System.currentTimeMillis());

                    StringBuilder content = new StringBuilder();
                    content.append(maxPriorityPoints);
                    content.append("|");
                    for(int k = 1; k < cars[i].size(); k++){
                        content.append(cars[i].get(k).toString());
                        content.append("|");
                    }
                    cfpMsg.setContent(content.toString());

                    myAgent.send(cfpMsg);
                    System.out.println("TL sent CFP to each of the first cars");

                    MessageTemplate proposeTemp = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                    MessageTemplate refuseTemp = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);
                    MessageTemplate bothTemp = MessageTemplate.or(proposeTemp, refuseTemp);

                    ACLMessage reply = myAgent.receive(bothTemp);

                    if(reply != null){
                        if(reply.getPerformative() == ACLMessage.PROPOSE){
                            int proposedPP = Integer.parseInt(reply.getContent());
                            if(proposedPP > maxPriorityPoints){
                                maxPriorityPoints = proposedPP;
                            }
                            chosen = i;
                            System.out.println("TL received proposal to increase the max PP");
                        }
                        else if(reply.getPerformative() == ACLMessage.REFUSE){
                            done = true;
                            alternateLane = !alternateLane;
                            System.out.println("TL received refusal to increase the max PP");
                        }
                    }
                    else{
                        block();
                    }
                }

                if(alternateLane){
                    i++;
                    if(i > 3)
                        i = 0;
                } else {
                    i--;
                    if(i < 0)
                        i = 3;
                }
            }
        }
    }
    */
}
