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
        addBehaviour(new Auction(this, Main.tick * 10));
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
        public void action(){
            MessageTemplate msgTemp = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(msgTemp);

            if(msg != null){

                switch(msg.getContent()){
                    case "NORTH":
                        cars[0].add(msg.getSender());
                        break;
                    case "EAST":
                        cars[1].add(msg.getSender());
                        break;
                    case "SOUTH":
                        cars[2].add(msg.getSender());
                        break;
                    case "WEST":
                        cars[3].add(msg.getSender());
                        break;
                }
            }
            else{
                block();
            }
        }
    }

    private class Auction extends TickerBehaviour {
        int maxPriorityPoints = 0;
        boolean done = false;
        int chosen;

        Auction(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onTick() {
            int i = 0;

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
                }

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
                        System.out.println("TL received refusal to increase the max PP");
                    }
                }
                else{
                    block();
                }

                i++;
                if(i >= 4)
                    i = 0;
            }
        }
    }
}
