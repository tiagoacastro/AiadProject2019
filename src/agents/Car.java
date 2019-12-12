package agents;

import app.Graph;
import app.GraphEdge;
import app.GraphNode;

public class Car extends Vehicle{
    /**
     * Car constructor
     * @param startingNode      start
     * @param targetNode        end
     */
    public Car(int startingNode, int targetNode){
        super(Graph.nodes.get(startingNode), Graph.nodes.get(targetNode));
    }

    /**
     * Vehicle constructor with chosen priority points and max tries
     * @param startingNode      start
     * @param targetNode        end
     * @param priorityPoints    priority points
     * @param maxTries          max tries
     */
    public Car(int startingNode, int targetNode, int priorityPoints, int maxTries){
        super(Graph.nodes.get(startingNode), Graph.nodes.get(targetNode), priorityPoints, maxTries);
    }

    /**
     *   Method where the agent decides how many Priority Points he is gonna use to pass in a Traffic Light
     *   @return number of points
     */
    @Override
    int choosePriorityPoints(){

        if(retry > maxTries)
           retry = maxTries;

        int TlLeft = auctionsLeft();

        if(TlLeft == 1)
           return priorityPoints;
        else
           return (retry*priorityPoints)/(TlLeft*maxTries);
    }

    @Override
    String getType(){
        return "Car";
    }
}
