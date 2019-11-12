package agents;

import app.GraphEdge;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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

    /*
        Inner Class. Used when in auction
    *//*
    private class Auction extends Behaviour{

        private MessageTemplate msgTemp;
        private boolean done = false;

        @Override
        public void action(){

            msgTemp = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage proposal = myAgent.receive(msgTemp);
            while(proposal == null){

                proposal = myAgent.receive(msgTemp);
            }

            ACLMessage reply = proposal.createReply();
//            if(vehicleCanPass()){
            if(true){

                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            }
            else{

                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            }
            myAgent.send(reply);

            done = true;
        }

        public boolean done(){

            return done;
        }
    }
    */
}
