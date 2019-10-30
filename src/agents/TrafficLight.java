package agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class TrafficLight extends Agent {

    /*
        TrafficLight agent nickname.
     */
    private String agentNickname;
    private int x;
    private int y;


    public TrafficLight(int x, int y){

        this.x = x;
        this.y = y;
    }

    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected void setup(){

        int pos = getAID().getName().indexOf("@");
        agentNickname = getAID().getName().substring(0, pos);
        System.out.println("TrafficLight-agent " + agentNickname + " has started!");

        registerYellowPages();

        addBehaviour(new GiveTrafficLightInfo());
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
            String tlInfo = agentNickname + " " + x + " " + y;
            reply.setContent(tlInfo);
            myAgent.send(reply);

            done = true;
        }

        public boolean done(){

            return done;
        }
    }
}
