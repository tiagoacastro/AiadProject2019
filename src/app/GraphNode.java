/*
    Code from https://javatutorial.net/graphs-java-example
 */

package app;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {

    private int id;
    private List<GraphEdge> neighbours = new ArrayList<GraphEdge>();
    private int x;
    private int y;

    public int getNodeId() {

        return this.id;
    }

    public int getX(){

        return this.x;
    }

    public int getY(){

        return this.y;
    }

    public void addNeighbour(GraphEdge e) {
        if(this.neighbours.contains(e)) {
            System.out.println("This edge has already been used for this node.");
        } else {
            System.out.println("Successfully added " + e);
            this.neighbours.add(e);
        }
    }

    public void getNeighbours() {
        System.out.println("List of all edges that node " + this.id +" has: ");
        System.out.println("=================================");
        for (int i = 0; i < this.neighbours.size(); i++ ){
            System.out.println("ID of Edge: " + neighbours.get(i).getId() + "\nID of the first node: " + neighbours.get(i).getIdOfStartNode() +
                    "\nID of the second node: " + neighbours.get(i).getIdOfEndNode());
            System.out.println();
        }
        System.out.println(neighbours);
    }

    public GraphNode(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}