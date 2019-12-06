package app;

import agents.*;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
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
    public static int tick = 1500;
    /*
        traffic light wait time
     */
    public static int tlWait = 8;
    /*
        represent gui flag
     */
    public static boolean gui = true;
    /*
        debug flag
     */
    public static boolean debug = true;
    /*
        config file buffered reader
     */
    private static BufferedReader br;

    /*
        Main
     */
    public static void main(String [] args){
        Graph.construct();

        config();

        startJADE();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Map(), tick, tick, TimeUnit.MILLISECONDS);

        createAgents();
    }

    /*
        config program
     */
    private static void config(){
        try {
            br = new BufferedReader(new FileReader(new File("config.txt")));

            String st;
            if ((st = br.readLine()) != null){
                gui = Boolean.parseBoolean(st);
            }
            if ((st = br.readLine()) != null){
                debug = Boolean.parseBoolean(st);
            }
            if ((st = br.readLine()) != null){
                tick = Integer.parseInt(st);
            }
            if ((st = br.readLine()) != null){
                tlWait = Integer.parseInt(st);
            }
            br.readLine();
        } catch (Exception e){
            e.printStackTrace();
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
            ArrayList<AgentController>[] agents = new ArrayList[Graph.nodes.size()];

            for (int i = 0; i < Graph.nodes.size(); i++) {
                agents[i] = new ArrayList<>();
            }

            String st;
            int i = 0;
            while ((st = br.readLine()) != null) {
                if(st.replace(" ", "").equals(""))
                    continue;
                String[] agentsInfo = st.split(" ");

                Vehicle agent;

                switch(agentsInfo[0]){
                    case "Rushed":
                        if(agentsInfo.length == 3)
                            agent = new RushedCar(Integer.parseInt(agentsInfo[1]), Integer.parseInt(agentsInfo[2]));
                        else
                            agent = new RushedCar(Integer.parseInt(agentsInfo[1]), Integer.parseInt(agentsInfo[2]),
                                    Integer.parseInt(agentsInfo[3]), Integer.parseInt(agentsInfo[4]));
                        break;
                    case "Ambulance":
                        if(agentsInfo.length == 3)
                            agent = new Ambulance(Integer.parseInt(agentsInfo[1]), Integer.parseInt(agentsInfo[2]));
                        else
                            agent = new Ambulance(Integer.parseInt(agentsInfo[1]), Integer.parseInt(agentsInfo[2]),
                                    Integer.parseInt(agentsInfo[3]), Integer.parseInt(agentsInfo[4]));
                        break;
                    default:
                        if(agentsInfo.length == 3)
                            agent = new Car(Integer.parseInt(agentsInfo[1]), Integer.parseInt(agentsInfo[2]));
                        else
                            agent = new Car(Integer.parseInt(agentsInfo[1]), Integer.parseInt(agentsInfo[2]),
                                    Integer.parseInt(agentsInfo[3]), Integer.parseInt(agentsInfo[4]));
                        break;
                }

                agents[Integer.parseInt(agentsInfo[1])].add(mainContainer.acceptNewAgent("vehicle" + i, agent));
                i++;
            }

            boolean notOver = true;
            i = 0;
            while(notOver){
                notOver = false;

                for(ArrayList<AgentController> agentsOnNode : agents){
                   if(i < agentsOnNode.size()) {
                       agentsOnNode.get(i).start();

                       notOver = true;
                   }
                }

                try
                {
                    Thread.sleep(tick);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }

                i++;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}