/*
    Code from https://javatutorial.net/graphs-java-example
 */

package app;

public class GraphEdge implements Comparable<GraphEdge>{

    public enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    private GraphNode source;
    private GraphNode destination;
    private int weight;
    private Direction direction;

    public GraphEdge(GraphNode s, GraphNode d, int w, Direction direction) {
        this.source = s;
        this.destination = d;
        this.weight = w;
        this.direction = direction;
    }

    public String toString(){
        return String.format("(%s -> %s, %d, %s", this.source.name, this.destination.name, this.weight, this.direction);
    }

    public GraphNode getStart() {
        return this.source;
    }

    public int getIdOfStartNode() {
        return this.source.getNodeId();
    }

    public GraphNode getEnd() {
        return this.destination;
    }

    public int getIdOfEndNode() {
        return this.destination.getNodeId();
    }

    public Direction getDirection(){ return this.direction; }

    public void setWeight(int weight) {this.weight = weight;}
    public double getWeight() {
        return this.weight;
    }

    @Override
    public int compareTo(GraphEdge otherEdge) {

        if (this.weight > otherEdge.weight)
            return 1;
        else return -1;
    }

    public boolean isDestination(GraphNode node){
        return source == node;
    }
}