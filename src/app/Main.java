package app;

import agents.*;

import graphics.MapGraphic;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.imtp.leap.nio.BufferTransformer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.*;
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
        results file buffered writer
     */
    private static BufferedWriter bw;
    /*
        results buffer
     */
    public static StringBuilder sb = new StringBuilder();
    /*
        results file buffered writer for the regression problem
     */
    private static BufferedWriter bwReg;
    /*
        results buffer for the regression problem
     */
    public static StringBuilder sbReg = new StringBuilder();
    /*
        Number of vehicles running
     */
    public static volatile int vehiclesRunning = 0;

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

        long start = System.currentTimeMillis();
        long end = start + 90000;

        try{
            while(vehiclesRunning > 0 && System.currentTimeMillis() < end)
                Thread.sleep(tick);

            if(System.currentTimeMillis() < end) {
                bw.write(sb.toString());
                bwReg.write(sbReg.toString());
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Execution took: " + (System.currentTimeMillis() - start)/1000 + "s");

        stop(scheduler);
    }

    private static void stop(ScheduledExecutorService scheduler){
        try{
            Main.mainContainer.kill();
        } catch (Exception e){
            e.printStackTrace();
        }

        runtime.shutDown();

        try{
            bw.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        try{
            bwReg.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        scheduler.shutdown();

        System.exit(0);
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

            File file = new File("results.csv");

            boolean writeHeader = false;
            if (!file.exists()) {
                file.createNewFile();
                writeHeader = true;
            }

            bw = new BufferedWriter(new FileWriter(file,true));

            if(writeHeader){
                bw.write("sep=,");
                bw.newLine();
                bw.write("type,\"start node\",\"dest. node\",\"start pps\",wave,\"max tries\",\"turns needed\"" +
                        ",\"1st auc. pps\",\"1st auc. tries\",\"1st auc. left aucs.\",\"2nd auc. pps\",\"2nd auc. tries\",\"2nd auc. left aucs.\"" +
                        ",\"3rd auc. pps\",\"3rd auc. tries\",\"3rd auc. left aucs.\",\"4th auc. pps\",\"4th auc. tries\",\"4th auc. left aucs.\"");
                bw.newLine();
            }

            /*                          Regression Problem File                         */
            File fileReg = new File("resultsReg.csv");

            boolean writeHeaderReg = true;
            if (!fileReg.exists()) {
                fileReg.createNewFile();
                writeHeaderReg = true;
            }

            bwReg = new BufferedWriter(new FileWriter(fileReg, true));

            if(writeHeaderReg){
                bwReg.write("sep=,");
                bwReg.newLine();
                bwReg.write("\"PPs to beat\",\"PPs given\",\"veh1 type\",\"veh1 PPs\",\"veh1 max tries\",\"veh1 tls left\"" +
                        ",\"veh2 type\",\"veh2 PPs\",\"veh2 max tries\",\"veh2 tls left\"" +
                        ",\"veh3 type\",\"veh3 PPs\",\"veh3 max tries\",\"veh3 tls left\"" +
                        ",\"veh4 type\",\"veh4 PPs\",\"veh4 max tries\",\"veh4 tls left\"" +
                        ",\"veh5 type\",\"veh5 PPs\",\"veh5 max tries\",\"veh5 tls left\"");
                bwReg.newLine();
            }
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
            ArrayList<AgentController>[] agentControllers = new ArrayList[Graph.nodes.size()];
            ArrayList<Vehicle>[] agents = new ArrayList[Graph.nodes.size()];

            for (int i = 0; i < Graph.nodes.size(); i++) {
                agentControllers[i] = new ArrayList<>();
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

                vehiclesRunning++;

                agents[Integer.parseInt(agentsInfo[1])].add(agent);
                agentControllers[Integer.parseInt(agentsInfo[1])].add(mainContainer.acceptNewAgent("vehicle" + i, agent));
                i++;
            }

            br.close();

            boolean notOver = true;
            i = 0;
            while(notOver){
                notOver = false;

                for(int k = 0; k < agentControllers.length; k++){
                   if(i < agentControllers[k].size()) {
                       agentControllers[k].get(i).start();
                       agents[k].get(i).setWave(i+1);

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