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
    private ArrayList<AID>[] cars = new ArrayList[4];
    private boolean alternateLane = true;

    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected void setup(){
        this.aid = getAID();

        for (int i = 0; i < 4; i++) {
            cars[i] = new ArrayList<>();
        }

        registerYellowPages();

        addBehaviour(new Listen());
        addBehaviour(new StartAuction(this, Main.tick * 10));
    }

    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    protected void takeDown(){

        System.out.println("TrafficLight-agent has terminated!");
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
                        if(!cars[0].contains(msg.getSender()))
                            cars[0].add(msg.getSender());
                        break;
                    case "EAST":
                        if(!cars[1].contains(msg.getSender()))
                            cars[1].add(msg.getSender());
                        break;
                    case "SOUTH":
                        if(!cars[2].contains(msg.getSender()))
                            cars[2].add(msg.getSender());
                        break;
                    case "WEST":
                        if(!cars[3].contains(msg.getSender()))
                            cars[3].add(msg.getSender());
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

            addBehaviour(new Auction());
        }
    }

    private class Auction extends Behaviour {


        int i = 0;
        int lanesInAuction = 0;
        int maxPriorityPoints = 0;

        @Override
        public void action() {

            int step = 0;
            while(i < cars.length){

                switch(step){

                    case 0:

                        if(cars[i].size() != 0){

                            lanesInAuction++;

                            ACLMessage cfpMsg = new ACLMessage(ACLMessage.CFP);
                            cfpMsg.addReceiver(cars[i].get(0));
                            cfpMsg.setConversationId("tl_car_auction");
                            cfpMsg.setReplyWith("cfp" + System.currentTimeMillis());

                            String content = maxPriorityPoints + "|";
                            for (int k = 1; k < cars[i].size(); k++) {

                                content += cars[i].get(k).toString() + "|";
                            }

                            cfpMsg.setContent(content);
                            myAgent.send(cfpMsg);
                            System.out.println("TL sent CFP to one of the first cars");

                            step = 1;
                        }
                        else{

                            i++;
                        }
                        break;


                    case 1:

                        MessageTemplate proposeTemp = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                        MessageTemplate refuseTemp = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);
                        MessageTemplate bothTemp = MessageTemplate.or(proposeTemp, refuseTemp);

                        ACLMessage reply = myAgent.receive(bothTemp);

                        if (reply != null) {

                            if (reply.getPerformative() == ACLMessage.PROPOSE) {

                                int proposedPP = Integer.parseInt(reply.getContent());
                                if (proposedPP > maxPriorityPoints) {

                                    maxPriorityPoints = proposedPP;
                                }

                                System.out.println("TL received proposal to increase the max PP");
                            } else if (reply.getPerformative() == ACLMessage.REFUSE) {

                                System.out.println("TL received refusal to increase the max PP");
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
        }

        @Override
        public boolean done() {

            return (i == 4);
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
