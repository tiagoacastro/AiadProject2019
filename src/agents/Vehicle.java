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
        Vehicle starting point.
     */
    public String startingPoint;

    /*
        Vehicle target point.
     */
    public String targetPoint;


    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected abstract void setup();


    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    protected abstract void takeDown();

    /*
        Method that defines the starting and target point for the vehicle.
     */
    public abstract void getPoints(final String vehicleStartingPoint, final String vehicleTargetPoint);
}
