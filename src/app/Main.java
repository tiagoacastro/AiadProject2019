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
    public static final int tick = 1500;

    /*
        Main
     */
    public static void main(String [] args){
        Graph.construct();

        startJADE();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Map(), tick, tick, TimeUnit.MILLISECONDS);

        createAgents();
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

            //A to I
            Car carAgent1 = new Car(Graph.nodes.get(0), Graph.nodes.get(8));
            AgentController ac1 = mainContainer.acceptNewAgent("car" + 1, carAgent1);
            ac1.start();

            //B to G
            Car carAgent2 = new Car(Graph.nodes.get(1), Graph.nodes.get(6));
            AgentController ac2 = mainContainer.acceptNewAgent("car" + 2, carAgent2);
            ac2.start();

            //D to H
            Car carAgent3 = new Car(Graph.nodes.get(3), Graph.nodes.get(8));
            AgentController ac3 = mainContainer.acceptNewAgent("car" + 3, carAgent3);
            ac3.start();

            //E to L
            Car carAgent4 = new Car(Graph.nodes.get(4), Graph.nodes.get(11));
            AgentController ac4 = mainContainer.acceptNewAgent("car" + 4, carAgent4);
            ac4.start();

            //F to K
            Car carAgent5 = new Car(Graph.nodes.get(5), Graph.nodes.get(10));
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

            //E to J
            Car carAgent6 = new Car(Graph.nodes.get(4), Graph.nodes.get(9));
            AgentController ac6 = mainContainer.acceptNewAgent("car" + 6, carAgent6);
            ac6.start();

        }
        catch(StaleProxyException spException){

            spException.printStackTrace();
        }
    }
}