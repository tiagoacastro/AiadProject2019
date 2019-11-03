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

    /*
        Directory Facilitator Agent Description
     */
    private DFAgentDescription template;
    /*
        Vehicle Remaining Priority Points
     */
    private int priorityPoints;
    /*
        Hashtable with the information of each Traffic Light
     */
    private Hashtable<Integer, int[]> tlInfo;





    /*
        Car Constructor
     */
    public Car(int startingNode, int targetNode, int priorityPoints){

        this.startingNode = startingNode;
        this.targetNode = targetNode;
        this.priorityPoints = priorityPoints;
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

        addBehaviour(new QueryTL());
    }


    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    @Override
    protected void takeDown(){

        System.out.println("Car-agent " + agentNickname + " has terminated!");
    }


    /*
        Getter for a Traffic Light AID
     */
    private AID getTrafficLightAID(int i){

        return trafficLightsAgents[i];
    }


    /*
        TODO
        Method where the agent decides how many Priority Points he is gonna use to pass in a Traffic Light
     */
    private int choosePriorityPoints(){

        return priorityPoints;
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


    /*
        Inner Class. Used to query a Traffic Light if can pass
     */
    private class QueryTL extends Behaviour{

        private MessageTemplate msgTemp;
        private int step = 0;

        @Override
        public void action(){

            switch(step) {

                case 0:

                    ACLMessage queryMsg = new ACLMessage(ACLMessage.QUERY_IF);
                    queryMsg.addReceiver(getTrafficLightAID(1));

                    queryMsg.setConversationId("query_passage");
                    queryMsg.setReplyWith("query_passage" + System.currentTimeMillis()); // To ensure unique values
                    myAgent.send(queryMsg);
                    msgTemp = MessageTemplate.and(MessageTemplate.MatchConversationId("query_passage"),
                            MessageTemplate.MatchInReplyTo(queryMsg.getReplyWith()));

                    step++;
                    break;

                case 1:

                    ACLMessage reply = myAgent.receive(msgTemp);
                    if(reply != null){

                        String content = reply.getContent();
                        if(content.equals("PASS")){

                            System.out.println("Carro pode passar");
                        }
                        else{

                            addBehaviour(new Auction());
                        }
                        step++;
                    }
                    else{

                        block();
                    }
                    break;
            }

        }

        public boolean done(){

            return (step == 2);
        }
    }


    /*
        Inner Class. Used when in Auction
     */
    private class Auction extends Behaviour{

        private MessageTemplate msgTemp;
        private int step = 0;

        @Override
        public void action(){

            switch(step){

                case 0:

                    ACLMessage proposalMsg = new ACLMessage(ACLMessage.PROPOSE);
                    proposalMsg.addReceiver(getTrafficLightAID(1));
                    proposalMsg.setConversationId("auction");
                    proposalMsg.setReplyWith("auction" + System.currentTimeMillis()); // To ensure unique values
                    proposalMsg.setContent(String.valueOf(choosePriorityPoints()));
                    myAgent.send(proposalMsg);
                    msgTemp = MessageTemplate.and(MessageTemplate.MatchConversationId("auction"),
                            MessageTemplate.MatchInReplyTo(proposalMsg.getReplyWith()));

                    step++;
                    break;

                case 1:

                    ACLMessage reply = myAgent.receive(msgTemp);
                    if(reply != null){

                        if(reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){

                            System.out.println("Proposta aceite. Carro pode passar");
                        }
                        else if(reply.getPerformative() == ACLMessage.REJECT_PROPOSAL){

                            System.out.println("Proposta NAO aceite. Carro NAO pode passar");
                        }
                        step++;
                    }
                    else{

                        block();
                    }
                    break;
            }
        }

        public boolean done(){

            return (step == 2);
        }
    }
}
