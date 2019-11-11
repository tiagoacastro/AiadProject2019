package agents;

import jade.core.Agent;
import jade.core.AID;


public abstract class Vehicle extends Agent {
    /*
        Vehicle agent nickname.
     */
    public String agentNickname;
    /*
        Vehicle known traffic lights.
     */
    public AID[] trafficLightsAgents;
    /*
        Vehicle starting node.
     */
    public int startingNode;
    /*
        Vehicle target node.
     */
    public int targetNode;

    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected abstract void setup();

    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    protected abstract void takeDown();
}
