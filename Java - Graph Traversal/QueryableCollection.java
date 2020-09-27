package pathfinder;

import java.util.ArrayDeque;
import java.util.PriorityQueue;

public interface QueryableCollection<T> {
    void add(T element);
    T remove();
    boolean contains(T element);
    int size();

    class QueryableStack<T> implements QueryableCollection<T> {
        private ArrayDeque<T> stack;

        public QueryableStack() {
            stack = new ArrayDeque<>();
        }

        public void add(T element) {
            stack.offerFirst(element);
        }

        public T remove() {
            return stack.removeFirst();
        }

        public boolean contains(T element) {
            return stack.contains(element);
        }

        public int size() {
            return stack.size();
        }
    }

    class QueryableQueue<T> implements QueryableCollection<T> {
        private ArrayDeque<T> list;

        public QueryableQueue() {
            list = new ArrayDeque<>();
        }

        public void add(T element) {
            list.offerLast(element);
        }

        public T remove() {
            return list.removeLast();
        }

        public boolean contains(T element) {
            return list.contains(element);
        }

        public int size() {
            return list.size();
        }
    }

    class QueryableSortedList<T> implements QueryableCollection<T> {
        private PriorityQueue<T> queue;

        public QueryableSortedList() {
            queue = new PriorityQueue<>();
        }

        public void add(T element) {
            queue.add(element);
        }

        public T remove() {
            return queue.poll();
        }

        public boolean contains(T element) {
            return queue.contains(element);
        }

        public int size() {
            return queue.size();
        }
    }
}
