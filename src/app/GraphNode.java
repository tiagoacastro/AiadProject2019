/*
    Code from https://javatutorial.net/graphs-java-example
 */

package app;

import java.util.LinkedList;
import agents.TrafficLight;

public class GraphNode {
    private int id;
    String name;
    LinkedList<GraphEdge> edges;
    private int x;
    private int y;
    private boolean visited;
    private TrafficLight tl;

    public GraphNode(int id, String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.edges = new LinkedList<>();
        this.x = x;
        this.y = y;
        this.visited = false;
    }

    public boolean isVisited(){
        return this.visited;
    }

    public void visit(){
        this.visited = true;
    }

    public void unvisited(){
        this.visited = false;
    }

    public String getName(){
        return this.name;
    }

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
        if(this.edges.contains(e)) {
            System.out.println("This edge has already been used for this node.");
        } else {
            this.edges.add(e);
        }
    }

    public int numberOfEdgesIn(){
        int n = 0;
        for(GraphEdge edge : edges){
            if(edge.isDestination(this))
                n++;
        }
        return n;
    }

    public void addTl(TrafficLight tl){
        this.tl = tl;
        Map.originalMap[y][x] = 'O';
        Map.map[y][x] = 'O';
    }
}