package agents;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class Car extends Vehicle{


    /*
       Method that is a placeholder for agent specific startup code.
     */
    @Override
    protected void setup(){

        int pos = getAID().getName().indexOf("@");
        agentNickname = getAID().getName().substring(0, pos);
        System.out.println("Car-agent " + agentNickname + " has started!");

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("communication_car_tl");
        template.addServices(sd);

        addBehaviour(new SimpleBehaviour(this) {

            private boolean done = false;

            @Override
            public void action() {

                try{

                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    System.out.print("Traffic Lights: ");
                    trafficLightsAgents = new AID[result.length];
                    for(int i = 0; i < result.length; i++) {

                        trafficLightsAgents[i] = result[i].getName();
                        System.out.print(trafficLightsAgents[i].getName() + " , ");
                    }
                    System.out.print("\n");
                }
                catch(FIPAException fe){

                    fe.printStackTrace();
                }

                done = true;
            }

            @Override
            public boolean done() {

                return done;
            }
        });
    }

    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    @Override
    protected void takeDown(){

        System.out.println("Car-agent " + agentNickname + " has terminated!");
    }

    /*
        Method that defines the starting and target point for the car.
     */
    public void getPoints(final String vehicleStartingPoint, final String vehicleTargetPoint){

        startingPoint = vehicleStartingPoint;
        targetPoint = vehicleTargetPoint;
    }
}
