package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 2.0;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 8;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 8;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!

    private double resizingLoadFactorThreshold;
    private int chainInitialCapacity;
    private int size;

    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.resizingLoadFactorThreshold = resizingLoadFactorThreshold;
        this.chainInitialCapacity = chainInitialCapacity;
        this.chains = this.createArrayOfChains(initialChainCount);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }


    private int getIndex(Object key) {
        if (key == null) {
            return 0;
        }
        int index = key.hashCode() % chains.length;
        if (index < 0) {
            return index + chains.length;
        } else {
            return index;
        }
    }
    @Override
    public V get(Object key) {
        int index = getIndex(key);
        if (chains[index] != null) {
            return chains[index].get(key);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        int index;
        if (key == null) {
            index = 0;
        } else {
            index = getIndex(key);
        }
        if (chains[index] == null) {
            chains[index] = createChain(chainInitialCapacity);
        }
        V prevValue = chains[index].put(key, value);
        if (prevValue == null) {
            size++;
        }
        if ((double) size / chains.length > resizingLoadFactorThreshold) {
            resize();
        }
        return prevValue;
    }

    private void resize() {
        AbstractIterableMap<K, V>[] prevChain = chains;
        int newCapacity = chains.length * 2;
        chains = createArrayOfChains(newCapacity);
        size = 0;
        for (AbstractIterableMap<K, V> chain : prevChain) {
            if (chain != null) {
                for (Map.Entry<K, V> entry : chain) {
                    put(entry.getKey(), entry.getValue());
                }
            }
        }
    }


    @Override
    public V remove(Object key) {
        int index = getIndex(key);
        if (chains[index] == null) {
            return null;
        }
        V prevV = chains[index].remove(key);
        if (prevV != null) {
            size--;
        }
        if (chains[index].size() == 0) {
            chains[index] = null;
        }
        return prevV;
    }

    @Override
    public void clear() {
        chains = createArrayOfChains(chains.length);
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return chains[0] != null && chains[0].containsKey(null);
        }
        int index = getIndex(key);
        return chains[index] != null && chains[index].containsKey(key);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        private int currentChainIndex;
        private Iterator<Map.Entry<K, V>> currentChainIterator;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            this.currentChainIndex = 0;
            this.currentChainIterator = getNextNonEmptyChainIterator();
        }

        private Iterator<Map.Entry<K, V>> getNextNonEmptyChainIterator() {
            while (currentChainIndex < chains.length && chains[currentChainIndex] == null) {
                currentChainIndex++;
            }
            if (currentChainIndex < chains.length) {
                return chains[currentChainIndex].iterator();
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return currentChainIterator != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (hasNext()) {
                Map.Entry<K, V> nextEntry = currentChainIterator.next();
                if (!currentChainIterator.hasNext()) {
                    currentChainIndex++;
                    currentChainIterator = getNextNonEmptyChainIterator();
                }
                return nextEntry;
            }
            throw new NoSuchElementException();
        }
    }
}
