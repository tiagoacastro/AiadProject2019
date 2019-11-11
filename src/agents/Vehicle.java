package agents;

import app.GraphEdge;
import jade.core.Agent;
import jade.core.AID;

import java.util.ArrayList;


public abstract class Vehicle extends Agent {
    /*
        Vehicle starting node.
     */
    int startingNode;
    /*
        Vehicle target node.
     */
    int targetNode;
    /*
        Vehicle path.
     */
    ArrayList<GraphEdge> path;
    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected abstract void setup();
    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    protected abstract void takeDown();
}
