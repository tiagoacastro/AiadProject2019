/*
    Code from https://javatutorial.net/graphs-java-example
 */

package app;

public class GraphEdge {
    private GraphNode start;
    private GraphNode end;
    private double weight;
    private String id;

    public String getId() {
        return this.id;
    }

    public GraphNode getStart() {
        return this.start;
    }

    public int getIdOfStartNode() {
        return this.start.getNodeId();
    }

    public GraphNode getEnd() {
        return this.end;
    }

    public int getIdOfEndNode() {
        return this.end.getNodeId();
    }

    public double getWeight() {
        return this.weight;
    }

    public GraphEdge(GraphNode s, GraphNode e, double w, String id) {
        this.start = s;
        this.end = e;
        this.weight = w;
        this.id = id;
    }
}