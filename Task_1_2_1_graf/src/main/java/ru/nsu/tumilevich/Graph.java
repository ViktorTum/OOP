package ru.nsu.tumilevich;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
    String toString();
    int hashCode();
}