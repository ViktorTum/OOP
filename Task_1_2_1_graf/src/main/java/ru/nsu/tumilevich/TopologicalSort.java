package ru.nsu.tumilevich;

import java.util.*;

class TopologicalSort {

    // Алгоритм на основе DFS
    public static List<String> sortDFS(Graph graph) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> temp = new HashSet<>(); // для обнаружения циклов

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

    private static boolean dfs(String vertex, Graph graph,
                               Set<String> visited, Set<String> temp,
                               List<String> result) {
        if (temp.contains(vertex)) return true; // обнаружен цикл
        if (visited.contains(vertex)) return false;

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

    // Алгоритм Кана (на основе входящих степеней)
    public static List<String> sortKahn(Graph graph) {
        Map<String, Integer> inDegree = new HashMap<>();

        // Инициализация входящих степеней
        for (String vertex : graph.getVertices()) {
            inDegree.put(vertex, 0);
        }

        // Вычисление входящих степеней
        for (String vertex : graph.getVertices()) {
            for (String neighbor : graph.getNeighbors(vertex)) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        // Очередь вершин с нулевой входящей степенью
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
}
