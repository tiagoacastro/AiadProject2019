package agents;

import app.GraphNode;

public class Car extends Vehicle{
    /**
     * Car constructor
     * @param startingNode      start
     * @param targetNode        end
     * @param priorityPoints    priority points
     */
    public Car(GraphNode startingNode, GraphNode targetNode, int priorityPoints){
        super(startingNode, targetNode, priorityPoints);
    }

    /**
     *   Method where the agent decides how many Priority Points he is gonna use to pass in a Traffic Light
     *   @return number of points
     */
    @Override
    int choosePriorityPoints() {
        return priorityPoints;
    }
}
