package pathfinder;

import java.util.concurrent.ExecutionException;

import static pathfinder.Pathfinder.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var n1 = new Node("n1", 8, 18);
        var n2 = new Node("n2", 4,15);
        var n3 = new Node("n3", 3,11);
        var n4 = new Node("n4", 6,7);
        var n5 = new Node("n5", 5,3);
        var n6 = new Node("n6", 8,0);
        var n7 = new Node("n7", 11,4);
        var n8 = new Node("n8", 9,11);
        var n9 = new Node("n9", 14,8);
        var n10 = new Node("n10", 12,15);

        n1.addAdjacent(n2, 7);
        n2.addAdjacent(n3, 5);
        n3.addAdjacent(n4, 7);
        n4.addAdjacent(n5, 9);
        n4.addAdjacent(n7, 8);
        n5.addAdjacent(n6, 6);
        n6.addAdjacent(n7, 9);
        n7.addAdjacent(n8, 10);
        n7.addAdjacent(n9, 7);
        n8.addAdjacent(n10, 7);
        n9.addAdjacent(n10, 9);
        n10.addAdjacent(n1, 8);

        var p = Pathfinder.getInstance();
        p.setStrategy(PathStrategy.Optimal);

        System.out.println(Pathfinder.toList(p.traverse(n1, n6)));
        System.out.println(Pathfinder.toList(p.traverseTimeLimit(n1, n6, 2)));
        System.out.println(Pathfinder.toList(p.traverseNodeLimit(n1, n6, 5)));
        System.out.println(Pathfinder.toList(p.traverseMemoryLimit(n1, n6, 240)));

        var a1 = p.asyncTraverse(n6, n1);
        var a2 = p.asyncTraverseTimeLimit(n6, n1, 2);
        var a3 = p.asyncTraverseNodeLimit(n6, n1, 5);
        var a4 = p.asyncTraverseMemoryLimit(n6, n1, 240);

        var r = a1.get();

        System.out.println(Pathfinder.toList(r));
        System.out.println(Pathfinder.toList(a2.get()));
        System.out.println(Pathfinder.toList(a3.get()));
        System.out.println(Pathfinder.toList(a4.get()));

        System.out.println(Pathfinder.skipSteps(r, 1));
        System.out.println(Pathfinder.skipSteps(Pathfinder.toList(r), 2));
    }
}
