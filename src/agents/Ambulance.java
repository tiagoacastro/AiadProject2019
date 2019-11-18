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

        priorityPoints = Integer.MAX_VALUE;
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
    }

    /**
     *   Method where the agent decides how many Priority Points he is gonna use to pass in a Traffic Light
     *   @return number of points
     */
    @Override
    int choosePriorityPoints(){
        return 300;
    }
}