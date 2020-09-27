package pathfinder;

import sun.misc.Unsafe;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pathfinder.Node.*;
import static pathfinder.QueryableCollection.*;

public class Pathfinder {

    private PathStrategy strategy;
    private ExecutorService executor;
    private static long segmentBytes;
    private static Unsafe unsafe; // access to the Unsafe singleton
    private static Pathfinder instance; // the Pathfinder singleton

    private Pathfinder() {
        executor = Executors.newCachedThreadPool();
    }

    public static Pathfinder getInstance() {
        if (instance == null)
            instance = new Pathfinder();

        if (unsafe == null) {
            unsafe = getUnsafe();
            segmentBytes = estimateSizeOfSegment();
        }

        return instance;
    }

    public enum PathStrategy {
        Depth, Breadth, Optimal, Uniform, Greedy
    }

    public static record PathSegment(Node latest, PathSegment previous, int cost, int estimate) implements Comparable<PathSegment> {
        @Override
        public int compareTo(PathSegment other) {
            return (cost + estimate) - (other.cost() + other.estimate());
        }
    }

    public static class PathfindingException extends RuntimeException {
        public PathfindingException(String message) {
            super(message);
        }

        public PathfindingException(Node start, Node goal, String message) {
            super(String.format("%s (path from %s to %s).", message, start.toString(), goal.toString()));
        }
    }

    public void setStrategy(PathStrategy strategy) {
        this.strategy = strategy;
    }

    public PathSegment traverse(Node start, Node goal) {
        return handleTraversal(start, goal, (open, closed) -> true);
    }

    public PathSegment traverseTimeLimit(Node start, Node goal, long msLimit) {
        if (msLimit <= 0)
            throw new PathfindingException(start, goal, "Time limit must be greater than 0");

        long now = System.currentTimeMillis(), end = now + msLimit;
        return handleTraversal(start, goal, (open, closed) -> System.currentTimeMillis() < end);
    }

    public PathSegment traverseNodeLimit(Node start, Node goal, int visitLimit) {
        if (visitLimit <= 0)
            throw new PathfindingException(start, goal, "Node limit must be greater than 0");

        return handleTraversal(start, goal, (open, closed) -> closed.size() < visitLimit);
    }

    public PathSegment traverseMemoryLimit(Node start, Node goal, long byteLimit) {
        if (byteLimit <= 0)
            throw new PathfindingException(start, goal, "Memory limit must be greater than 0");

        return handleTraversal(start, goal, (open, closed) -> (open.size() + closed.size()) * segmentBytes < byteLimit);
    }

    public Future<PathSegment> asyncTraverse(Node start, Node goal) {
        return executor.submit(() -> traverse(start, goal));
    }

    public Future<PathSegment> asyncTraverseTimeLimit(Node start, Node goal, long msLimit) {
        return executor.submit(() -> traverseTimeLimit(start, goal, msLimit));
    }

    public Future<PathSegment> asyncTraverseNodeLimit(Node start, Node goal, int visitLimit) {
        return executor.submit(() -> traverseNodeLimit(start, goal, visitLimit));
    }

    public Future<PathSegment> asyncTraverseMemoryLimit(Node start, Node goal, long byteLimit) {
        return executor.submit(() -> traverseMemoryLimit(start, goal, byteLimit));
    }

    private PathSegment handleTraversal(
            Node start,
            Node goal,
            BiPredicate<QueryableCollection<PathSegment>, HashSet<Node>> limitFunction
    ) {
        if (strategy == null)
            throw new PathfindingException(start, goal, "Path strategy cannot be null. Use setStrategy()");

        var open = instantiateCollection();
        var closed = new HashSet<Node>();

        open.add(new PathSegment(start, null, 0, estimate(start, goal)));

        while (open.size() > 0) {
            var segment = open.remove();
            var node = segment.latest();

            // return complete or closest solution
            if (!limitFunction.test(open, closed) || node.matches(goal))
                return segment;

            closed.add(node);

            for (var adjacent : node.getAdjacent()) {
                var next = adjacent.node();
                if (!closed.contains(next))
                    open.add(new PathSegment(next, segment, cost(segment, adjacent), estimate(next, goal)));
            }
        }

        // no solution, so don't move
        return new PathSegment(start, null, 0, 0);
    }

    // utility method for easier consumption
    public static List<Node> toList(PathSegment endSegment) {
        var list = new ArrayList<Node>();

        do {
            list.add(0, endSegment.latest());
            endSegment = endSegment.previous();
        } while (endSegment != null);

        return list;
    }

    public static List<Node> skipSteps(PathSegment endSegment, int skipInterval) {
        return skipSteps(Pathfinder.toList(endSegment), skipInterval);
    }

    // simulate faster movement speed by jumping ahead a skipInterval amount of nodes each step
    // the start and goal nodes are guaranteed to remain on the list
    public static List<Node> skipSteps(List<Node> steps, int skipInterval) {
        if (skipInterval <= 0) return steps;

        return IntStream.range(0, steps.size())
                .filter(i -> i == 0 || i == steps.size() - 1 || i % (skipInterval + 1) == 0)
                .mapToObj(steps::get)
                .collect(Collectors.toList());
    }

    private int cost(PathSegment segment, Connection connection) {
        return strategy == PathStrategy.Optimal || strategy == PathStrategy.Uniform
                ? segment.cost() + connection.weight()
                : 0;
    }

    private int estimate(Node current, Node goal) {
        return strategy == PathStrategy.Optimal || strategy == PathStrategy.Greedy
                ? Math.abs(current.getX() - goal.getX()) + Math.abs(current.getY() - goal.getY())
                : 0;
    }

    private QueryableCollection<PathSegment> instantiateCollection() {
        return switch (strategy) {
            case Depth -> new QueryableStack<>();
            case Breadth -> new QueryableQueue<>();
            case Optimal, Uniform, Greedy -> new QueryableSortedList<>();
        };
    }

    // fast worst-case scenario estimate
    private static long estimateSizeOfSegment() {
        var highestOffset = Arrays.asList(PathSegment.class.getDeclaredFields())
                .stream()
                .map(unsafe::objectFieldOffset)
                .max(Comparator.comparingLong(value -> value))
                .get();

        // field is either a reference or an int, consider both to have a size of 8 bytes
        var finalOffset = highestOffset + 8;

        // pad to a multiple of 8
        return finalOffset % 8 == 0 ? finalOffset : new Double(Math.ceil(finalOffset / 8d) * 8).longValue();
    }

    private static Unsafe getUnsafe() {
        try {
            var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(Unsafe.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
           throw new PathfindingException("Failed to initialize Pathfinder (no access to Unsafe). "+e.getMessage());
        }
    }
}