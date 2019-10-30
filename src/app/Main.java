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
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class Main {

    private static Runtime runtime;
    private static ContainerController mainContainer;
    private static Graph graph = new Graph();

    private static int numberCars;


    /*
        Main function.
     */
    public static void main(String [] args){

        parseNodesFile();
        parseEdgesFile();

        startJADE();

        createAgents();
    }


    /*
        Nodes File Parser
     */
    private static void parseNodesFile(){


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

            graph.createNode(new GraphNode(id, x, y));
        }
    }


    /*
        Edges File Parser
     */
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

            GraphEdge tempEdge = new GraphEdge(graph.getNodes().get(fromID), graph.getNodes().get(toID), 0, id);
            graph.getNodes().get(fromID).addNeighbour(tempEdge);
        }

        for(int i = 0; i < graph.getNodes().size(); i++){

            graph.getNodes().get(i).getNeighbours();
        }
    }


    /*
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


    /*
        Method that iterates through every node of the graph and creates a Vehicles agent in each.
     */
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
}