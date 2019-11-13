package agents;

import app.GraphEdge;
import app.GraphNode;
import app.Pos;
import jade.core.Agent;
import jade.core.AID;

import java.util.ArrayList;
import java.util.HashMap;


public abstract class Vehicle extends Agent {
    /*
        Vehicle position
     */
    Pos pos;
    /*
        Vehicle starting node.
     */
    GraphNode startingNode;
    /*
        Vehicle target node.
     */
    GraphNode targetNode;
    /*
        Vehicle path.
     */
    GraphEdge[] path;
    /*
        Current edge
     */
    int currentEdge = 0;
    /*
        Vehicle AID
     */
    AID aid = this.getAID();
    /*
        If car is waiting and can't move
     */
    boolean waiting = false;

    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected abstract void setup();
    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    protected abstract void takeDown();
}
