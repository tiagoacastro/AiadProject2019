package agents;

import jade.core.behaviours.Behaviour;


public class Car extends Vehicle{
    /*
        Vehicle Remaining Priority Points
     */
    private int priorityPoints;

    /*
        Car Constructor
     */
    public Car(int startingNode, int targetNode, int priorityPoints){
        this.startingNode = startingNode;
        this.targetNode = targetNode;
        this.priorityPoints = priorityPoints;

        //TODO: implement djikstra
    }

    /*
       Method that is a placeholder for agent specific startup code.
     */
    @Override
    protected void setup(){
        addBehaviour(new Decide());
    }

    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    @Override
    protected void takeDown(){
        System.out.println("Car-agent has terminated!");
    }

    /*
        TODO
        Method where the agent decides how many Priority Points he is gonna use to pass in a Traffic Light
     */
    private int choosePriorityPoints(){
        return priorityPoints;
    }

    private class Decide extends Behaviour {
        @Override
        public void action(){
            //TODO: Decide entre: andar, esperar na fila, auction
        }

        public boolean done(){
            return false;
        }
    }

    /*
        Inner Class. Used to query a Traffic Light if can pass
     *//*
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
    */

    /*
        Inner Class. Used when in Auction
     *//*
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
    */
}
