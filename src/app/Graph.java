/*
    Code from
 */

package app;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private List<GraphNode> nodes = new ArrayList<GraphNode>();
    private int numberOfNodes = 0;

    public boolean checkForAvailability() { // will be used in Main.java
        return this.numberOfNodes > 1;
    }

    public void createNode(GraphNode node) {
        this.nodes.add(node);
        this.numberOfNodes++; // a node has been added
    }

    public List<GraphNode> getNodes(){

        return nodes;
    }

    public int getNumberOfNodes() {
        return this.numberOfNodes;
    }
}