package ru.nsu.tumilevich;

import java.util.*;
import java.io.*;

class AdjacencyMatrixGraph implements Graph {
    private final Map<String, Integer> vertexIndex;
    private final List<String> vertices;
    private boolean[][] matrix;
    private int edgeCount;

    public AdjacencyMatrixGraph() {
        vertexIndex = new HashMap<>();
        vertices = new ArrayList<>();
        matrix = new boolean[0][0];
        edgeCount = 0;
    }

    @Override
    public boolean addVertex(String vertex) {
        if (vertex == null || vertexIndex.containsKey(vertex)) {
            return false;
        }

        vertices.add(vertex);
        vertexIndex.put(vertex, vertices.size() - 1);

        // Расширяем матрицу
        boolean[][] newMatrix = new boolean[vertices.size()][vertices.size()];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, matrix.length);
        }
        matrix = newMatrix;
        return true;
    }

    @Override
    public boolean removeVertex(String vertex) {
        if (!vertexIndex.containsKey(vertex)) {
            return false;
        }

        int index = vertexIndex.get(vertex);

        // Удаляем все связанные ребра
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[index][i]) edgeCount--;
            if (matrix[i][index]) edgeCount--;
        }

        // Удаляем вершину из списков
        vertices.remove(index);
        vertexIndex.remove(vertex);

        // Обновляем индексы
        for (int i = 0; i < vertices.size(); i++) {
            vertexIndex.put(vertices.get(i), i);
        }

        // Создаем новую матрицу
        boolean[][] newMatrix = new boolean[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                int oldI = i < index ? i : i + 1;
                int oldJ = j < index ? j : j + 1;
                if (oldI < matrix.length && oldJ < matrix.length) {
                    newMatrix[i][j] = matrix[oldI][oldJ];
                }
            }
        }
        matrix = newMatrix;
        return true;
    }

    @Override
    public boolean addEdge(String source, String destination) {
        if (!vertexIndex.containsKey(source) || !vertexIndex.containsKey(destination)) {
            return false;
        }

        int sourceIndex = vertexIndex.get(source);
        int destIndex = vertexIndex.get(destination);

        if (matrix[sourceIndex][destIndex]) {
            return false; // Ребро уже существует
        }

        matrix[sourceIndex][destIndex] = true;
        edgeCount++;
        return true;
    }

    @Override
    public boolean removeEdge(String source, String destination) {
        if (!vertexIndex.containsKey(source) || !vertexIndex.containsKey(destination)) {
            return false;
        }

        int sourceIndex = vertexIndex.get(source);
        int destIndex = vertexIndex.get(destination);

        if (!matrix[sourceIndex][destIndex]) {
            return false; // Ребро не существует
        }

        matrix[sourceIndex][destIndex] = false;
        edgeCount--;
        return true;
    }

    @Override
    public List<String> getNeighbors(String vertex) {
        if (!vertexIndex.containsKey(vertex)) {
            return Collections.emptyList();
        }

        int index = vertexIndex.get(vertex);
        List<String> neighbors = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[index][i]) {
                neighbors.add(vertices.get(i));
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
        int sourceIndex = vertexIndex.get(source);
        int destIndex = vertexIndex.get(destination);
        return matrix[sourceIndex][destIndex];
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    @Override
    public int getEdgeCount() {
        return edgeCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Graph other = (Graph) obj;
        return getVertices().equals(other.getVertices()) &&
                getAllEdges().equals(getAllEdges(other));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVertices(), getAllEdges());
    }

    @Override
    public String toString() {
        String sb = "AdjacencyMatrixGraph {\n" +
                "  Vertices: " + vertices + "\n" +
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