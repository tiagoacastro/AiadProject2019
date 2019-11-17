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

        //up nodes (left to right)
        GraphNode a = new GraphNode(0, "0" , 5, 0);
        Graph.addNode(a);
        GraphNode b = new GraphNode(1, "1" , 11, 0);
        Graph.addNode(b);
        GraphNode c = new GraphNode(2, "2", 17, 0);
        Graph.addNode(c);

        //left nodes (up to down)
        GraphNode d = new GraphNode(3, "3" , 0, 5);
        Graph.addNode(d);
        GraphNode e = new GraphNode(4, "4" , 0, 11);
        Graph.addNode(e);
        GraphNode f = new GraphNode(5, "5", 0, 17);
        Graph.addNode(f);

        //down nodes (left to right)
        GraphNode g = new GraphNode(6, "6" , 5, 19);
        Graph.addNode(g);
        GraphNode h = new GraphNode(7, "7" , 11, 19);
        Graph.addNode(h);
        GraphNode i = new GraphNode(8, "8", 17, 19);
        Graph.addNode(i);

        //right nodes (up to down)
        GraphNode j = new GraphNode(9, "9" , 19, 5);
        Graph.addNode(j);
        GraphNode k = new GraphNode(10, "10" , 19, 11);
        Graph.addNode(k);
        GraphNode l = new GraphNode(11, "11", 19, 17);
        Graph.addNode(l);

        //middle up nodes (left to right)
        GraphNode m = new GraphNode(12, "12" , 5, 5);
        Graph.addNode(m);
        GraphNode n = new GraphNode(13, "13" , 11, 5);
        Graph.addNode(n);
        GraphNode o = new GraphNode(14, "14", 17, 5);
        Graph.addNode(o);

        //middle middle nodes (left to right)
        GraphNode p = new GraphNode(15, "15" , 5, 11);
        Graph.addNode(p);
        GraphNode q = new GraphNode(16, "16" , 11, 11);
        Graph.addNode(q);
        GraphNode r = new GraphNode(17, "17", 17, 11);
        Graph.addNode(r);

        //middle down nodes (left to right)
        GraphNode s = new GraphNode(18, "18" , 5, 17);
        Graph.addNode(s);
        GraphNode t = new GraphNode(19, "19" , 11, 17);
        Graph.addNode(t);
        GraphNode u = new GraphNode(20, "20", 17, 17);
        Graph.addNode(u);

        Graph.addEdge(a, m, 5);
        Graph.addEdge(b, n, 5);
        Graph.addEdge(o, c, 5);
        Graph.addEdge(d, m, 5);
        Graph.addEdge(m, n, 5);
        Graph.addEdge(n, o, 5);
        Graph.addEdge(o, j, 2);
        Graph.addEdge(p, m, 5);
        Graph.addEdge(q, n, 5);
        Graph.addEdge(o, r, 5);
        Graph.addEdge(e, p, 5);
        Graph.addEdge(p, q, 5);
        Graph.addEdge(q, r, 5);
        Graph.addEdge(r, k, 2);
        Graph.addEdge(s, p, 5);
        Graph.addEdge(q, t, 5);
        Graph.addEdge(r, u, 5);
        Graph.addEdge(f, s, 5);
        Graph.addEdge(t, s, 5);
        Graph.addEdge(u, t, 5);
        Graph.addEdge(u, l, 2);
        Graph.addEdge(s, g, 2);
        Graph.addEdge(t, h, 2);
        Graph.addEdge(u, i, 2);
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
            if(n1.getX() < n2.getX()) {
                GraphEdge edge = new GraphEdge(n1, n2, weight, GraphEdge.Direction.EAST);
                n1.edges.add(edge);
                if(directed)
                    n2.edgesLeadingUp.add(edge);
            } else {
                GraphEdge edge = new GraphEdge(n1, n2, weight, GraphEdge.Direction.WEST);
                n1.edges.add(edge);
                if(directed)
                    n2.edgesLeadingUp.add(edge);
            }
        }
        else{
            if(n1.getY() > n2.getY()) {
                GraphEdge edge = new GraphEdge(n1, n2, weight, GraphEdge.Direction.NORTH);
                n1.edges.add(edge);
                if(directed)
                    n2.edgesLeadingUp.add(edge);
            } else {
                GraphEdge edge = new GraphEdge(n1, n2, weight, GraphEdge.Direction.SOUTH);
                n1.edges.add(edge);
                if(directed)
                    n2.edgesLeadingUp.add(edge);
            }
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