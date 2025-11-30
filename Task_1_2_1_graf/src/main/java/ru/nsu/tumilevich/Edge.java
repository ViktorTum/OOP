package ru.nsu.tumilevich;

import java.util.Objects;

// Вспомогательный класс для хранения ребра
class Edge {
    String source;
    String destination;

    public Edge(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Edge edge = (Edge) obj;
        return Objects.equals(source, edge.source) &&
                Objects.equals(destination, edge.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }

    @Override
    public String toString() {
        return source + "->" + destination;
    }
}