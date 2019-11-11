package agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;


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
        addBehaviour(new Decide(this, 1000));
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

    private class Decide extends TickerBehaviour {
        Decide(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            //TODO: Decide entre: andar, esperar na fila, auction
        }
    }

    private class Move extends Behaviour {
        @Override
        public void action(){
            //TODO mover
        }

        public boolean done(){
            return false;
        }
    }

    private class Wait extends Behaviour {
        @Override
        public void action(){
            //TODO parado a ouvir à espera de auction
        }

        public boolean done(){
            return false;
        }
    }

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
