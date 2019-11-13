package app;

import agents.TrafficLight;
import agents.Car;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.concurrent.*;


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
        Main
     */
    public static void main(String [] args){
        Graph.construct();

        //parseNodesFile();
        //parseEdgesFile();

        startJADE();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Map(), 0, 2, TimeUnit.SECONDS);

        createAgents();
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
     *//*
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
            Thread.sleep(300);
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
            Car carAgent = new Car(Graph.nodes.get(1), Graph.nodes.get(4), 100);
            AgentController ac = mainContainer.acceptNewAgent("car" + 1, carAgent);
            ac.start();
        }
        catch(StaleProxyException spException){

            spException.printStackTrace();
        }
    }
}