package ru.nsu.tumilevich;

import java.io.*;
import java.util.*;


public class AdjacencyMatrixGraph implements Graph {
    private final List<String> vertices;           // Список вершин
    private final Map<String, Integer> vertexIndex; // Карта для быстрого доступа к индексам вершин
    private boolean[][] adjacencyMatrix;           // Матрица смежности


    public AdjacencyMatrixGraph() {
        vertices = new ArrayList<>();
        vertexIndex = new HashMap<>();
        adjacencyMatrix = new boolean[0][0];
    }

    @Override
    public boolean addVertex(String vertex) {
        if (vertex == null || vertexIndex.containsKey(vertex)) {
            return false;
        }

        vertices.add(vertex);
        vertexIndex.put(vertex, vertices.size() - 1);

        boolean[][] newMatrix = new boolean[vertices.size()][vertices.size()];
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            System.arraycopy(adjacencyMatrix[i], 0, newMatrix[i], 0, adjacencyMatrix[i].length);
        }
        adjacencyMatrix = newMatrix;

        return true;
    }

    @Override
    public boolean removeVertex(String vertex) {
        if (!vertexIndex.containsKey(vertex)) {
            return false;
        }

        int index = vertexIndex.get(vertex);

        vertices.remove(index);
        vertexIndex.remove(vertex);

        for (int i = 0; i < vertices.size(); i++) {
            vertexIndex.put(vertices.get(i), i);
        }

        boolean[][] newMatrix = new boolean[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                int oldI = i < index ? i : i + 1;
                int oldJ = j < index ? j : j + 1;
                if (oldI < adjacencyMatrix.length && oldJ < adjacencyMatrix.length) {
                    newMatrix[i][j] = adjacencyMatrix[oldI][oldJ];
                }
            }
        }
        adjacencyMatrix = newMatrix;

        return true;
    }

    @Override
    public boolean addEdge(String source, String destination) {
        if (!vertexIndex.containsKey(source) || !vertexIndex.containsKey(destination)) {
            return false;
        }

        int sourceIdx = vertexIndex.get(source);
        int destIdx = vertexIndex.get(destination);

        if (adjacencyMatrix[sourceIdx][destIdx]) {
            return false;
        }

        adjacencyMatrix[sourceIdx][destIdx] = true;
        return true;
    }

    @Override
    public boolean removeEdge(String source, String destination) {
        if (!vertexIndex.containsKey(source) || !vertexIndex.containsKey(destination)) {
            return false;
        }

        int sourceIdx = vertexIndex.get(source);
        int destIdx = vertexIndex.get(destination);

        if (!adjacencyMatrix[sourceIdx][destIdx]) {
            return false;
        }

        adjacencyMatrix[sourceIdx][destIdx] = false;
        return true;
    }

    @Override
    public List<String> getNeighbors(String vertex) {
        if (!vertexIndex.containsKey(vertex)) {
            return Collections.emptyList();
        }

        int vertexIdx = vertexIndex.get(vertex);
        List<String> neighbors = new ArrayList<>();

        for (int j = 0; j < vertices.size(); j++) {
            if (adjacencyMatrix[vertexIdx][j]) {
                neighbors.add(vertices.get(j));
            }
        }

        return neighbors;
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
        return new HashSet<>(vertices);
    }

    @Override
    public boolean hasVertex(String vertex) {
        return vertexIndex.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(String source, String destination) {
        if (!vertexIndex.containsKey(source) || !vertexIndex.containsKey(destination)) {
            return false;
        }

        int sourceIdx = vertexIndex.get(source);
        int destIdx = vertexIndex.get(destination);
        return adjacencyMatrix[sourceIdx][destIdx];
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    @Override
    public int getEdgeCount() {
        int count = 0;
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (adjacencyMatrix[i][j]) {
                    count++;
                }
            }
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
        sb.append("AdjacencyMatrixGraph {\n");
        sb.append("  Vertices: ").append(vertices).append("\n");

        // Вывод матрицы смежности
        sb.append("  Adjacency Matrix:\n");
        sb.append("     ");
        for (String vertex : vertices) {
            sb.append(String.format("%-3s", vertex.substring(0, Math.min(3, vertex.length())) + " "));
        }
        sb.append("\n");

        for (int i = 0; i < vertices.size(); i++) {
            sb.append(String.format("%-4s", vertices.get(i) + ": "));
            for (int j = 0; j < vertices.size(); j++) {
                sb.append(String.format("%-3s", adjacencyMatrix[i][j] ? "1" : "0"));
            }
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private Set<Edge> getEdges() {
        Set<Edge> edges = new HashSet<>();
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (adjacencyMatrix[i][j]) {
                    edges.add(new Edge(vertices.get(i), vertices.get(j)));
                }
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