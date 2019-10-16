package agents;

import jade.core.Agent;
import jade.core.AID;


public abstract class Vehicle extends Agent {

    /*
        Vehicle agent nickname.
     */
    public String agentNickname;
    public AID[] trafficLightsAgents;


    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected abstract void setup();


    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    protected abstract void takeDown();
}
