package ru.nsu.tumilevich;

import java.io.*;
import java.util.*;

/**
 * Реализация графа с использованием матрицы инцидентности.
 * В матрице инцидентности:
 * - 1 означает, что вершина является началом ребра
 * - -1 означает, что вершина является концом ребра
 * - 0 означает, что вершина не инцидентна ребру
 */

public class IncidenceMatrixGraph implements Graph {
    private final List<String> vertices;           // Список вершин
    private final List<Edge> edges;                // Список ребер
    private int[][] incidenceMatrix;              // Матрица инцидентности
    private final Map<String, Integer> vertexIndex; // Карта для быстрого доступа к индексам вершин

    public IncidenceMatrixGraph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        vertexIndex = new HashMap<>();
        incidenceMatrix = new int[0][0];
    }

    @Override
    public boolean addVertex(String vertex) {
        if (vertex == null || vertexIndex.containsKey(vertex)) {
            return false;
        }

        vertices.add(vertex);
        vertexIndex.put(vertex, vertices.size() - 1);

        int[][] newMatrix = new int[vertices.size()][edges.size()];
        for (int i = 0; i < incidenceMatrix.length; i++) {
            System.arraycopy(incidenceMatrix[i], 0, newMatrix[i], 0, incidenceMatrix[i].length);
        }
        incidenceMatrix = newMatrix;

        return true;
    }

    @Override
    public boolean removeVertex(String vertex) {
        if (!vertexIndex.containsKey(vertex)) {
            return false;
        }

        int index = vertexIndex.get(vertex);

        List<Edge> edgesToRemove = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.source.equals(vertex) || edge.destination.equals(vertex)) {
                edgesToRemove.add(edge);
            }
        }

        for (Edge edge : edgesToRemove) {
            removeEdge(edge.source, edge.destination);
        }

        vertices.remove(index);
        vertexIndex.remove(vertex);

        for (int i = 0; i < vertices.size(); i++) {
            vertexIndex.put(vertices.get(i), i);
        }

        int[][] newMatrix = new int[vertices.size()][edges.size()];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                int oldI = i < index ? i : i + 1;
                if (oldI < incidenceMatrix.length) {
                    newMatrix[i][j] = incidenceMatrix[oldI][j];
                }
            }
        }
        incidenceMatrix = newMatrix;

        return true;
    }

    @Override
    public boolean addEdge(String source, String destination) {
        if (!vertexIndex.containsKey(source) || !vertexIndex.containsKey(destination)) {
            return false;
        }

        for (Edge edge : edges) {
            if (edge.source.equals(source) && edge.destination.equals(destination)) {
                return false;
            }
        }

        Edge newEdge = new Edge(source, destination);
        edges.add(newEdge);

        int[][] newMatrix = new int[vertices.size()][edges.size()];
        for (int i = 0; i < incidenceMatrix.length; i++) {
            System.arraycopy(incidenceMatrix[i], 0, newMatrix[i], 0, incidenceMatrix[i].length);
        }
        incidenceMatrix = newMatrix;

        int sourceIdx = vertexIndex.get(source);
        int destIdx = vertexIndex.get(destination);
        int edgeIdx = edges.size() - 1;

        incidenceMatrix[sourceIdx][edgeIdx] = 1;
        incidenceMatrix[destIdx][edgeIdx] = -1;

        return true;
    }

    @Override
    public boolean removeEdge(String source, String destination) {
        int edgeIndex = -1;
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            if (edge.source.equals(source) && edge.destination.equals(destination)) {
                edgeIndex = i;
                break;
            }
        }

        if (edgeIndex == -1) {
            return false;
        }

        edges.remove(edgeIndex);

        int[][] newMatrix = new int[vertices.size()][edges.size()];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                int oldJ = j < edgeIndex ? j : j + 1;
                newMatrix[i][j] = incidenceMatrix[i][oldJ];
            }
        }
        incidenceMatrix = newMatrix;

        return true;
    }

    @Override
    public List<String> getNeighbors(String vertex) {
        if (!vertexIndex.containsKey(vertex)) {
            return Collections.emptyList();
        }

        int vertexIdx = vertexIndex.get(vertex);
        List<String> neighbors = new ArrayList<>();

        for (int j = 0; j < edges.size(); j++) {
            if (incidenceMatrix[vertexIdx][j] == 1) {
                for (int i = 0; i < vertices.size(); i++) {
                    if (incidenceMatrix[i][j] == -1) {
                        neighbors.add(vertices.get(i));
                        break;
                    }
                }
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
        for (Edge edge : edges) {
            if (edge.source.equals(source) && edge.destination.equals(destination)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    @Override
    public int getEdgeCount() {
        return edges.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Graph other)) return false;

        return getVertices().equals(other.getVertices()) &&
                getAllEdges().equals(getAllEdgesFromGraph(other));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVertices(), getAllEdges());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IncidenceMatrixGraph {\n");
        sb.append("  Vertices: ").append(vertices).append("\n");
        sb.append("  Edges: ").append(edges).append("\n");

        sb.append("  Incidence Matrix:\n");
        sb.append("       ");
        for (int j = 0; j < edges.size(); j++) {
            sb.append(String.format("e%d ", j));
        }
        sb.append("\n");

        for (int i = 0; i < vertices.size(); i++) {
            sb.append(String.format("%-5s", vertices.get(i) + ": "));
            for (int j = 0; j < edges.size(); j++) {
                sb.append(String.format("%2d ", incidenceMatrix[i][j]));
            }
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private Set<Edge> getAllEdges() {
        return new HashSet<>(edges);
    }


    private Set<Edge> getAllEdgesFromGraph(Graph graph) {
        Set<Edge> edges = new HashSet<>();
        for (String vertex : graph.getVertices()) {
            for (String neighbor : graph.getNeighbors(vertex)) {
                edges.add(new Edge(vertex, neighbor));
            }
        }
        return edges;
    }
}