package ru.nsu.tumilevich;

import java.util.*;
import java.io.*;

interface Graph {
    // Основные операции
    boolean addVertex(String vertex);
    boolean removeVertex(String vertex);
    boolean addEdge(String source, String destination);
    boolean removeEdge(String source, String destination);
    List<String> getNeighbors(String vertex);
    void readFromFile(String filename) throws IOException;

    // Дополнительные операции
    Set<String> getVertices();
    boolean hasVertex(String vertex);
    boolean hasEdge(String source, String destination);
    int getVertexCount();
    int getEdgeCount();

    // Стандартные методы
    boolean equals(Object obj);
    String toString();
    int hashCode();
}

