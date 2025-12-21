package ru.nsu.tumilevich;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Интерфейс для работы с ориентированными графами.
 *
 * Как использовать этот интерфейс:
 *   Создайте экземпляр графа: {Graph graph = new AdjacencyListGraph();}
 *   Добавьте вершины: {graph.addVertex("A");}
 *   Добавьте ребра: {graph.addEdge("A", "B");}
 *   Получите список соседей: {List<String> neighbors = graph.getNeighbors("A");}
 *   Прочитайте граф из файла: {graph.readFromFile("graph.txt");}
 *
 * Для разработчиков:
 *   При создании новой реализации графа (например, гибридного представления),
 *       убедитесь, что все методы интерфейса реализованы корректно.
 *   Методы equals() и hashCode() должны основываться на структуре графа
 *       (вершины и ребра), а не на внутренней реализации.
 *   Метод toString() должен возвращать читаемое представление графа для отладки.
 */

public interface Graph {
    boolean addVertex(String vertex);
    boolean removeVertex(String vertex);
    boolean addEdge(String source, String destination);
    boolean removeEdge(String source, String destination);
    List<String> getNeighbors(String vertex);
    void readFromFile(String filename) throws IOException;

    Set<String> getVertices();
    boolean hasVertex(String vertex);
    boolean hasEdge(String source, String destination);
    int getVertexCount();
    int getEdgeCount();
    boolean equals(Object obj);
    int hashCode();
    String toString();
}
