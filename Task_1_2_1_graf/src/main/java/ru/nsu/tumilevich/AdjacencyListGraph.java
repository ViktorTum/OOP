package ru.nsu.tumilevich;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class AdjacencyListGraph implements Graph {
    private final Map<String, List<String>> adjacencyList;

    public AdjacencyListGraph() {
        adjacencyList = new HashMap<>();
    }

    @Override
    public boolean addVertex(String vertex) {
        if (vertex == null || adjacencyList.containsKey(vertex)) {
            return false;
        }

        adjacencyList.put(vertex, new ArrayList<>());
        return true;
    }

    @Override
    public boolean removeVertex(String vertex) {
        if (!adjacencyList.containsKey(vertex)) {
            return false;
        }

        for (List<String> neighbors : adjacencyList.values()) {
            neighbors.remove(vertex);
        }

        adjacencyList.remove(vertex);
        return true;
    }

    @Override
    public boolean addEdge(String source, String destination) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            return false;
        }

        List<String> neighbors = adjacencyList.get(source);
        if (neighbors.contains(destination)) {
            return false;
        }

        neighbors.add(destination);
        return true;
    }

    @Override
    public boolean removeEdge(String source, String destination) {
        if (!adjacencyList.containsKey(source)) {
            return false;
        }

        return adjacencyList.get(source).remove(destination);
    }

    @Override
    public List<String> getNeighbors(String vertex) {
        if (!adjacencyList.containsKey(vertex)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(adjacencyList.get(vertex));
    }

    @Override
    public void readFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean readingVertices = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals("EDGES:")) {
                    readingVertices = false;
                    continue;
                }

                if (readingVertices) {
                    addVertex(line);
                } else {
                    String[] parts = line.split("\\s+");
                    if (parts.length == 2) {
                        addEdge(parts[0], parts[1]);
                    }
                }
            }
        }
    }

    @Override
    public Set<String> getVertices() {
        return new HashSet<>(adjacencyList.keySet());
    }

    @Override
    public boolean hasVertex(String vertex) {
        return adjacencyList.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(String source, String destination) {
        if (!adjacencyList.containsKey(source)) {
            return false;
        }

        return adjacencyList.get(source).contains(destination);
    }

    @Override
    public int getVertexCount() {
        return adjacencyList.size();
    }

    @Override
    public int getEdgeCount() {
        int count = 0;
        for (List<String> neighbors : adjacencyList.values()) {
            count += neighbors.size();
        }
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Graph other)) return false;

        return getVertices().equals(other.getVertices()) &&
                getEdges().equals(getEdgesFromGraph(other));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVertices(), getEdges());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AdjacencyListGraph {\n");

        for (String vertex : adjacencyList.keySet()) {
            sb.append("  ").append(vertex).append(": ").append(adjacencyList.get(vertex)).append("\n");
        }

        sb.append("}");
        return sb.toString();
    }

    private Set<Edge> getEdges() {
        Set<Edge> edges = new HashSet<>();
        for (String source : adjacencyList.keySet()) {
            for (String destination : adjacencyList.get(source)) {
                edges.add(new Edge(source, destination));
            }
        }
        return edges;
    }

    private Set<Edge> getEdgesFromGraph(Graph graph) {
        Set<Edge> edges = new HashSet<>();
        for (String vertex : graph.getVertices()) {
            for (String neighbor : graph.getNeighbors(vertex)) {
                edges.add(new Edge(vertex, neighbor));
            }
        }
        return edges;
    }
}