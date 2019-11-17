package agents;

import app.*;
import jade.core.Agent;
import jade.core.AID;
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
import java.util.Random;

public abstract class Vehicle extends Agent {
    /**
     *  Vehicle nickname
     */
    private String nickname;
    /**
     *  Vehicle position
     */
    private Pos pos;
    /**
     *  Vehicle starting node.
     */
    private GraphNode startingNode;
    /**
     *  Vehicle target node.
     */
    private GraphNode targetNode;
    /**
     *  Vehicle path.
     */
    GraphEdge[] path;
    /**
     *  Current edge
     */
    int currentEdge = 0;
    /**
     *  Vehicle AID
     */
    private  AID aid = this.getAID();
    /**
     *   If vehicle is waiting and can't move
     */
    private boolean waiting = false;
    /**
     *   Vehicle Remaining Priority Points
     */
    int priorityPoints;
    /**
     *   Represents if vehicle can pass the traffic light
     */
    private boolean pass = false;
    /**
     *   Max tries to improve the auction value
     */
    int maxTries;
    /**
     *   number of try on auction
     */
    int retry = 1;

    /**
     * Vehicle constructor
     * @param startingNode      start
     * @param targetNode        end
     */
    Vehicle(GraphNode startingNode, GraphNode targetNode){
        this.startingNode = startingNode;
        this.targetNode = targetNode;
        this.pos = new Pos(startingNode.getX(), startingNode.getY());
        Random r = new Random();
        this.priorityPoints = r.nextInt(51) + 50; //generate random from 50 to 100 (both inclusive)
        this.maxTries = r.nextInt(5)+1;

        definePath();
    }

    /**
     * Vehicle constructor with chosen priority points and max tries
     * @param startingNode      start
     * @param targetNode        end
     * @param priorityPoints    priority points
     * @param maxTries          max tries
     */
    Vehicle(GraphNode startingNode, GraphNode targetNode, int priorityPoints, int maxTries){
        this.startingNode = startingNode;
        this.targetNode = targetNode;
        this.pos = new Pos(startingNode.getX(), startingNode.getY());
        this.priorityPoints = priorityPoints;
        this.maxTries = maxTries;

        definePath();
    }

    /**
     *  Method that is a placeholder for agent specific startup code.
     */
    @Override
    protected void setup(){
        this.nickname = getAID().getName().substring(0, getAID().getName().indexOf("@"));
        Map.newMap[pos.y][pos.x] = 'X';
        addBehaviour(new Decide(this, Main.tick));
    }

    /**
     *   Method that is a placeholder for agent specific cleanup code.
     */
    @Override
    protected void takeDown(){
        System.out.println(nickname + " has terminated!");
    }

    /**
     *   Method where the agent decides how many Priority Points he is gonna use to pass in a Traffic Light
     *   @return number of points
     */
    abstract int choosePriorityPoints();

    /**
     * Defines the path the vehicle has to take
     */
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

    /**
     * Chooses next position
     * @param pos   position
     */
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

    /**
     * Decides next behaviour
     */
    private class Decide extends TickerBehaviour {
        Decide(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if(pos.x == path[path.length-1].getEnd().getX() && pos.y == path[path.length-1].getEnd().getY())
                myAgent.doDelete();

            try {
                if(waiting)
                    Map.newMap[pos.y][pos.x] = 'X';
                else {
                    Pos aux = new Pos(pos.x,pos.y);
                    chooseNext(aux);

                    if(Map.oldMap[aux.y][aux.x] == 'O' && pass){
                        pass = false;
                        addBehaviour(new Move());
                    } else {
                        while (Map.oldMap[aux.y][aux.x] == 'X')
                            chooseNext(aux);

                        switch (Map.oldMap[aux.y][aux.x]) {
                            case '|':
                            case '-':
                            case '+':
                                addBehaviour(new Move());
                                break;
                            case 'O':
                                if (pass) {
                                    addBehaviour(new Move());
                                } else {
                                    addBehaviour(new Inform());
                                    Map.newMap[pos.y][pos.x] = 'X';
                                    waiting = true;
                                }
                                break;
                        }
                    }
                }
            } catch(ArrayIndexOutOfBoundsException e){
                addBehaviour(new Move());
            }
        }
    }

    /**
     * Moves the vehicle
     */
    private class Move extends OneShotBehaviour {
        @Override
        public void action(){
            chooseNext(pos);
            Map.newMap[pos.y][pos.x] = 'X';

            if(pos.x == path[currentEdge].getEnd().getX() && pos.y == path[currentEdge].getEnd().getY()){
                currentEdge++;
            }
        }
    }

    /**
     * Informs the traffic light that the vehicle is waiting for an auction
     */
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

    /**
     * Auction behaviour for the first and back cars
     */
    private class Auction extends Behaviour {

        int step = 0;
        ArrayList<AID> carsBehind;
        ArrayList<AID> carsStillInAuction;
        HashMap<AID, Integer> carsProposedPP;
        int currentMaxPriorityPoints = 0;
        int lastPriorityPoints = 0;
        AID tlAid;

        @Override
        public void action() {

            switch(step){

                case 0:         // Receive CFP, either one with info (first car) or empty (other cars)

                    MessageTemplate msgTempCfp = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                    MessageTemplate msgTempInform = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                    MessageTemplate msgTempAccProp = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    MessageTemplate msgTempRejProp = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
                    MessageTemplate bothTemp1 = MessageTemplate.or(msgTempCfp, msgTempInform);
                    MessageTemplate bothTemp2 = MessageTemplate.or(msgTempAccProp, msgTempRejProp);
                    MessageTemplate fullTemp = MessageTemplate.or(bothTemp1, bothTemp2);

                    ACLMessage message = myAgent.receive(fullTemp);

                    if(message != null){

                        if(message.getPerformative() == ACLMessage.CFP) {
                            String content = message.getContent();

                            if (content != null) {                // First car of the queue

                                carsBehind = new ArrayList<>();
                                carsStillInAuction = new ArrayList<>();
                                carsProposedPP = new HashMap<>();
                                carsProposedPP.put(getAID(), 0);

                                System.out.println("First car (" + nickname + ") received the TL's CFP");
                                tlAid = message.getSender();

                                currentMaxPriorityPoints = Integer.parseInt(content.substring(0, content.indexOf("|")));
                                String aids = content.substring(content.indexOf("|") + 1);

                                int posX = aids.indexOf('|');

                                while (posX != -1) {

                                    StringACLCodec codec = new StringACLCodec(new StringReader(aids.substring(0, posX)), null);
                                    AID tempAid = null;

                                    try {
                                        tempAid = codec.decodeAID();
                                    } catch (ACLCodec.CodecException e) {
                                        e.printStackTrace();
                                    }

                                    carsBehind.add(tempAid);
                                    carsStillInAuction.add(tempAid);
                                    carsProposedPP.put(tempAid, 0);
                                    aids = aids.substring(posX + 1);
                                    posX = aids.indexOf('|');
                                }

                                step = 1;
                            }
                            else {                               // Cars behind the first of the queue

                                System.out.println(nickname + " behind the first car received its CFP");
                                ACLMessage replyToFirstCarMsg = message.createReply();

                                if (retry <= maxTries) {
                                    int tempPP = choosePriorityPoints();
                                    retry++;
                                    lastPriorityPoints = tempPP;
                                    replyToFirstCarMsg.setPerformative(ACLMessage.PROPOSE);
                                    replyToFirstCarMsg.setContent(String.valueOf(lastPriorityPoints));
                                    System.out.println(nickname + " behind the first car sent his PROPOSE (" + tempPP + ")");
                                } else {
                                    replyToFirstCarMsg.setPerformative(ACLMessage.REFUSE);
                                    System.out.println(nickname + " behind the first car sent his REFUSE");
                                }

                                myAgent.send(replyToFirstCarMsg);

                                step = 0;
                            }
                        }
                        else if(message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL ||
                                message.getPerformative() == ACLMessage.REJECT_PROPOSAL){

                            System.out.println(nickname + " received ACCEPT_PROPOSAL/REJECT_PROPOSAL from the TL");
                            ACLMessage informMsg = new ACLMessage(ACLMessage.INFORM);
                            for(int i = 0; i < carsBehind.size(); i++){
                                informMsg.addReceiver(carsBehind.get(i));
                            }

                            if(message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
                                informMsg.setContent("GO");
                                System.out.println(nickname + " sent INFORM to GO to the ones behind him");

                                priorityPoints -= lastPriorityPoints;
                                waiting = false;
                                pass = true;
                                step = 4;
                                retry = 1;
                            }
                            else {
                                informMsg.setContent("DONT_GO");
                                System.out.println(nickname + " sent INFORM to DONT_GO to the ones behind him");
                                lastPriorityPoints = 0;
                            }
                            informMsg.setConversationId("inform_auction");
                            informMsg.setReplyWith("inform" + System.currentTimeMillis());
                            myAgent.send(informMsg);

                        }
                        else if(message.getPerformative() == ACLMessage.INFORM){

                            if((message.getContent()).equals("GO")){

                                System.out.println(nickname + " received INFORM to GO");
                                waiting = false;
                                pass = true;
                                priorityPoints -= lastPriorityPoints;
                            }
                            else if((message.getContent()).equals("DONT_GO")){

                                System.out.println(nickname + " received INFORM to DONT_GO");
                                lastPriorityPoints = 0;
                                step = 0;
                            }
                        }
                    }
                    else{
                        block();
                    }

                    break;


                case 1:         // First car sends CFP to the cars behind it in the queue

                    ACLMessage cfpMsgCar = new ACLMessage(ACLMessage.CFP);
                    for(int i = 0; i < carsStillInAuction.size(); i++){
                        cfpMsgCar.addReceiver(carsStillInAuction.get(i));
                    }
                    cfpMsgCar.setConversationId("car_car_auction");
                    cfpMsgCar.setReplyWith("cfp" + System.currentTimeMillis());
                    myAgent.send(cfpMsgCar);
                    System.out.println(nickname + " sent CFP to the ones behind him and still in auction");

                    step = 2;
                    break;


                case 2:         // Receive every PROPOSE/REFUSE

                    MessageTemplate proposeTemp = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                    MessageTemplate refuseTemp = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);
                    MessageTemplate bothTemp3 = MessageTemplate.or(proposeTemp, refuseTemp);

                    int i = 0;
                    while(i < carsStillInAuction.size()){
                        ACLMessage proposeMsg = myAgent.receive(bothTemp3);

                        if(proposeMsg != null){

                            if (proposeMsg.getPerformative() == ACLMessage.PROPOSE) {

                                carsProposedPP.put(proposeMsg.getSender(), Integer.parseInt(proposeMsg.getContent()));
                                i++;
                                System.out.println(nickname + " received the PROPOSE (" + proposeMsg.getContent() + ") from " + proposeMsg.getSender().getName().substring(0, proposeMsg.getSender().getName().indexOf("@")));
                            }
                            else if(proposeMsg.getPerformative() == ACLMessage.REFUSE){

                                carsStillInAuction.remove(proposeMsg.getSender());
                                System.out.println(nickname + " received the REFUSE from " + proposeMsg.getSender().getName().substring(0, proposeMsg.getSender().getName().indexOf("@")));
                            }
                        }
                        else{

                            block();
                        }
                    }

                    step = 3;
                    break;


                case 3:              // Send to TL the PROPOSE/REFUSE

                    int totalProposedPP = 0;
                    for (Integer value : carsProposedPP.values()) {

                        totalProposedPP += value;
                    }

                    if(totalProposedPP > currentMaxPriorityPoints){

                        ACLMessage replyToTL = new ACLMessage(ACLMessage.PROPOSE);
                        replyToTL.setContent(String.valueOf(totalProposedPP));
                        replyToTL.setConversationId("car_tl_auction");
                        replyToTL.setReplyWith("proposal" + System.currentTimeMillis());
                        replyToTL.addReceiver(tlAid);
                        myAgent.send(replyToTL);
                        System.out.println(nickname + " sent the PROPOSE (" + totalProposedPP + ") to the TL");
                        step = 0;
                    }
                    else{

                        if(carsStillInAuction.size() == 0){

                            ACLMessage replyToTL = new ACLMessage(ACLMessage.REFUSE);
                            replyToTL.setConversationId("car_tl_auction");
                            replyToTL.setReplyWith("proposal" + System.currentTimeMillis());
                            replyToTL.addReceiver(tlAid);
                            myAgent.send(replyToTL);
                            System.out.println(nickname + " sent the REFUSE to the TL");

                            step = 0;
                        }
                        else{

                            step = 1;
                        }
                    }
                    break;


                default:
                    break;
            }
        }

        @Override
        public boolean done() {

            return (step == 4);
        }
    }
}
