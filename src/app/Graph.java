/*
    Code from
 */

package app;

import java.util.*;

public class Graph {
    public static ArrayList<GraphNode> nodes = new ArrayList<>();
    private static boolean directed;

    public static void construct(){
        Graph.directed = true;

        GraphNode zero = new GraphNode(0, "0" , 0, 5);
        Graph.addNode(zero);
        GraphNode one = new GraphNode(1, "1" , 5, 0);
        Graph.addNode(one);
        GraphNode two = new GraphNode(2, "2", 5, 5);
        Graph.addNode(two);
        GraphNode three = new GraphNode(3, "3", 5, 10);
        Graph.addNode(three);
        GraphNode four = new GraphNode(4, "4", 10, 5);
        Graph.addNode(four);

        Graph.addEdge(zero, two, 5);
        Graph.addEdge(one, two, 5);
        Graph.addEdge(two, three, 5);
        Graph.addEdge(two, four, 5);
    }

    public static void addNode(GraphNode... node){
        nodes.addAll(Arrays.asList(node));
    }

    public static void addEdge(GraphNode source, GraphNode destination, int weight){
        addEdgeHelper(source, destination, weight);

        if(!directed && source != destination){
            addEdgeHelper(destination, source, weight);
        }

    }

    public static void addEdgeHelper(GraphNode n1, GraphNode n2, int weight){
        for (GraphEdge edge: n1.edges){
            if(edge.getStart() == n1 && edge.getEnd() == n2){
                edge.setWeight(weight);
                return;
            }
        }

        if(n1.getY() == n2.getY()){

            if(n1.getX() < n2.getX())
                n1.edges.add(new GraphEdge(n1, n2, weight, GraphEdge.Direction.EAST));
            else
                n1.edges.add(new GraphEdge(n1, n2, weight, GraphEdge.Direction.WEST));
        }
        else{

            if(n1.getY() > n2.getY())
                n1.edges.add(new GraphEdge(n1, n2, weight, GraphEdge.Direction.NORTH));
            else
                n1.edges.add(new GraphEdge(n1, n2, weight, GraphEdge.Direction.SOUTH));
        }
    }

    public static void printEdges(){
        for(GraphNode node  : nodes){
            LinkedList<GraphEdge> edges = node.edges;

            if(edges.isEmpty()){
                System.out.println("Node " + node.getName() + " has no edges.");
                continue;
            }

            System.out.print("Node " + node.getName() + " has edges to: ");

            for(GraphEdge edge : edges){
                System.out.print(edge.getEnd().getName() + "(" + edge.getWeight() + ")");
            }

            System.out.println();
        }
    }

    public static boolean hasEdge(GraphNode source, GraphNode destination){
        LinkedList<GraphEdge> edges = source.edges;
        for(GraphEdge edge : edges){
            if(edge.getEnd() == destination)
                return true;
        }

        return false;
    }

    public static void resetNodesVisited(){
        for(GraphNode node : nodes){
            node.unvisited();
        }
    }

    public static String DijkstraShortestPath(GraphNode start, GraphNode end) {
        HashMap<GraphNode, GraphNode> changedAt = new HashMap<>();
        changedAt.put(start, null);

        // Keeps track of the shortest path we've found so far for every node
        HashMap<GraphNode, Double> shortestPathMap = new HashMap<>();

        for (GraphNode node : nodes) {
            if (node == start)
                shortestPathMap.put(start, 0.0);
            else shortestPathMap.put(node, Double.POSITIVE_INFINITY);
        }

        for (GraphEdge edge : start.edges) {
            shortestPathMap.put(edge.getEnd(), edge.getWeight());
            changedAt.put(edge.getEnd(), start);
        }

        start.visit();

        while (true) {
            GraphNode currentNode = closestReachableUnvisited(shortestPathMap);
            if (currentNode == null) {
                System.out.println("There isn't a path between " + start.name + " and " + end.name);
                return null;
            }

            // If the closest non-visited node is our destination, we want to print the path
            if (currentNode == end) {

                GraphNode child = end;

                String path = end.name;
                while (true) {
                    GraphNode parent = changedAt.get(child);
                    if (parent == null) {
                        break;
                    }

                    path = parent.name + " " + path;
                    child = parent;
                }
                return path;
            }
            currentNode.visit();

            for (GraphEdge edge : currentNode.edges) {
                if (edge.getEnd().isVisited())
                    continue;

                if (shortestPathMap.get(currentNode)
                        + edge.getWeight()
                        < shortestPathMap.get(edge.getEnd())) {
                    shortestPathMap.put(edge.getEnd(),
                            shortestPathMap.get(currentNode) + edge.getWeight());
                    changedAt.put(edge.getEnd(), currentNode);
                }
            }
        }
    }

    private static GraphNode closestReachableUnvisited(HashMap<GraphNode, Double> shortestPathMap) {
        double shortestDistance = Double.POSITIVE_INFINITY;
        GraphNode closestReachableNode = null;

        for (GraphNode node : nodes) {
            if (node.isVisited())
                continue;

            double currentDistance = shortestPathMap.get(node);
            if (currentDistance == Double.POSITIVE_INFINITY)
                continue;

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                closestReachableNode = node;
            }
        }
        return closestReachableNode;
    }

    /*
        Method that calculates the distance between 2 nodes
    */
    private static double calculateWeight(int f, int t){
        int fx = Graph.nodes.get(f).getX();
        int fy = Graph.nodes.get(f).getY();
        int tx = Graph.nodes.get(t).getX();
        int ty = Graph.nodes.get(t).getY();
        return Math.sqrt(Math.pow(tx-fx, 2) + Math.pow(ty-fy, 2));
    }
}