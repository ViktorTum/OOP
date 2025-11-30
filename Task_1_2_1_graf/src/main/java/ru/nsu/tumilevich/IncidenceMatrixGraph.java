package ru.nsu.tumilevich;

import java.util.*;
import java.io.*;

class IncidenceMatrixGraph implements Graph {
    private final List<String> vertices;
    private final List<Edge> edges;
    private int[][] incidenceMatrix;
    private final Map<String, Integer> vertexIndex;

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

        // Расширяем матрицу инцидентности
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

        // Находим все ребра, связанные с вершиной
        List<Edge> edgesToRemove = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.source.equals(vertex) || edge.destination.equals(vertex)) {
                edgesToRemove.add(edge);
            }
        }

        // Удаляем ребра
        for (Edge edge : edgesToRemove) {
            removeEdge(edge.source, edge.destination);
        }

        // Удаляем вершину
        vertices.remove(index);
        vertexIndex.remove(vertex);

        // Обновляем индексы
        for (int i = 0; i < vertices.size(); i++) {
            vertexIndex.put(vertices.get(i), i);
        }

        // Создаем новую матрицу
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

        // Проверяем, существует ли уже такое ребро
        for (Edge edge : edges) {
            if (edge.source.equals(source) && edge.destination.equals(destination)) {
                return false;
            }
        }

        // Добавляем ребро
        Edge newEdge = new Edge(source, destination);
        edges.add(newEdge);

        // Расширяем матрицу инцидентности
        int[][] newMatrix = new int[vertices.size()][edges.size()];
        for (int i = 0; i < incidenceMatrix.length; i++) {
            System.arraycopy(incidenceMatrix[i], 0, newMatrix[i], 0, incidenceMatrix[i].length);
        }
        incidenceMatrix = newMatrix;

        // Заполняем новый столбец
        int sourceIdx = vertexIndex.get(source);
        int destIdx = vertexIndex.get(destination);
        int edgeIdx = edges.size() - 1;

        incidenceMatrix[sourceIdx][edgeIdx] = 1;   // Исходная вершина
        incidenceMatrix[destIdx][edgeIdx] = -1;    // Конечная вершина

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

        // Удаляем ребро
        edges.remove(edgeIndex);

        // Создаем новую матрицу без удаленного столбца
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
            if (incidenceMatrix[vertexIdx][j] == 1) { // Вершина является исходной
                // Находим конечную вершину для этого ребра
                for (int i = 0; i < vertices.size(); i++) {
                    if (incidenceMatrix[i][j] == -1) { // Конечная вершина
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
                getAllEdges().equals(getAllEdges(other));
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

        // Вывод матрицы инцидентности для отладки
        sb.append("  Incidence Matrix:\n");
        for (int i = 0; i < vertices.size(); i++) {
            sb.append("    ").append(vertices.get(i)).append(": ");
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