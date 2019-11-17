package agents;

import app.GraphNode;

public class Ambulance extends Vehicle{
    /**
     * Car constructor
     * @param startingNode      start
     * @param targetNode        end
     */
    public Ambulance(GraphNode startingNode, GraphNode targetNode){
        super(startingNode, targetNode);
    }

    /**
     * Vehicle constructor with chosen priority points and max tries
     * @param startingNode      start
     * @param targetNode        end
     * @param priorityPoints    priority points
     * @param maxTries          max tries
     */
    public Ambulance(GraphNode startingNode, GraphNode targetNode, int priorityPoints, int maxTries){
        super(startingNode, targetNode, priorityPoints, maxTries);
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