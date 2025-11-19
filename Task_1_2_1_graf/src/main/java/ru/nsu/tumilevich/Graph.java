package ru.nsu.tumilevich;

import java.io.IOException;
import java.util.List;

public interface Graph {
    // Основные операции
    void addVertex(String vertex);
    void removeVertex(String vertex);
    void addEdge(String source, String destination);
    void removeEdge(String source, String destination);
    List<String> getNeighbors(String vertex);

    // Чтение из файла
    void readFromFile(String filename) throws IOException;

    // Дополнительные операции
    List<String> getVertices();
    int getVertexCount();
    int getEdgeCount();

    // Алгоритмы
    List<String> topologicalSort();

    // Стандартные методы
    @Override
    boolean equals(Object obj);
    @Override
    String toString();
}

