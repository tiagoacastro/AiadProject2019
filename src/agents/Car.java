package agents;

import app.*;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.StringACLCodec;

import java.io.StringReader;
import java.util.ArrayList;
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
        this.pos = new Pos(startingNode.getX(), startingNode.getY());
        this.priorityPoints = priorityPoints;

        definePath();
    }

    /*
       Method that is a placeholder for agent specific startup code.
     */
    @Override
    protected void setup(){
        Map.newMap[pos.y][pos.x] = 'X';
        addBehaviour(new Decide(this, Main.tick));
    }

    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    @Override
    protected void takeDown(){

        System.out.println(getAID().getName().substring(0, getAID().getName().indexOf("@")) + " has terminated!");
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

        Graph.resetNodesVisited();
    }

    private void chooseNext(Pos pos) {
        switch(path[currentEdge].getDirection()){
            case NORTH:
                pos.y--;
                break;
            case SOUTH:
                pos.y++;
                break;
            case EAST:
                pos.x++;
                break;
            case WEST:
                pos.x--;
                break;
            default:
                break;
        }
    }

    private class Decide extends TickerBehaviour {
        Decide(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if(waiting)
                Map.newMap[pos.y][pos.x] = 'X';
            else {
                Pos aux = new Pos(pos.x,pos.y);
                chooseNext(aux);

                while(Map.oldMap[aux.y][aux.x] == 'X')
                    chooseNext(aux);

                switch(Map.oldMap[aux.y][aux.x]){
                    case '|': case '-':
                        addBehaviour(new Move());
                        break;
                    case 'O':
                        addBehaviour(new Inform());
                        Map.newMap[pos.y][pos.x] = 'X';
                        waiting = true;
                        break;
                }
            }
        }
    }

    private class Move extends OneShotBehaviour {
        @Override
        public void action(){
            if(!(pos.x == path[path.length-1].getEnd().getX() && pos.y == path[path.length-1].getEnd().getY())){

                chooseNext(pos);
                Map.newMap[pos.y][pos.x] = 'X';

                if(pos.x == path[currentEdge].getEnd().getX() && pos.y == path[currentEdge].getEnd().getY()){

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

            addBehaviour(new Auction());
        }
    }

    /*
        Inner Class. Used when in Auction
     */
    private class Auction extends Behaviour {
        int step = 0;
        ArrayList<AID> carsBehindAID = null;
        int currentMaxPriorityPoints = 0;
        AID tlAid = null;
        int retryVal = 0;
        boolean retry = false;

        @Override
        public void action() {

            switch(step){
                case 0:         // Receive CFP, either one with info (first car) or empty (other cars)
                    MessageTemplate msgTempCfp = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                    ACLMessage cfpMsgTL = myAgent.receive(msgTempCfp);
                    if(cfpMsgTL != null){
                        String content = cfpMsgTL.getContent();

                        if(content != null){                // First car of the queue
                            System.out.println("First car (" + getAID().getName().substring(0, getAID().getName().indexOf("@")) + ") of the queue received the CFP");
                            tlAid = cfpMsgTL.getSender();

                            int pos = content.indexOf("|");
                            currentMaxPriorityPoints = Integer.parseInt(content.substring(0, pos));
                            String aids = content.substring(pos + 1);

                            carsBehindAID = new ArrayList<>();
                            int posX = aids.indexOf('|');

                            while(posX != -1){
                                StringACLCodec codec = new StringACLCodec(new StringReader(aids.substring(0, posX)), null);
                                AID tempAid = null;

                                try {
                                    tempAid = codec.decodeAID();
                                } catch (ACLCodec.CodecException e) {
                                    e.printStackTrace();
                                }

                                carsBehindAID.add(tempAid);
                                aids = aids.substring(posX+1);
                                posX = aids.indexOf('|');
                            }

                            step = 1;
                        }
                        else{                               // Cars behind the first of the queue
                            System.out.println("Car behind the first received CFP from the first");
                            ACLMessage replyToFirstCarMsg = cfpMsgTL.createReply();
                            replyToFirstCarMsg.setPerformative(ACLMessage.PROPOSE);
                            replyToFirstCarMsg.setContent(String.valueOf(choosePriorityPoints()));
                            myAgent.send(replyToFirstCarMsg);
                            System.out.println("Car behind the first one sent his PROPOSE");
//                            content = "";
//
//                            while(content != null) {
//                                cfpMsgTL = myAgent.receive(msgTempCfp);
//
//                                if (cfpMsgTL != null) {
//                                    content = cfpMsgTL.getContent();
//
//                                    if (content != null) {
//                                        System.out.println("Car behind the first received CFP from the first to give more points");
//                                        replyToFirstCarMsg = cfpMsgTL.createReply();
//                                        replyToFirstCarMsg.setPerformative(ACLMessage.PROPOSE);
//                                        replyToFirstCarMsg.setContent(String.valueOf(choosePriorityPoints()));
//                                        myAgent.send(replyToFirstCarMsg);
//                                        System.out.println("Car behind the first one sent his PROPOSE");
//                                    }
//                                }
//                            }


                            step = 0;
                        }
                    }
                    else{
                        block();
                    }

                    break;
                case 1:         // Car sends CFP to the cars behind it in the queue
                    ACLMessage cfpMsgCar = new ACLMessage(ACLMessage.CFP);
                    for(int i = 0; i < carsBehindAID.size(); i++){
                        cfpMsgCar.addReceiver(carsBehindAID.get(i));
                    }
                    if(retry)
                        cfpMsgCar.setContent(String.valueOf(retryVal));
                    cfpMsgCar.setConversationId("car_car_auction");
                    cfpMsgCar.setReplyWith("cfp" + System.currentTimeMillis());
                    myAgent.send(cfpMsgCar);
                    System.out.println("First car sent CFP to the ones behind him");

                    step = 2;
                    break;
                case 2:         // Receive every PROPOSE and send to TL the total sum of the PriorityPoints
                    MessageTemplate msgTempPropose = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                    int totalProposedPP = 0;
                    int i = 0;
                    while(i < carsBehindAID.size()){
                        ACLMessage proposeMsg = myAgent.receive(msgTempPropose);

                        if(proposeMsg != null){
                            totalProposedPP += Integer.parseInt(proposeMsg.getContent());
                            i++;
                        }
                        else{
                            block();
                        }
                    }
                    System.out.println("First car received the PROPOSE from each of the behind ones");

                    ACLMessage replyToTL;
                    if(totalProposedPP > currentMaxPriorityPoints){
                        replyToTL = new ACLMessage(ACLMessage.PROPOSE);
                        replyToTL.setContent(String.valueOf(totalProposedPP));
//                        replyToTL.setConversationId("car_tl_auction");
//                        replyToTL.setReplyWith("proposal" + System.currentTimeMillis());
//                        replyToTL.addReceiver(tlAid);
//                        myAgent.send(replyToTL);
//                        System.out.println("First car sent the PROPOSE to the tl");
//
//                        cfpMsgCar = new ACLMessage(ACLMessage.CFP);
//                        for(int j = 0; j < carsBehindAID.size(); j++){
//                            cfpMsgCar.addReceiver(carsBehindAID.get(i));
//                        }
//                        cfpMsgCar.setConversationId("car_car_auction");
//                        cfpMsgCar.setReplyWith("cfp" + System.currentTimeMillis());
//                        myAgent.send(cfpMsgCar);
//                        System.out.println("First car sent to other cars to say he wont need higher values");
                    }
                    else{
                        replyToTL = new ACLMessage(ACLMessage.REFUSE);
//                        if(totalProposedPP > retryVal){
//                            retryVal = totalProposedPP;
//                            retry = true;
//                            step = 1;
//                            break;
//                        } else {
//                            replyToTL = new ACLMessage(ACLMessage.REFUSE);
//                            replyToTL.setConversationId("car_tl_auction");
//                            replyToTL.setReplyWith("proposal" + System.currentTimeMillis());
//                            replyToTL.addReceiver(tlAid);
//                            myAgent.send(replyToTL);
//                            System.out.println("First car sent the PROPOSE to the tl");
//
//                            cfpMsgCar = new ACLMessage(ACLMessage.CFP);
//                            for(int j = 0; j < carsBehindAID.size(); j++){
//                                cfpMsgCar.addReceiver(carsBehindAID.get(i));
//                            }
//                            cfpMsgCar.setConversationId("car_car_auction");
//                            cfpMsgCar.setReplyWith("cfp" + System.currentTimeMillis());
//                            myAgent.send(cfpMsgCar);
//                            System.out.println("First car sent to other cars to say he wont need higher values");
//                        }
                    }

                    replyToTL.setConversationId("car_tl_auction");
                    replyToTL.setReplyWith("proposal" + System.currentTimeMillis());
                    replyToTL.addReceiver(tlAid);
                    myAgent.send(replyToTL);
                    System.out.println("First car sent the PROPOSE to the tl");
                    waiting = false;

                    step = 3;
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean done() {

            return (step == 3);
        }
    }

}
