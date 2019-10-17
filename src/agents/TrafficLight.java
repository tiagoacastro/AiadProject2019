package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class TrafficLight extends Agent {

    /*
        TrafficLight agent nickname.
     */
    private String agentNickname;


    /*
        Method that is a placeholder for agent specific startup code.
     */
    protected void setup(){

        int pos = getAID().getName().indexOf("@");
        agentNickname = getAID().getName().substring(0, pos);
        System.out.println("TrafficLight-agent " + agentNickname + " has started!");

        registerYellowPages();
    }


    /*
        Method that is a placeholder for agent specific cleanup code.
     */
    protected void takeDown(){

        System.out.println("TrafficLight-agent " + agentNickname + " has terminated!");
    }


    /*
        Method that takes care of the Traffic Light's registration in the yellow pages.
     */
    private void registerYellowPages(){

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("communication_car_tl");
        sd.setName("JADE-tl");
        dfd.addServices(sd);
        try{

            DFService.register(this, dfd);
        }
        catch(FIPAException fe){

            fe.printStackTrace();
        }
    }
}
