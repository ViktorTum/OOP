import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public abstract class AbstractGraph implements Graph {
    protected Set<String> vertices;

    public AbstractGraph() {
        this.vertices = new HashSet<>();
    }

    @Override
    public void addVertex(String vertex) {
        vertices.add(vertex);
    }

    @Override
    public void removeVertex(String vertex) {
        vertices.remove(vertex);
    }

    @Override
    public List<String> getVertices() {
        return new ArrayList<>(vertices);
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    @Override
    public void readFromFile(String filename) throws IOException {
        vertices.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean readingVertices = false;
            boolean readingEdges = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) continue;

                if (line.equals("VERTICES:")) {
                    readingVertices = true;
                    readingEdges = false;
                    continue;
                } else if (line.equals("EDGES:")) {
                    readingVertices = false;
                    readingEdges = true;
                    continue;
                }

                if (readingVertices) {
                    addVertex(line);
                } else if (readingEdges) {
                    String[] parts = line.split("\\s+");
                    if (parts.length == 2) {
                        addEdge(parts[0], parts[1]);
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Graph other = (Graph) obj;
        if (!vertices.equals(new HashSet<>(other.getVertices()))) {
            return false;
        }

        for (String vertex : vertices) {
            List<String> thisNeighbors = getNeighbors(vertex);
            List<String> otherNeighbors = other.getNeighbors(vertex);
            if (!new HashSet<>(thisNeighbors).equals(new HashSet<>(otherNeighbors))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public abstract String toString();
}