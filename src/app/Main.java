package app;

import agents.TrafficLight;
import agents.Car;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {
    /*
        Runtime instance of JADE
     */
    private static Runtime runtime;
    /*
        Main container where the agents are
     */
    private static ContainerController mainContainer;
    /*
        global tick time
     */
    public static final int tick = 2000;

    /*
        Main
     */
    public static void main(String [] args){
        Graph.construct();

        //parseNodesFile();
        //parseEdgesFile();

        startJADE();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Map(), tick, tick, TimeUnit.MILLISECONDS);

        createAgents();

        try {
            AgentController ac = mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma());
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }


    /*
        Nodes File Parser
     */
    /*private static void parseNodesFile(){
        File nodeFile = new File("resources/my_nodes.nod.xml");
        Document doc = null;

        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.parse(nodeFile);
        }
        catch(IOException | SAXException | ParserConfigurationException ioException){

            ioException.printStackTrace();
        }

        NodeList nList = doc.getElementsByTagName("node");

        for(int i = 0; i < nList.getLength(); i++){

            Element eElement = (Element) nList.item(i);

            int id = Integer.parseInt(eElement.getAttribute("id"));
            int x = Integer.parseInt(eElement.getAttribute("x"));
            int y = Integer.parseInt(eElement.getAttribute("y"));

            graph.createNode(new GraphNode(id, eElement.getAttribute("id"), x, y));
        }
    }


    *//*
        Edges File Parser
     */
    /*
    private static void parseEdgesFile(){
        File nodeFile = new File("resources/my_edges.edg.xml");
        Document doc = null;

        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.parse(nodeFile);
        }
        catch(IOException | SAXException | ParserConfigurationException ioException){

            ioException.printStackTrace();
        }

        NodeList eList = doc.getElementsByTagName("edge");


        for(int i = 0; i < eList.getLength(); i++) {

            Element eElement = (Element) eList.item(i);

            String id = eElement.getAttribute("id");
            int fromID = Integer.parseInt(eElement.getAttribute("from"));
            int toID = Integer.parseInt(eElement.getAttribute("to"));
            double weight = calculateWeight(fromID, toID);

            GraphEdge tempEdge = new GraphEdge(graph.getNodes().get(fromID), graph.getNodes().get(toID), weight, id);
            graph.getNodes().get(fromID).addNeighbour(tempEdge);
        }
    }


    *//*
    Method that initializes JADE.
    */
    private static void startJADE(){
        runtime = Runtime.instance();
        mainContainer = runtime.createMainContainer(new ProfileImpl());
    }


    /*
        Method that creates all the JADE agents.
     */
    private static void createAgents(){
        createTrafficLightsAgents();

        try
        {
            Thread.sleep(tick/4);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        createVehiclesAgents();
    }


    /*
        Method that iterates through every node of the graph and creates a TrafficLight agent in each.
    */
    private static void createTrafficLightsAgents() {
        try {
            for (GraphNode node : Graph.nodes) {
                if(node.numberOfEdgesIn() >= 2){
                    TrafficLight tlAgent = new TrafficLight();
                    node.addTl(tlAgent);
                    AgentController ac = mainContainer.acceptNewAgent("tl" + node.getNodeId(), tlAgent);
                    ac.start();
                }
            }
        }
        catch(StaleProxyException spException){

            spException.printStackTrace();
        }
    }


    /*
        Method that iterates through every node of the graph and creates a Vehicles agent in each.
    */
    private static void createVehiclesAgents(){
        try {
            // car de 1->4
            Car carAgent1 = new Car(Graph.nodes.get(1), Graph.nodes.get(4), 100);
            AgentController ac1 = mainContainer.acceptNewAgent("car" + 1, carAgent1);
            ac1.start();

            // cars de 0->4
            Car carAgent4 = new Car(Graph.nodes.get(0), Graph.nodes.get(4), 100);
            AgentController ac4 = mainContainer.acceptNewAgent("car" + 4, carAgent4);
            ac4.start();

            try
            {
                Thread.sleep(tick);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }

            // car de 1->4
            Car carAgent2 = new Car(Graph.nodes.get(1), Graph.nodes.get(4), 100);
            AgentController ac2 = mainContainer.acceptNewAgent("car" + 2, carAgent2);
            ac2.start();

            // cars de 0->4
            Car carAgent5 = new Car(Graph.nodes.get(0), Graph.nodes.get(4), 100);
            AgentController ac5 = mainContainer.acceptNewAgent("car" + 5, carAgent5);
            ac5.start();

            try
            {
                Thread.sleep(tick);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }

            // car de 1->4
            Car carAgent3 = new Car(Graph.nodes.get(1), Graph.nodes.get(4), 100);
            AgentController ac3 = mainContainer.acceptNewAgent("car" + 3, carAgent3);
            ac3.start();
        }
        catch(StaleProxyException spException){

            spException.printStackTrace();
        }
    }
}