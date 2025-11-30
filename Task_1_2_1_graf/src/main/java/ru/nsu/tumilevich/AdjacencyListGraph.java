package ru.nsu.tumilevich;

import java.util.*;
import java.io.*;

class AdjacencyListGraph implements Graph {
    private final Map<String, List<String>> adjList;
    private int edgeCount;

    public AdjacencyListGraph() {
        adjList = new HashMap<>();
        edgeCount = 0;
    }

    @Override
    public boolean addVertex(String vertex) {
        if (vertex == null || adjList.containsKey(vertex)) {
            return false;
        }
        adjList.put(vertex, new ArrayList<>());
        return true;
    }

    @Override
    public boolean removeVertex(String vertex) {
        if (!adjList.containsKey(vertex)) {
            return false;
        }

        // Удаляем все исходящие ребра
        edgeCount -= adjList.get(vertex).size();
        adjList.remove(vertex);

        // Удаляем все входящие ребра
        for (List<String> neighbors : adjList.values()) {
            edgeCount -= Collections.frequency(neighbors, vertex);
            neighbors.removeIf(neighbor -> neighbor.equals(vertex));
        }

        return true;
    }

    @Override
    public boolean addEdge(String source, String destination) {
        if (!adjList.containsKey(source) || !adjList.containsKey(destination)) {
            return false;
        }

        List<String> neighbors = adjList.get(source);
        if (neighbors.contains(destination)) {
            return false;
        }

        neighbors.add(destination);
        edgeCount++;
        return true;
    }

    @Override
    public boolean removeEdge(String source, String destination) {
        if (!adjList.containsKey(source) || !adjList.containsKey(destination)) {
            return false;
        }

        List<String> neighbors = adjList.get(source);
        boolean removed = neighbors.remove(destination);
        if (removed) {
            edgeCount--;
        }
        return removed;
    }

    @Override
    public List<String> getNeighbors(String vertex) {
        return adjList.getOrDefault(vertex, Collections.emptyList());
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
        return new HashSet<>(adjList.keySet());
    }

    @Override
    public boolean hasVertex(String vertex) {
        return adjList.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(String source, String destination) {
        if (!adjList.containsKey(source)) {
            return false;
        }
        return adjList.get(source).contains(destination);
    }

    @Override
    public int getVertexCount() {
        return adjList.size();
    }

    @Override
    public int getEdgeCount() {
        return edgeCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Graph other)) return false;
        return getVertices().equals(other.getVertices()) &&
                getAllEdges().equals(getAllEdges(other));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVertices(), getAllEdges());
    }

    @Override
    public String toString() {
        String sb = "AdjacencyListGraph {\n" +
                "  Vertices: " + getVertices() + "\n" +
                "  Edges: " + getAllEdges() + "\n" +
                "}";
        return sb;
    }

    private Set<Edge> getAllEdges() {
        return getAllEdges(this);
    }

    private Set<Edge> getAllEdges(Graph graph) {
        Set<Edge> edges = new HashSet<>();
        for (String vertex : graph.getVertices()) {
            for (String neighbor : graph.getNeighbors(vertex)) {
                edges.add(new Edge(vertex, neighbor));
            }
        }
        return edges;
    }
}