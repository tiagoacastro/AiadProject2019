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
     *   Vehicle Start Priority Points
     */
    int startPriorityPoints;
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
     *   wave in which the vehicle started
     */
    private int wave;

    private int auctions = 0;

    private ArrayList<Integer> ppAuction = new ArrayList<>();

    private ArrayList<Integer> leftAuctions = new ArrayList<>();

    private ArrayList<Integer> triesAuction = new ArrayList<>();

    private int turnsTaken = 0;

    String vehicleType;

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
        this.startPriorityPoints = this.priorityPoints;
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
        this.startPriorityPoints = this.priorityPoints;
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

    abstract String getType();

    /**
     *   Method that is a placeholder for agent specific cleanup code.
     */
    @Override
    protected void takeDown(){
        try{
            String pp1, pp2, pp3, pp4, t1, t2, t3, t4, la1, la2, la3, la4;
            pp1 = pp2 = pp3 = pp4 = t1 = t2 = t3 = t4 = la1 = la2 = la3 = la4 = "0";
            for(int i = 0; i < 4 && i < ppAuction.size(); i++){
                switch(i){
                    case 0:
                        pp1 = Integer.toString(ppAuction.get(i));
                        t1 = Integer.toString(triesAuction.get(i));
                        la1 = Integer.toString(leftAuctions.get(i));
                        break;
                    case 1:
                        pp2 = Integer.toString(ppAuction.get(i));
                        t2 = Integer.toString(triesAuction.get(i));
                        la2 = Integer.toString(leftAuctions.get(i));
                        break;
                    case 2:
                        pp3 = Integer.toString(ppAuction.get(i));
                        t3 = Integer.toString(triesAuction.get(i));
                        la3 = Integer.toString(leftAuctions.get(i));
                        break;
                    case 3:
                        pp4 = Integer.toString(ppAuction.get(i));
                        t4 = Integer.toString(triesAuction.get(i));
                        la4 = Integer.toString(leftAuctions.get(i));
                        break;
                }
            }

            Main.sb.append(getType()+','+startingNode.getName()+','+targetNode.getName()+','+this.startPriorityPoints
                        +','+this.wave+','+this.maxTries+','+this.turnsTaken+','+pp1+','+t1+','+la1+','+pp2+','+t2+','+la2+','+pp3+','+t3+','+la3+','+pp4+','+t4+','+la4+'\n');
        } catch(Exception e){
            e.printStackTrace();
        }

        Main.vehiclesRunning--;

        if(Main.debug)
            System.out.println(nickname + " has terminated!");
    }

    /**
     *   Method where the agent decides how many Priority Points he is gonna use to pass in a Traffic Light
     *   @return number of points
     */
    abstract int choosePriorityPoints();

    /**
     * wave setter
     * @param wave wave number
     */
    public void setWave(int wave) {
        this.wave = wave;
    }

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

    int auctionsLeft(){
        int TlLeft = 0;

        for(int i = this.currentEdge; i < this.path.length; i++){
            GraphEdge edge = path[i];
            if(edge.getEnd().getTl() != null)
                TlLeft++;
        }

        return TlLeft;
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
            else
                turnsTaken++;

            try {
                if(waiting)
                    Map.newMap[pos.y][pos.x] = 'X';
                else {
                    Pos aux = new Pos(pos.x,pos.y);
                    chooseNext(aux);

                    if(Map.originalMap[aux.y][aux.x] == 'O' && pass){
                        pass = false;
                        addBehaviour(new Move());
                    } else {
                        while (Map.originalMap[aux.y][aux.x] != 'O' && Map.oldMap[aux.y][aux.x] == 'X')
                            chooseNext(aux);

                        switch (Map.originalMap[aux.y][aux.x]) {
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
    private class Inform extends Behaviour {
        private int step = 0;

        @Override
        public void action(){
            switch(step){
                case 0:
                    ACLMessage informMsg = new ACLMessage(ACLMessage.INFORM);
                    informMsg.addReceiver(path[currentEdge].getTlAid());
                    informMsg.setConversationId("inform");
                    informMsg.setReplyWith("inform" + System.currentTimeMillis()); // To ensure unique values
                    String contentStr = String.valueOf(path[currentEdge].getDirection());
                    contentStr += "/" + vehicleType;
                    contentStr += "/" + priorityPoints;
                    contentStr += "/" + maxTries;
                    int TlLeft = 0;
                    for(int i = currentEdge; i < path.length; i++){
                        GraphEdge edge = path[i];
                        if(edge.getEnd().getTl() != null)
                            TlLeft++;
                    }
                    contentStr += "/" + TlLeft;
                    informMsg.setContent(contentStr);
                    myAgent.send(informMsg);

                    step = 1;
                    break;
                case 1:
                    MessageTemplate msgTempAgree = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
                    MessageTemplate msgTempRefuse = MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM);
                    MessageTemplate bothTemp = MessageTemplate.or(msgTempAgree, msgTempRefuse);

                    ACLMessage message = myAgent.receive(bothTemp);

                    if(message != null) {
                        if (message.getPerformative() == ACLMessage.CONFIRM) {
                            addBehaviour(new Auction());
                            auctions++;
                            step = 2;

                            if(Main.debug)
                                System.out.println("ACCEPTED INFORM FROM " + nickname);
                        } else {
                            step = 0;

                            if(Main.debug)
                                System.out.println("REFUSED INFORM FROM " + nickname);
                        }
                    }
                    break;
            }
        }

        @Override
        public boolean done() {
            return step == 2;
        }
    }

    /**
     * Auction behaviour for the first and back cars
     */
    private class Auction extends Behaviour {

        int step = 0;
        ArrayList<AID> carsBehind = new ArrayList<>();
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

                                this.carsBehind = new ArrayList<AID>();
                                carsStillInAuction = new ArrayList<>();
                                carsStillInAuction.add(getAID());
                                carsProposedPP = new HashMap<>();
                                carsProposedPP.put(getAID(), 0);

                                tlAid = message.getSender();

                                currentMaxPriorityPoints = Integer.parseInt(content.substring(0, content.indexOf("|")));

                                if(Main.debug)
                                    System.out.println("First car (" + nickname + ") received the TL's CFP (" + currentMaxPriorityPoints + ")");

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

                                    this.carsBehind.add(tempAid);
                                    carsStillInAuction.add(tempAid);
                                    carsProposedPP.put(tempAid, 0);
                                    aids = aids.substring(posX + 1);
                                    posX = aids.indexOf('|');
                                }

                                step = 1;
                            }
                            else {                               // Cars behind the first of the queue
                                if(Main.debug)
                                    System.out.println(nickname + " behind the first car received its CFP");

                                ACLMessage replyToFirstCarMsg = message.createReply();

                                if (retry <= maxTries) {
                                    int tempPP = choosePriorityPoints();
                                    lastPriorityPoints = tempPP;

                                    if(ppAuction.size() < auctions)
                                        ppAuction.add(lastPriorityPoints);
                                    else
                                        ppAuction.set(auctions-1,lastPriorityPoints);

                                    if(triesAuction.size() < auctions)
                                        triesAuction.add(retry);
                                    else
                                        triesAuction.set(auctions-1,retry);

                                    if(leftAuctions.size() < auctions)
                                        leftAuctions.add(auctionsLeft());

                                    retry++;
                                    replyToFirstCarMsg.setPerformative(ACLMessage.PROPOSE);
                                    replyToFirstCarMsg.setContent(String.valueOf(lastPriorityPoints));

                                    if(Main.debug)
                                        System.out.println(nickname + " behind the first car sent his PROPOSE (" + tempPP + ")");
                                } else {
                                    replyToFirstCarMsg.setPerformative(ACLMessage.REFUSE);
                                    replyToFirstCarMsg.setContent(String.valueOf(lastPriorityPoints));

                                    if(Main.debug)
                                        System.out.println(nickname + " behind the first car sent his REFUSE (" + lastPriorityPoints + ")");
                                }

                                myAgent.send(replyToFirstCarMsg);

                                step = 0;
                            }
                        }
                        else if(message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL ||
                                message.getPerformative() == ACLMessage.REJECT_PROPOSAL){
                            if(Main.debug)
                                System.out.println(nickname + " received ACCEPT_PROPOSAL/REJECT_PROPOSAL from the TL");

                            ACLMessage informMsg = new ACLMessage(ACLMessage.INFORM);

                            for(int i = 0; i < this.carsBehind.size(); i++){
                                informMsg.addReceiver(this.carsBehind.get(i));
                            }

                            if(message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
                                informMsg.setContent("GO");

                                if(Main.debug)
                                    System.out.println(nickname + " sent INFORM to GO to the ones behind him");

                                priorityPoints -= lastPriorityPoints;
                                waiting = false;
                                pass = true;
                                step = 4;
                            }
                            else {
                                informMsg.setContent("DONT_GO");

                                if(Main.debug)
                                    System.out.println(nickname + " sent INFORM to DONT_GO to the ones behind him");

                                lastPriorityPoints = 0;
                                step = 0;
                            }
                            retry = 1;
                            informMsg.setConversationId("inform_auction");
                            informMsg.setReplyWith("inform" + System.currentTimeMillis());
                            myAgent.send(informMsg);

                        }
                        else if(message.getPerformative() == ACLMessage.INFORM){
                            if((message.getContent()).equals("GO")){
                                if(Main.debug)
                                    System.out.println(nickname + " received INFORM to GO");

                                waiting = false;
                                pass = true;
                                priorityPoints -= lastPriorityPoints;
                                step = 4;
                            }
                            else if((message.getContent()).equals("DONT_GO")){
                                if(Main.debug)
                                    System.out.println(nickname + " received INFORM to DONT_GO");

                                lastPriorityPoints = 0;
                                step = 0;
                            }
                            retry = 1;
                        }
                    }
                    else{
                        block();
                    }

                    break;


                case 1:         // First car sends CFP to the cars behind it in the queue

                    ACLMessage cfpMsgCar = new ACLMessage(ACLMessage.CFP);
                    for(int i = 1; i < carsStillInAuction.size(); i++){
                        cfpMsgCar.addReceiver(carsStillInAuction.get(i));
                    }
                    cfpMsgCar.setConversationId("car_car_auction");
                    cfpMsgCar.setReplyWith("cfp" + System.currentTimeMillis());
                    myAgent.send(cfpMsgCar);

                    if(Main.debug)
                        System.out.println(nickname + " sent CFP to the ones behind him and still in auction");

                    step = 2;
                    break;


                case 2:         // Receive every PROPOSE/REFUSE and First car chooses the PP

                    MessageTemplate proposeTemp = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                    MessageTemplate refuseTemp = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);
                    MessageTemplate bothTemp3 = MessageTemplate.or(proposeTemp, refuseTemp);

                    // First Car choosing his PP to send
                    if (retry <= maxTries) {
                        int tempPP = choosePriorityPoints();
                        lastPriorityPoints = tempPP;

                        if(ppAuction.size() < auctions)
                            ppAuction.add(lastPriorityPoints);
                        else
                            ppAuction.set(auctions-1,lastPriorityPoints);

                        if(triesAuction.size() < auctions)
                            triesAuction.add(retry);
                        else
                            triesAuction.set(auctions-1,retry);

                        if(leftAuctions.size() < auctions)
                            leftAuctions.add(auctionsLeft());

                        retry++;
                        if(Main.debug)
                            System.out.println(nickname + " (first car) choose his PP to send (" + tempPP + ")");

                        carsProposedPP.put(getAID(), lastPriorityPoints);
                    } else {
                        carsStillInAuction.remove(getAID());

                        if(Main.debug)
                            System.out.println(nickname + " (first car) choose wont send more PP (retry:" + retry + ")");
                    }

                    int i = 1;
                    while(i < carsStillInAuction.size()){
                        ACLMessage proposeMsg = myAgent.receive(bothTemp3);

                        if(proposeMsg != null){

                            carsProposedPP.put(proposeMsg.getSender(), Integer.parseInt(proposeMsg.getContent()));
                            if (proposeMsg.getPerformative() == ACLMessage.PROPOSE) {
                                i++;

                                if(Main.debug)
                                    System.out.println(nickname + " received the PROPOSE (" + proposeMsg.getContent() + ") from " + proposeMsg.getSender().getName().substring(0, proposeMsg.getSender().getName().indexOf("@")));
                            }
                            else if(proposeMsg.getPerformative() == ACLMessage.REFUSE){
                                carsStillInAuction.remove(proposeMsg.getSender());

                                if(Main.debug)
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
                        if(Main.debug)
                            System.out.println(carsProposedPP.toString());

                        totalProposedPP += value;
                    }

                    if(Main.debug)
                        System.out.println("totalProposedPP: " + totalProposedPP);

                    if(totalProposedPP > currentMaxPriorityPoints){

                        ACLMessage replyToTL = new ACLMessage(ACLMessage.PROPOSE);
                        replyToTL.setContent(String.valueOf(totalProposedPP));
                        replyToTL.setConversationId("car_tl_auction");
                        replyToTL.setReplyWith("proposal" + System.currentTimeMillis());
                        replyToTL.addReceiver(tlAid);
                        myAgent.send(replyToTL);

                        if(Main.debug)
                            System.out.println(nickname + " sent the PROPOSE (" + totalProposedPP + ") to the TL");

                        step = 0;
                    }
                    else{

                        if(carsStillInAuction.size() == 0){

                            ACLMessage replyToTL = new ACLMessage(ACLMessage.REFUSE);
                            replyToTL.setConversationId("car_tl_auction");
                            replyToTL.setReplyWith("proposal" + System.currentTimeMillis());
                            replyToTL.addReceiver(tlAid);
                            replyToTL.setContent(String.valueOf(totalProposedPP));
                            myAgent.send(replyToTL);

                            if(Main.debug)
                                System.out.println(nickname + " sent the REFUSE to the TL ");

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
