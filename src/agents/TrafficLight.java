package agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class TrafficLight extends Agent {
    /*
        TrafficLight agent nickname
     */
    private String agentNickname;

    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected void setup(){

        int pos = getAID().getName().indexOf("@");
        agentNickname = getAID().getName().substring(0, pos);

        registerYellowPages();

        addBehaviour(new GiveTrafficLightInfo());

        addBehaviour(new ListenToVehicles());
    }

    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    protected void takeDown(){

        System.out.println("TrafficLight-agent " + agentNickname + " has terminated!");
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
        TODO
        Method that checks if a car can pass
     */
    private boolean vehicleCanPass(){

        return false;
    }

    /*
        Inner Class. Used to give the own Traffic Light information
     */
    private class GiveTrafficLightInfo extends Behaviour{

        private boolean done = false;

        @Override
        public void action() {

            MessageTemplate msgTemp = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(msgTemp);
            while(msg == null){

                msg = myAgent.receive(msgTemp);
            }

            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            String tlInfo = agentNickname;
            reply.setContent(tlInfo);
            myAgent.send(reply);

            done = true;
        }

        public boolean done(){

            return done;
        }
    }

    /*
        Inner Class. Used to always be listening to vehicles QUERY messages
     */
    private class ListenToVehicles extends CyclicBehaviour {

        @Override
        public void action(){

            MessageTemplate msgTemp = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
            ACLMessage msg = myAgent.receive(msgTemp);
            if(msg != null){

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                String pass;

                if(vehicleCanPass()){
                    pass = "PASS";
                }
                else{

                    pass = "CANT_PASS";
                    reply.setContent(pass);
                    myAgent.send(reply);
                    addBehaviour(new Auction());
                }
            }
            else{

                block();
            }
        }
    }

    /*
        Inner Class. Used when in auction
     */
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
}
