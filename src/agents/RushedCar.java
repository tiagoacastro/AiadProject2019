package agents;

import app.GraphEdge;
import app.GraphNode;

public class RushedCar extends Vehicle{
    /**
     * Car constructor
     * @param startingNode      start
     * @param targetNode        end
     */
    public RushedCar(GraphNode startingNode, GraphNode targetNode){
        super(startingNode, targetNode);
    }

    /**
     * Vehicle constructor with chosen priority points and max tries
     * @param startingNode      start
     * @param targetNode        end
     * @param priorityPoints    priority points
     * @param maxTries          max tries
     */
    public RushedCar(GraphNode startingNode, GraphNode targetNode, int priorityPoints, int maxTries){
        super(startingNode, targetNode, priorityPoints, maxTries);
    }

    /**
     *   Method where the agent decides how many Priority Points he is gonna use to pass in a Traffic Light
     *   @return number of points
     */
    @Override
    int choosePriorityPoints(){
        int TlLeft = 0;

        if(retry > maxTries)
            retry = maxTries;

        for(int i = this.currentEdge; i < this.path.length; i++){
            GraphEdge edge = path[i];
            if(edge.getEnd().getTl() != null)
                TlLeft++;
        }

        if(TlLeft == 1)
            return priorityPoints;
        else
            return (retry*priorityPoints)/maxTries;
    }
}
