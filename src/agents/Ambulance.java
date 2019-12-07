package agents;

import app.Graph;
import app.GraphNode;

public class Ambulance extends Vehicle{
    /**
     * Car constructor
     * @param startingNode      start
     * @param targetNode        end
     */
    public Ambulance(int startingNode, int targetNode){
        super(Graph.nodes.get(startingNode), Graph.nodes.get(targetNode));

        this.priorityPoints = Integer.MAX_VALUE;
        this.maxTries = 1;
    }

    /**
     * Vehicle constructor with chosen priority points and max tries
     * @param startingNode      start
     * @param targetNode        end
     * @param priorityPoints    priority points
     * @param maxTries          max tries
     */
    public Ambulance(int startingNode, int targetNode, int priorityPoints, int maxTries){
        super(Graph.nodes.get(startingNode), Graph.nodes.get(targetNode), priorityPoints, maxTries);

        this.priorityPoints = Integer.MAX_VALUE;
        this.maxTries = 1;
    }

    /**
     *   Method where the agent decides how many Priority Points he is gonna use to pass in a Traffic Light
     *   @return number of points
     */
    @Override
    int choosePriorityPoints(){
        return 300;
    }

    @Override
    String getType(){
        return "Ambulance";
    }
}