package agents;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Hashtable;


public class Car extends Vehicle{

    private DFAgentDescription template;

    private int priority;
    private Hashtable<Integer, int[]> tlInfo;


    public Car(int startingNode, int targetNode, int priority){

        this.startingNode = startingNode;
        this.targetNode = targetNode;
        this.priority = priority;
        this.tlInfo = new Hashtable<>();
    }

    /*
       Method that is a placeholder for agent specific startup code.
     */
    @Override
    protected void setup(){

        int pos = getAID().getName().indexOf("@");
        agentNickname = getAID().getName().substring(0, pos);
        System.out.println("Car-agent " + agentNickname + " has started!");

        template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("communication_car_tl");
        template.addServices(sd);

        addBehaviour(new FindTrafficLights());
        addBehaviour(new FindTrafficLightsInfo());
    }

    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    @Override
    protected void takeDown(){

        System.out.println("Car-agent " + agentNickname + " has terminated!");
    }


    /*
        Inner Class. Used to find all the traffic lights
     */
    private class FindTrafficLights extends SimpleBehaviour{

        private boolean done = false;

        @Override
        public void action() {

            try{

                DFAgentDescription[] result = DFService.search(myAgent, template);
                trafficLightsAgents = new AID[result.length];
                for(int i = 0; i < result.length; i++) {

                    trafficLightsAgents[i] = result[i].getName();
                }
            }
            catch(FIPAException fe){

                fe.printStackTrace();
            }

            done = true;
        }

        @Override
        public boolean done() {

            return done;
        }
    }


    /*
        Inner Class. Used to exchange messages to find traffic lights information
     */
    private class FindTrafficLightsInfo extends Behaviour {

        private int step = 0;
        private MessageTemplate msgTemp;

        @Override
        public void action() {

            switch(step){

                case 0:             // Send REQUEST for tl info

                    ACLMessage requestInfo = new ACLMessage(ACLMessage.REQUEST);
                    for (AID trafficLightsAgent : trafficLightsAgents) {

                        requestInfo.addReceiver(trafficLightsAgent);
                    }
                    requestInfo.setConversationId("tl_info");
                    requestInfo.setReplyWith("request" + System.currentTimeMillis()); // To ensure unique values
                    myAgent.send(requestInfo);
                    msgTemp = MessageTemplate.and(MessageTemplate.MatchConversationId("tl_info"),
                            MessageTemplate.MatchInReplyTo(requestInfo.getReplyWith()));

                    step = 1;
                    break;

                case 1:             // Receive INFORM for tl info

                    int i = 0;
                    while(i < trafficLightsAgents.length){

                        ACLMessage reply = myAgent.receive(msgTemp);
                        if(reply != null){

                            if(reply.getPerformative() == ACLMessage.INFORM){

                                String content = reply.getContent();

                                int pos0 = content.indexOf(" ");
                                int id = Integer.parseInt(content.substring(2, pos0));
                                int pos1 = content.indexOf(" ", pos0 + 1);
                                int x = Integer.parseInt(content.substring(pos0+1, pos1));
                                int y = Integer.parseInt(content.substring(pos1+1));

                                int[] temp = {x, y};
                                tlInfo.put(id, temp);
                            }
                            i++;
                        }
                        else{

                            block();
                        }
                    }

                    step = 2;
            }
        }

        public boolean done(){

            return (step == 2);
        }
    }
}
