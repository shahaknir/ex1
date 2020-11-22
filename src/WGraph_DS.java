package ex1;

import java.util.*;


public class WGraph_DS implements weighted_graph {

	public class NodeInfo implements node_info {
		private int id;
		private String info;
		private double tag;

		public NodeInfo(int id) {
			this.id = id;
		}

		@Override
		public int getKey() {
			return this.id;
		}

		
		@Override
		public String getInfo() {
			return this.info;
		}

		
		@Override
		public void setInfo(String s) {
			this.info = s;
		}

		
		@Override
		public double getTag() {
			return this.tag;
		}

		
		@Override
		public void setTag(double t) {
			this.tag = t;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			NodeInfo nodeInfo = (NodeInfo) o;
			return id == nodeInfo.id &&
					Double.compare(nodeInfo.tag, tag) == 0 &&
					Objects.equals(info, nodeInfo.info);
		}
	}

	private HashMap<Integer, node_info> graphNodes;  // node id to node
	private HashMap<Integer, HashMap<Integer, Double>> adjacencyLists;  // maps from each node, to a map of node to edge weight (node to his neighbors with weight)
																		// for example - if the edge (1,5) exists with weight of 3 - there is map from 1 to 5 to 3
	int nodeCount;
	int edgeCount;
	int mcCount;

	public WGraph_DS() {
		graphNodes = new HashMap<Integer, node_info>();
		adjacencyLists = new HashMap<Integer, HashMap<Integer, Double>>();
		nodeCount = 0;
		edgeCount = 0;
		mcCount = 0;
	}

	@Override
	public node_info getNode(int key) {
		if (this.graphNodes.containsKey(key)) {
			return this.graphNodes.get(key);
		}

		return null;
	}

	
	@Override
	public boolean hasEdge(int node1, int node2) {
		var firstNodeNeighbors = this.adjacencyLists.getOrDefault(node1, null);

		if (firstNodeNeighbors == null) { // node1 doesn't exist
			return false;
		}

		if (firstNodeNeighbors.containsKey(node2)) {
			return true;
		}
		
		return false;
	}

	@Override
	public double getEdge(int node1, int node2) {
		if (!hasEdge(node1, node2)) {
			return -1;
		}

		return this.adjacencyLists.get(node1).get(node2);
	}

	@Override
	public void addNode(int key) {
		if (getNode(key) != null) {
			return;  // node with such key already exists - do nothing
		}

		node_info newNode = new NodeInfo(key);
		graphNodes.put(key, newNode);
		adjacencyLists.put(key, new HashMap<Integer, Double>());  // set new hash map for his future neighbors

		nodeCount++;
		mcCount++;
	}

	@Override
	public void connect(int node1, int node2, double w) {
		if (w < 0 || node1 == node2) {
			return;  // we disallow negative edges or self loops
		}

		if (!graphNodes.containsKey(node1) || !graphNodes.containsKey(node2)) {
			return;  // one of the nodes does not exist in the graph
		}

		if (!hasEdge(node1, node2)) {  // new edge
			edgeCount++;
			mcCount++;
		}

		adjacencyLists.get(node1).put(node2, w);
		adjacencyLists.get(node2).put(node1, w);
	}

	@Override
	public Collection<node_info> getV() {
		return graphNodes.values();
	}

	@Override
	public Collection<node_info> getV(int node_id) {
		if (getNode(node_id) == null) {
			return null;  // node does not exist
		}

		var neighborIds = adjacencyLists.get(node_id).keySet();  // we have list of ids - need to convert them to list of node_data

		var neighbors = new ArrayList<node_info>();

		for (int neighborId : neighborIds) {
			neighbors.add(graphNodes.get(neighborId));
		}

		return neighbors;
	}

	@Override
	public node_info removeNode(int key) {
		if (getNode(key) == null) {
			return null;  // node doesn't exist
		}

		HashMap<Integer, Double> neighborsIds = adjacencyLists.get(key);

		for (int neighborId : neighborsIds.keySet()) {  // iterate over all neighbors of the node
			adjacencyLists.get(neighborId).remove(key);  // remove the node from their list
			edgeCount--;
			mcCount++;
		}

		adjacencyLists.remove(key);  // remove the adjacency list of the removed node
		node_info removedNode = graphNodes.remove(key);  // remove the node itself

		nodeCount--;
		mcCount++;

		return removedNode;
	}

	@Override
	public void removeEdge(int node1, int node2) {
		if (!hasEdge(node1, node2)) {
			return;
		}

		adjacencyLists.get(node1).remove(node2);
		adjacencyLists.get(node2).remove(node1);
		edgeCount--;
		mcCount++;
	}

	@Override
	public int nodeSize() {
		return nodeCount;
	}

	@Override
	public int edgeSize() {
		return edgeCount;
	}

	@Override
	public int getMC() {
		return mcCount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WGraph_DS wGraph_ds = (WGraph_DS) o;
		return graphNodes.equals(wGraph_ds.graphNodes) &&
				adjacencyLists.equals(wGraph_ds.adjacencyLists);
	}
}