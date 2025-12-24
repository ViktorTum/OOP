package ru.nsu.tumilevich;

import java.util.*;

/**
 * Утилитарный класс для выполнения топологической сортировки графа.
 * Класс предоставляет два алгоритма топологической сортировки для ориентированных ациклических графов (DAG):
 * DFS-алгоритм и алгоритм Кана. Оба алгоритма работают с любыми реализациями интерфейса Graph
 * Использование:
 * {
 * Graph graph = new AdjacencyListGraph();
 * // Заполнение графа...
 * List<String> sorted = TopologicalSort.sortDFS(graph);
 * }
 *
 */

public final class TopologicalSort {
    /**
     * Выполняет топологическую сортировку графа с использованием алгоритма DFS (обход в глубину).
     *
     * Алгоритм использует рекурсивный DFS для построения топологического порядка. При обнаружении цикла
     * выбрасывается исключение. Гарантирует, что для каждого ребра (u, v) вершина u будет расположена перед v
     * в результирующем списке.
     */
    public static List<String> sortDFS(Graph graph) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> temp = new HashSet<>();

        for (String vertex : graph.getVertices()) {
            if (!visited.contains(vertex)) {
                if (dfs(vertex, graph, visited, temp, result)) {
                    throw new IllegalArgumentException("Граф содержит циклы! Топологическая сортировка невозможна.");
                }
            }
        }

        Collections.reverse(result);
        return result;
    }

    /**
     * Выполняет топологическую сортировку графа с использованием алгоритма Кана.
     *
     * Алгоритм основан на вычислении входящих степеней вершин. На каждой итерации выбираются вершины
     * с нулевой входящей степенью, добавляются в результат и удаляются из графа. При обнаружении цикла
     * выбрасывается исключение.
     */

    public static List<String> sortKahn(Graph graph) {
        Map<String, Integer> inDegree = new HashMap<>();

        for (String vertex : graph.getVertices()) {
            inDegree.put(vertex, 0);
        }

        for (String vertex : graph.getVertices()) {
            for (String neighbor : graph.getNeighbors(vertex)) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        Queue<String> queue = new LinkedList<>();
        for (String vertex : graph.getVertices()) {
            if (inDegree.get(vertex) == 0) {
                queue.add(vertex);
            }
        }

        List<String> result = new ArrayList<>();
        int visitedCount = 0;

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);
            visitedCount++;

            for (String neighbor : graph.getNeighbors(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (visitedCount != graph.getVertexCount()) {
            throw new IllegalArgumentException("Граф содержит циклы! Топологическая сортировка невозможна.");
        }

        return result;
    }

    /**
     * Вспомогательный метод для DFS-алгоритма, реализующий рекурсивный обход графа.
     */
    private static boolean dfs(String vertex, Graph graph,
                               Set<String> visited, Set<String> temp,
                               List<String> result) {

        if (temp.contains(vertex)) {
            return true;
        }

        if (visited.contains(vertex)) {
            return false;
        }

        temp.add(vertex);

        for (String neighbor : graph.getNeighbors(vertex)) {
            if (dfs(neighbor, graph, visited, temp, result)) {
                return true;
            }
        }

        temp.remove(vertex);
        visited.add(vertex);
        result.add(vertex);

        return false;
    }
}