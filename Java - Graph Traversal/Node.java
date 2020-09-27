package pathfinder;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String name;
    private int x;
    private int y;
    private List<Connection> adjacent;

    public static record Connection(Node node, int weight) { }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.adjacent = new ArrayList<>();
    }

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.adjacent = new ArrayList<>();
    }

    public void addAdjacent(Node node, int weight) {
        this.adjacent.add(new Connection(node, weight));
        node.getAdjacent().add(new Connection(this, weight));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Connection> getAdjacent() {
        return adjacent;
    }

    public boolean matches(Node other) {
        return x == other.getX() && y == other.getY();
    }

    @Override
    public String toString() {
        return name == null
                ? String.format("(x=%d, y=%d)", x, y)
                : String.format("[%s (x=%d, y=%d)]", name, x, y);
    }
}
