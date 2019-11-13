package agents;

import app.Graph;
import app.GraphEdge;
import app.GraphNode;

import app.Map;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class Car extends Vehicle{
    /*
        Vehicle Remaining Priority Points
     */
    private int priorityPoints;

    /*
        Car Constructor
     */
    public Car(GraphNode startingNode, GraphNode targetNode, int priorityPoints){
        this.startingNode = startingNode;
        this.targetNode = targetNode;
        this.x = startingNode.getX();
        this.y = startingNode.getY();
        this.priorityPoints = priorityPoints;

        definePath();

        for(int i = 0; i < this.path.length; i++){

            System.out.println(this.path[i]);
        }
    }

    /*
       Method that is a placeholder for agent specific startup code.
     */
    @Override
    protected void setup(){
        Map.newMap[y][x] = 'X';
        addBehaviour(new Decide(this, 2000));
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

    private void definePath(){

        HashMap<Integer, GraphNode> nodePath = new HashMap<>();

        String pathString = Graph.DijkstraShortestPath(startingNode, targetNode);
        int pos = pathString.indexOf(" ");
        int counter = 0;
        while(pos != -1){

            String subString = pathString.substring(0, pos);
            nodePath.put(counter, Graph.nodes.get(Integer.parseInt(subString)));
            counter++;
            pathString = pathString.substring(pos+1, pathString.length());
            pos = pathString.indexOf(" ");
        }
        nodePath.put(counter, Graph.nodes.get(Integer.parseInt(pathString)));

        this.path = new GraphEdge[nodePath.size()-1];
        for(int i = 0; i < nodePath.size()-1; i++){
            for(int k = 0; k < nodePath.get(i).edges.size(); k++){

                if(nodePath.get(i).edges.get(k).getEnd() == nodePath.get(i+1)){

                    this.path[i] = nodePath.get(i).edges.get(k);
                }
            }
        }
    }

    private class Decide extends TickerBehaviour {
        Decide(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            addBehaviour(new Move());
        }
    }

    private class Move extends OneShotBehaviour {
        @Override
        public void action(){
            if(!(x == path[path.length-1].getEnd().getX() && y == path[path.length-1].getEnd().getY())){

                switch(path[currentEdge].getDirection()){

                    case NORTH:
                        y--;
                        break;

                    case SOUTH:
                        y++;
                        break;

                    case EAST:
                        x++;
                        break;

                    case WEST:
                        x--;
                        break;

                    default:
                        break;
                }
                Map.newMap[y][x] = 'X';

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS");
                System.out.println( "Car: " + sdf.format(cal.getTime()) );

                if(x == path[currentEdge].getEnd().getX() && y == path[currentEdge].getEnd().getY()){

                    currentEdge++;
                }
            }
            else{

                myAgent.doDelete();
            }
        }
    }

    private class Inform extends OneShotBehaviour {
        @Override
        public void action(){
            ACLMessage informMsg = new ACLMessage(ACLMessage.INFORM);
            informMsg.addReceiver(path[currentEdge].getTlAid());
            informMsg.setConversationId("inform");
            informMsg.setReplyWith("inform" + System.currentTimeMillis()); // To ensure unique values
            informMsg.setContent(String.valueOf(path[currentEdge].getDirection()));
            myAgent.send(informMsg);
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
