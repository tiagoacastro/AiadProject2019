package app;

import agents.TrafficLight;
import agents.Car;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.Math;


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
        Graph with nodes and edges representing the roads
     */
    private static Graph graph;

    /*
        Main
     */
    public static void main(String [] args){

        graph = new Graph(true);

        GraphNode zero = new GraphNode(0, "0" , 0, 5);
        GraphNode one = new GraphNode(1, "1" , 5, 0);
        GraphNode two = new GraphNode(2, "2", 5, 5);
        GraphNode three = new GraphNode(3, "3", 5, 10);
        GraphNode four = new GraphNode(4, "4", 10, 5);

        graph.addEdge(zero, two, 5);
        graph.addEdge(one, two, 5);
        graph.addEdge(two, three, 5);
        graph.addEdge(two, four, 5);

        graph.DijkstraShortestPath(zero, four);

        //parseNodesFile();
        //parseEdgesFile();

        //startJADE();

        //createAgents();
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
     *//*
    private static void startJADE(){

        runtime = Runtime.instance();
        mainContainer = runtime.createMainContainer(new ProfileImpl());
    }


    *//*
        Method that creates all the JADE agents.
     *//*
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


    *//*
        Method that iterates through every node of the graph and creates a TrafficLight agent in each.
     *//*
    private static void createTrafficLightsAgents() {

        try {

            for (int i = 0; i < graph.getNodes().size(); i++) {

                TrafficLight tlAgent = new TrafficLight(graph.getNodes().get(i).getX(), graph.getNodes().get(i).getY());
                AgentController ac = mainContainer.acceptNewAgent("tl" + i, tlAgent);
                ac.start();
            }
        }
        catch(StaleProxyException spException){

            spException.printStackTrace();
        }
    }


    *//*
        Method that iterates through every node of the graph and creates a Vehicles agent in each.
     *//*
    private static void createVehiclesAgents(){

        try {

            Car carAgent = new Car(0, 9, 100);
            AgentController ac = mainContainer.acceptNewAgent("car" + 1, carAgent);
            ac.start();
        }
        catch(StaleProxyException spException){

            spException.printStackTrace();
        }
    }


    *//*
        Method that calculates the distance between 2 nodes
     *//*
    private static double calculateWeight(int f, int t){

        int fx = graph.getNodes().get(f).getX();
        int fy = graph.getNodes().get(f).getY();
        int tx = graph.getNodes().get(t).getX();
        int ty = graph.getNodes().get(t).getY();
        return Math.sqrt(Math.pow(tx-fx, 2) + Math.pow(ty-fy, 2));
    }*/
}