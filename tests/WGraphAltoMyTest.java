package ex1;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.Test;

public class WGraphAltoMyTest {
	
		
	@Test
	public void NodeId() {
		weighted_graph graph = CreateGraph();
		node_info FirstNode = graph.getNode(0);
		node_info SecondNode = graph.getNode(2);
		assertNotEquals(FirstNode.getKey(), SecondNode.getKey());
	}
	
	
	@Test
	public void GetNodes() {
		weighted_graph graph = CreateGraph();
		for(node_info CurrNode : graph.getV()) {
			assertEquals(graph.getNode(CurrNode.getKey()), CurrNode);	
		}
	}
	
	@Test
	public void HasEdge() {
		weighted_graph graph = SmallGraph();// All nodes are connected to each other
		for(node_info CurrNode : graph.getV()) {
			for(node_info SecNode : graph.getV()) {
				if (CurrNode.getKey() != SecNode.getKey()) {
					assertTrue(graph.hasEdge(CurrNode.getKey(), SecNode.getKey()));
				}
			}
			
		}
	}
	
	@Test
	public void AddNodeEx() {// No change was made in addNode
		weighted_graph graph = SmallGraph();
		int originalNodeCount = graph.nodeSize();
		graph.addNode(0);
		assertEquals(originalNodeCount, graph.nodeSize());
	}
	
	@Test
	public void ConnectedAllNodes() {
		weighted_graph graph = CreateGraph();
		int originalNodeCount = graph.nodeSize();
		int originalEdgeCount = graph.edgeSize();
		int originalModeCount = graph.getMC();
		
		graph.connect(0,1,8);
        graph.connect(0,2,26);
        graph.connect(0,3,68);
        
        graph.connect(1,4,17);
        graph.connect(1,5,1);
        
        graph.connect(2,4,1);
        graph.connect(2,10,1);
        
        graph.connect(3,5,10);
        graph.connect(3,6,100);
        graph.connect(3,9,10);
        
        graph.connect(4,10,1);
        
        graph.connect(5,7,1.1);
        graph.connect(5,6,1.1);
        graph.connect(5,9,10);
        
        graph.connect(6,7,1.53);
        graph.connect(6,8,30.400);
        graph.connect(6,2,1.53);
        graph.connect(6,4,30.400);
        graph.connect(6,10,2);

        graph.connect(7,10,2);
        graph.connect(7,3,2);

        graph.connect(8,10,2);
        graph.connect(8,10,10);
        graph.connect(8,6,10);
        
        assertEquals(originalNodeCount, graph.nodeSize());
        assertEquals(originalEdgeCount, graph.edgeSize());
        assertEquals(originalModeCount, graph.getMC());
	}
	
	@Test
	public void NumGraphNode() {
		weighted_graph graph = CreateGraph();
		weighted_graph graphSmall = SmallGraph();
		weighted_graph Unconnectedgraph = CreateUnconnectedGraph();
		
		assertEquals(graph.getV().size() + 1,Unconnectedgraph.getV().size());
		assertEquals(graphSmall.getV().size(), 4);
		
	}
	
	@Test 
	public void TestIsConnected() {
		weighted_graph Unconnectedgraph = CreateUnconnectedGraph();
		weighted_graph_algorithms algo = new WGraph_Algo();
		
		algo.init(Unconnectedgraph);
		
		assertFalse(algo.isConnected());
	}
	
	@Test
	public void TestShortestPath() {
		// Arrange
		weighted_graph graph = new WGraph_DS();
		weighted_graph_algorithms algo = new WGraph_Algo();
		
		for (int i = 0; i < 5; i++) {
			graph.addNode(i);
		}
		
		graph.connect(0, 1, 0.5);
		graph.connect(1, 2, 0.5);
		graph.connect(2, 3, 0.5);
		graph.connect(3, 4, 0.5);
		graph.connect(0, 4, 5);
		
		algo.init(graph);
		
		// Act
		var minDistance = algo.shortestPathDist(0, 4);
		var minPath = algo.shortestPath(0, 4);
		var isConnected = algo.isConnected();
		
		// Assert
		assertEquals(2.0, minDistance);
		assertEquals(5, minPath.size());
		for (int i = 0; i < 5; i++) {
			assertEquals(i, minPath.get(i).getKey());
		}
		assertEquals(true, isConnected);
	}
	
	@Test
	public void TestShortestDis() {
		// Arrange
		weighted_graph graph = new WGraph_DS();
		weighted_graph_algorithms algo = new WGraph_Algo();
		
		for (int i = 0; i < 5; i++) {
			graph.addNode(i);
		}
		
		graph.connect(0, 1, 0.5);
		graph.connect(1, 2, 0.5);
		graph.connect(2, 3, 0.5);
		graph.connect(3, 4, 0.5);
		graph.connect(0, 4, 5);
		
		algo.init(graph);
		
		// Act
		var minDistance = algo.shortestPathDist(0, 4);
		var minPath = algo.shortestPath(0, 4);
		var isConnected = algo.isConnected();
		
		// Assert
		assertEquals(2.0, minDistance);
		assertEquals(5, minPath.size());
		for (int i = 0; i < 5; i++) {
			assertEquals(i, minPath.get(i).getKey());
		}
		assertTrue(isConnected);
		graph.removeNode(3);
		minDistance = algo.shortestPathDist(0, 4);
		assertNotEquals(2.0, minDistance);
		graph.removeNode(0);
		isConnected = algo.isConnected();
		assertFalse(isConnected);
		
	}
	
	@Test
	public void SaveLoadTest() {
		// Arrange
		weighted_graph originalGraph = new WGraph_DS();
		weighted_graph_algorithms algo = new WGraph_Algo();
		
		for (int i = 0; i < 5; i++) {
			originalGraph.addNode(i);
		}
		
		originalGraph.connect(0, 1, 0.5);
		originalGraph.connect(1, 2, 0.5);
		originalGraph.connect(2, 3, 0.5);
		originalGraph.connect(3, 4, 0.5);
		originalGraph.connect(0, 4, 5);
		
		// Act
		algo.init(originalGraph);
		boolean saveSuccess = algo.save("GraphSave");
		boolean loadSuccess = algo.load("GraphSave");
		weighted_graph loadedGraph = algo.getGraph();
		
		// Assert
		assertTrue(saveSuccess);
		assertTrue(loadSuccess);
		assertEquals(originalGraph, loadedGraph);
		loadedGraph.removeNode(0);
		assertNotEquals(originalGraph, loadedGraph);
		loadedGraph.addNode(0);
		loadedGraph.connect(0,  1, 0.5);
		loadedGraph.connect(0, 4, 5);
		assertEquals(originalGraph, loadedGraph);
		loadedGraph.removeEdge(0, 4);
		assertNotEquals(originalGraph, loadedGraph);
	}
	
	///////////////////////
	// Private functions //
	///////////////////////
	
	private weighted_graph SmallGraph() {
		weighted_graph SmallConnectedGraph = new WGraph_DS();
		SmallConnectedGraph.addNode(0);
		SmallConnectedGraph.addNode(1);
		SmallConnectedGraph.addNode(2);
		SmallConnectedGraph.addNode(3);
		
		SmallConnectedGraph.connect(0,1,0);
		SmallConnectedGraph.connect(0,2,0);
		SmallConnectedGraph.connect(0,3,0);
		
		SmallConnectedGraph.connect(1,0,0);
		SmallConnectedGraph.connect(1,2,1);
		SmallConnectedGraph.connect(1,3,1);
		
		SmallConnectedGraph.connect(2,3,2);
		SmallConnectedGraph.connect(3,1,6);
		
		return SmallConnectedGraph;
	}
	
	
	public weighted_graph CreateGraph() {
		weighted_graph GraphCreated = new WGraph_DS();
		GraphCreated.addNode(0);
		GraphCreated.addNode(1);
		GraphCreated.addNode(2);
		GraphCreated.addNode(3);
		GraphCreated.addNode(4);
		GraphCreated.addNode(5);
		GraphCreated.addNode(6);
		GraphCreated.addNode(7);
		GraphCreated.addNode(8);
		GraphCreated.addNode(9);
		GraphCreated.addNode(10);
		
		GraphCreated.connect(0,1,8);
        GraphCreated.connect(0,2,26);
        GraphCreated.connect(0,3,68);
        
        GraphCreated.connect(1,4,17);
        GraphCreated.connect(1,5,1);
        
        GraphCreated.connect(2,4,1);
        GraphCreated.connect(2,10,1);
        
        GraphCreated.connect(3,5,10);
        GraphCreated.connect(3,6,100);
        GraphCreated.connect(3,9,10);
        
        GraphCreated.connect(4,10,1);
        
        GraphCreated.connect(5,7,1.1);
        GraphCreated.connect(5,6,1.1);
        GraphCreated.connect(5,9,10);
        
        GraphCreated.connect(6,7,1.53);
        GraphCreated.connect(6,8,30.400);
        GraphCreated.connect(6,2,1.53);
        GraphCreated.connect(6,4,30.400);
        GraphCreated.connect(6,10,2);

        GraphCreated.connect(7,10,2);
        GraphCreated.connect(7,3,2);

        GraphCreated.connect(8,10,2);
        GraphCreated.connect(8,10,10);
        GraphCreated.connect(8,6,10);
        
		return GraphCreated;
	}
	
	
	public weighted_graph CreateUnconnectedGraph() {
		weighted_graph UnconnectedGraph = CreateGraph();
		UnconnectedGraph.addNode(100);
		return UnconnectedGraph;
	}
	
	

}
