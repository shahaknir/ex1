package ex1;

import java.io.*;
import java.util.*;

public class WGraph_Algo implements weighted_graph_algorithms {
    private weighted_graph g;

    @Override
    public void init(weighted_graph g) {
        this.g = g;
    }

    @Override
    public weighted_graph getGraph() {
        return g;
    }

    @Override
    public weighted_graph copy() {
        weighted_graph graphCopy = new WGraph_DS();

        for (node_info node : g.getV()) {  // iterate over nodes
            int nodeKey = node.getKey();
            graphCopy.addNode(nodeKey);  // add the node to graph

            node_info nodeCopy = graphCopy.getNode(nodeKey);
            nodeCopy.setInfo(node.getInfo());  // copy his info
            nodeCopy.setTag(node.getTag());  // and his tag

            for (node_info neighbor : g.getV(node.getKey())) {  // iterate over his neighbors
                int neighborKey = neighbor.getKey();
                graphCopy.addNode(neighborKey);  // add the neighbor node if it doesn't exist yet (if exists no action is performed)

                double edgeWeight = graphCopy.getEdge(nodeKey, neighborKey);  // get the original edge weight
                graphCopy.connect(nodeKey, neighborKey, edgeWeight);  // add the edge
            }
        }

        return graphCopy;
    }

    @Override
    public boolean isConnected() {
        var nodes = g.getV();

        if (nodes.isEmpty()) {
            return true;  // empty graph - connected
        }

        node_info firstNode = nodes.iterator().next();

        // check if there is a path from the first node to all others
        for (node_info node : nodes) {
            if (shortestPathDist(firstNode.getKey(), node.getKey()) == -1) {  // no path between 2 nodes - not connected
                return false;
            }
        }

        // one node is connected to all others - graph is connected
        return true;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        List<node_info> shortestPath = shortestPath(src, dest);

        if (shortestPath == null) {
            return -1;  // no path
        }

        double pathWeight = 0;
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            node_info node1 = shortestPath.get(i);
            node_info node2 = shortestPath.get(i + 1);
            double weight = g.getEdge(node1.getKey(), node2.getKey());
            pathWeight += weight;
        }

        return pathWeight;
    }

    @Override
    public List<node_info> shortestPath(int src, int dest) {
        if (g.getNode(src) == null || g.getNode(dest) == null) {
            return null;  // one of the nodes doesn't exist in the graph
        }

        List<node_info> path = new ArrayList<node_info>();

        if (src == dest) {
            path.add(g.getNode(src));
            return path;
        }

        HashSet<Integer> seen = new HashSet<>();  // which nodes we've already seen
        HashMap<Integer, Integer> prev = new HashMap<>();  // save a back pointer for each node (to restore path)
        HashMap<Integer, Double> distances = new HashMap<>();  // distance of each node from the source
        PriorityQueue<node_info> closestToSource = new PriorityQueue<>(new Comparator<node_info>() {
            @Override
            public int compare(node_info o1, node_info o2) {
                return distances.get(o1.getKey()).compareTo(distances.get(o2.getKey()));
            }
        });  // to pop each time the closest node to source


        // add src to the lists
        closestToSource.add(g.getNode(src));

        for (node_info node : g.getV()) {
            distances.put(node.getKey(), Double.POSITIVE_INFINITY);  // set distance of all nodes to infinity
        }
        distances.put(src, 0.0);  // distance of src is 0

        while (!closestToSource.isEmpty()) {
            int currentNodeKey = closestToSource.poll().getKey();  // pick the closest node to src that we didn't process yet
            seen.add(src);

            for (node_info neighbor : g.getV(currentNodeKey)) {  // iterate over neighbors
                int neighborKey = neighbor.getKey();

                double alternativePathWeight = distances.get(currentNodeKey) + g.getEdge(currentNodeKey, neighbor.getKey());
                if (alternativePathWeight < distances.get(neighborKey)) {  // if we found a shorter path to this neighbor: (src -> currentNode -> neighbor) - update it to be the shortest in 'distances'
                    distances.put(neighborKey, alternativePathWeight);
                    prev.put(neighborKey, currentNodeKey);  // update back pointer
                }

                if (!seen.contains(neighborKey)) {  // add neighbor to queue if we didn't see him yet
                    closestToSource.add(neighbor);
                    seen.add(neighborKey);
                }
            }
        }

        if (!prev.containsKey(dest)) {
            return null;  // dest was unreachable
        }

        // proceed to restore path
        node_info current = g.getNode(dest);

        while (current.getKey() != src) {
            path.add(current);
            int previousNodeId = prev.get(current.getKey());
            current = g.getNode(previousNodeId);
        }

        path.add(g.getNode(src));

        // we now hold the reversed path - reverse it back
        Collections.reverse(path);

        return path;
    }

    @Override
    public boolean save(String file) {
        try {
            DataOutputStream writer = new DataOutputStream(new FileOutputStream(file));

            /**
             * Format:
             * line 1: number of nodes
             * line 2-n, a line for each node: node id, node tag, node info, (neighbor1, edge weight), (neighbor2, edge weight), ...
             */

            writer.writeInt(g.nodeSize());

            for (node_info node : g.getV()) {  // for each node in the graph
                // write node info
                writer.writeInt(node.getKey());
                writer.writeUTF(node.getInfo() != null ? node.getInfo() : "null");
                writer.writeDouble(node.getTag());

                // write node neighbors
                var neighbors = g.getV(node.getKey());
                writer.writeInt(neighbors.size());

                for (node_info neighbor : neighbors) {
                    writer.writeInt(neighbor.getKey());  // neighbor key
                    double edgeWeight = g.getEdge(node.getKey(), neighbor.getKey());
                    writer.writeDouble(edgeWeight);  // edge weight
                }
            }

            writer.close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean load(String file) {
        try {
            DataInputStream reader = new DataInputStream(new FileInputStream(file));

            int numberOfNodes = reader.readInt();

            g = new WGraph_DS();

            for (int i = 0; i < numberOfNodes; i++) {
                int nodeId = reader.readInt();
                String nodeInfo = reader.readUTF();
                double nodeTag = reader.readDouble();

                // set node info
                g.addNode(nodeId);
                g.getNode(nodeId).setInfo(nodeInfo.equals("null") ? null : nodeInfo);
                g.getNode(nodeId).setTag(nodeTag);

                // connect edges to hsi neighbors
                int numberOfNeighbors = reader.readInt();

                for (int j = 0; j < numberOfNeighbors; j++) {
                    int neighborId = reader.readInt();
                    double edgeWeight = reader.readDouble();

                    if (!g.hasEdge(nodeId, neighborId)) {  // edge doesn't exist yet
                        g.addNode(neighborId);  // add neighbor to graph (if not added yet)
                        g.connect(nodeId, neighborId, edgeWeight);
                    }
                }
            }

            reader.close();
            return true;
        }

        catch (Exception e) {
            return false;
        }
    }
}
