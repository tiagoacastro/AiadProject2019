/*
    Code from https://javatutorial.net/graphs-java-example
 */

package app;

public class GraphEdge implements Comparable<GraphEdge>{

    private GraphNode source;
    private GraphNode destination;
    private int weight;

    public GraphEdge(GraphNode s, GraphNode d, int w) {
        this.source = s;
        this.destination = d;
        this.weight = w;
    }

    public String toString(){
        return String.format("(%s -> %s, %d", this.source.name, this.destination.name, this.weight);
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