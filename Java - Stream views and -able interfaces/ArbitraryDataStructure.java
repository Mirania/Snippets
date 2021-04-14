import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Some arbitrary data structure class.
 */
public class ArbitraryDataStructure<K, V> implements Iterable<V>,
                                                     Comparable<ArbitraryDataStructure<K, V>> {

    /**
     * The concrete data structure for these implementation examples.
     */
    private final Map<K, List<V>> structure;

    private int size;

    public ArbitraryDataStructure() {
        this.structure = new HashMap<>();
    }

    public void put(final K key, final V value) {
        this.structure.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        this.size++;
    }

    @SafeVarargs
    public final void put(final K key, final V... values) {
        for (var value : values) {
            this.put(key, value);
        }
    }

    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        return "Data[size=" + this.size + "]";
    }

    /**
     * Implementation of {@link Comparable}.
     */
    @Override
    public int compareTo(final ArbitraryDataStructure<K, V> other) {
        return this.size - other.size();
    }

    /**
     * Implementation of {@link Iterable}.
     */
    @Override
    public Iterator<V> iterator() {
        return new ArbitraryDataStructureIterator();
    }

    /**
     * Implementation of {@link Iterator}.
     */
    private class ArbitraryDataStructureIterator implements Iterator<V> {

        private final List<K> keys;
        private int keyIndex;
        private int valueIndex;

        public ArbitraryDataStructureIterator() {
            this.keys = new ArrayList<>(structure.keySet());
        }

        @Override
        public boolean hasNext() {
            return this.keyIndex < this.keys.size() && this.valueIndex < structure.get(this.keys.get(this.keyIndex)).size();
        }

        @Override
        public V next() {
            List<V> list;
            V value;

            try {
                list = structure.get(this.keys.get(this.keyIndex));
                value = list.get(this.valueIndex);
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }

            if (++this.valueIndex == list.size()) {
                this.keyIndex++;
                this.valueIndex = 0;
            }

            return value;
        }
    }

    /**
     * Sequential stream factory method.
     */
    public Stream<V> stream() {
        return StreamSupport.stream(new ArbitraryDataStructureSpliterator(), false);
    }

    /**
     * Parallel stream factory method.
     */
    public Stream<V> parallelStream() {
        return StreamSupport.stream(new ArbitraryDataStructureSpliterator(), true);
    }

    /**
     * Implementation of {@link Spliterator}.
     */
    private class ArbitraryDataStructureSpliterator implements Spliterator<V> {

        private final List<K> keys;
        private int startKeyIndex; // inclusive
        private int endKeyIndex; // exclusive
        private int valueIndex;

        public ArbitraryDataStructureSpliterator() {
            this.keys = new ArrayList<>(structure.keySet());
            this.endKeyIndex = this.keys.size();
        }

        private ArbitraryDataStructureSpliterator(final List<K> keys, final int startKeyIndex, final int endKeyIndex) {
            this.keys = keys;
            this.startKeyIndex = startKeyIndex;
            this.endKeyIndex = endKeyIndex;
        }

        // optional to override this, would default to relying on tryAdvance but it can be optimized
        @Override
        public void forEachRemaining(final Consumer<? super V> action) {
            for (; this.startKeyIndex < this.endKeyIndex; this.startKeyIndex++) {
                var list = structure.get(this.keys.get(this.startKeyIndex));
                for (; this.valueIndex < list.size(); this.valueIndex++) {
                    action.accept(list.get(this.valueIndex));
                }
                this.valueIndex = 0;
            }
        }

        @Override
        public boolean tryAdvance(final Consumer<? super V> action) {
            if (this.startKeyIndex == this.endKeyIndex) {
                return false;
            }

            var list = structure.get(this.keys.get(this.startKeyIndex));
            action.accept(list.get(this.valueIndex));

            if (++this.valueIndex == list.size()) {
                this.startKeyIndex++;
                this.valueIndex = 0;
            }

            return this.startKeyIndex < this.endKeyIndex;
        }

        // would be computationally expensive to guarantee a truly fair split
        // (it would require splitting by keys and values)
        // so we only split by keys here
        @Override
        public Spliterator<V> trySplit() {
            if (this.startKeyIndex == this.endKeyIndex) {
                return null;
            }

            var subStartKeyIndex = (this.endKeyIndex - this.startKeyIndex) / 2 + this.startKeyIndex;
            var subEndKeyIndex = this.endKeyIndex;
            this.endKeyIndex = subStartKeyIndex;

            return new ArbitraryDataStructureSpliterator(this.keys, subStartKeyIndex, subEndKeyIndex);
        }

        @Override
        public long estimateSize() {
            return this.endKeyIndex - this.startKeyIndex;
        }

        // not sized or subsized because it's computationally expensive to calculate
        // not ordered because we iterate a hashmap
        @Override
        public int characteristics() {
            return IMMUTABLE;
        }
    }
}
