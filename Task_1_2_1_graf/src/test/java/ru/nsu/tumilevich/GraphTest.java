package ru.nsu.tumilevich;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.util.*;

class GraphTest {

	@Test
	void testAllGraphImplementations() {
		testBasicOperations(new AdjacencyListGraph());
		testBasicOperations(new AdjacencyMatrixGraph());
		testBasicOperations(new IncidenceMatrixGraph());
	}

	private void testBasicOperations(Graph graph) {
		assertTrue(graph.addVertex("A"));
		assertTrue(graph.addVertex("B"));
		assertTrue(graph.addVertex("C"));
		assertFalse(graph.addVertex("A")); // Дубликат

		assertEquals(3, graph.getVertexCount());
		assertTrue(graph.hasVertex("A"));
		assertTrue(graph.hasVertex("B"));
		assertTrue(graph.hasVertex("C"));

		assertTrue(graph.addEdge("A", "B"));
		assertTrue(graph.addEdge("B", "C"));
		assertTrue(graph.addEdge("A", "C"));
		assertFalse(graph.addEdge("A", "B"));
		assertFalse(graph.addEdge("D", "E"));

		assertEquals(3, graph.getEdgeCount());
		assertTrue(graph.hasEdge("A", "B"));
		assertTrue(graph.hasEdge("B", "C"));
		assertTrue(graph.hasEdge("A", "C"));

		assertEquals(Arrays.asList("B", "C"), graph.getNeighbors("A"));
		assertEquals(Collections.singletonList("C"), graph.getNeighbors("B"));
		assertEquals(Collections.emptyList(), graph.getNeighbors("C"));

		assertTrue(graph.removeEdge("A", "B"));
		assertFalse(graph.hasEdge("A", "B"));
		assertEquals(2, graph.getEdgeCount());

		assertTrue(graph.removeVertex("B"));
		assertFalse(graph.hasVertex("B"));
		assertEquals(2, graph.getVertexCount());
		assertEquals(1, graph.getEdgeCount());
		assertEquals(Collections.singletonList("C"), graph.getNeighbors("A"));
	}

	@Test
	void testGraphEquality() {
		Graph graph1 = new AdjacencyListGraph();
		Graph graph2 = new AdjacencyMatrixGraph();
		Graph graph3 = new IncidenceMatrixGraph();

		for (String vertex : Arrays.asList("A", "B", "C", "D")) {
			graph1.addVertex(vertex);
			graph2.addVertex(vertex);
			graph3.addVertex(vertex);
		}

		for (String[] edge : new String[][]{{"A", "B"}, {"B", "C"}, {"C", "D"}, {"A", "D"}}) {
			graph1.addEdge(edge[0], edge[1]);
			graph2.addEdge(edge[0], edge[1]);
			graph3.addEdge(edge[0], edge[1]);
		}

		assertEquals(graph1, graph2);
		assertEquals(graph1, graph3);
		assertEquals(graph2, graph3);

		graph1.removeEdge("A", "B");
		assertNotEquals(graph1, graph2);
	}

	@Test
	void testReadFromFile() throws IOException {
		String content = "A\nB\nC\nD\nEDGES:\nA B\nB C\nC D\nA D\n";
		File tempFile = File.createTempFile("graph_test", ".txt");
		try (PrintWriter writer = new PrintWriter(tempFile)) {
			writer.print(content);
		}

		Graph[] graphs = {
				new AdjacencyListGraph(),
				new AdjacencyMatrixGraph(),
				new IncidenceMatrixGraph()
		};

		for (Graph graph : graphs) {
			graph.readFromFile(tempFile.getAbsolutePath());

			assertEquals(4, graph.getVertexCount());
			assertEquals(4, graph.getEdgeCount());

			assertEquals(Arrays.asList("B", "D"), graph.getNeighbors("A"));
			assertEquals(Collections.singletonList("C"), graph.getNeighbors("B"));
			assertEquals(Collections.singletonList("D"), graph.getNeighbors("C"));
			assertEquals(Collections.emptyList(), graph.getNeighbors("D"));
		}

		tempFile.delete();
	}

	@Test
	void testTopologicalSortDFS() {
		Graph graph = new AdjacencyListGraph();
		setupDAG(graph);

		List<String> sorted = TopologicalSort.sortDFS(graph);
		validateTopologicalOrder(graph, sorted);
	}

	@Test
	void testTopologicalSortKahn() {
		Graph graph = new AdjacencyListGraph();
		setupDAG(graph);

		List<String> sorted = TopologicalSort.sortKahn(graph);
		validateTopologicalOrder(graph, sorted);
	}

	@Test
	void testTopologicalSortWithCycle() {
		Graph graph = new AdjacencyListGraph();

		for (String vertex : Arrays.asList("A", "B", "C")) {
			graph.addVertex(vertex);
		}
		graph.addEdge("A", "B");
		graph.addEdge("B", "C");
		graph.addEdge("C", "A");

		assertThrows(IllegalArgumentException.class, () -> TopologicalSort.sortDFS(graph));
		assertThrows(IllegalArgumentException.class, () -> TopologicalSort.sortKahn(graph));
	}

	@Test
	void testTopologicalSortEdgeCases() {
		Graph emptyGraph = new AdjacencyListGraph();
		assertTrue(TopologicalSort.sortDFS(emptyGraph).isEmpty());
		assertTrue(TopologicalSort.sortKahn(emptyGraph).isEmpty());

		Graph singleVertexGraph = new AdjacencyListGraph();
		singleVertexGraph.addVertex("A");
		assertEquals(Arrays.asList("A"), TopologicalSort.sortDFS(singleVertexGraph));
		assertEquals(Arrays.asList("A"), TopologicalSort.sortKahn(singleVertexGraph));
	}

	private void setupDAG(Graph graph) {
		for (String vertex : Arrays.asList("A", "B", "C", "D", "E", "F")) {
			graph.addVertex(vertex);
		}

		graph.addEdge("A", "B");
		graph.addEdge("A", "C");
		graph.addEdge("B", "D");
		graph.addEdge("C", "D");
		graph.addEdge("D", "E");
		graph.addEdge("B", "F");
	}

	private void validateTopologicalOrder(Graph graph, List<String> sorted) {
		assertEquals(graph.getVertexCount(), sorted.size());

		for (String vertex : graph.getVertices()) {
			for (String neighbor : graph.getNeighbors(vertex)) {
				int uIndex = sorted.indexOf(vertex);
				int vIndex = sorted.indexOf(neighbor);
				assertTrue(uIndex < vIndex,
						"Нарушение топологического порядка: " + vertex + " должно быть перед " + neighbor);
			}
		}
	}
}