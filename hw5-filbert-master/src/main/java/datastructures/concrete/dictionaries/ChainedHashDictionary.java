package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.Iterator;
import java.util.NoSuchElementException;



/**
 * @see IDictionary and the assignment page for more details on what each method should do
 */


public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    private final double lambda;
    // You MUST use this field to store the contents of your dictionary.
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.

    private IDictionary<K, V>[] chains;
    // You're encouraged to add extra fields (and helper methods) though!
    private int pairs;
    private int capacity;
    private int position;


    public ChainedHashDictionary() {
        this.lambda = 1.0;
        this.pairs = 0;
        this.capacity = 10;
        this.chains = makeArrayOfChains(capacity);
    }
    public ChainedHashDictionary(double lambda) {
        this.lambda = lambda;
        this.pairs = 0;
        this.capacity = 10;
        this.chains = makeArrayOfChains(capacity);

    }
    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int arraySize) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[arraySize];
    }
    @Override
    public V get(K key) {
        if (containsKey(key)){
            return this.chains[keyHash(key)].get(key);
        }else {
            throw new NoSuchKeyException();
        }
    }
    @Override
    public void put(K key, V value) {
        if (!this.containsKey(key)){
            if (((double) ((this.pairs + 1) / this.capacity)) >= lambda){
                capacity *= 2;
                IDictionary<K, V>[] other = makeArrayOfChains(this.capacity);

                for (int i = 0; i < capacity / 2; i++){
                    if (!(this.chains[i] == null)){
                        for (KVPair<K, V> pair : this.chains[i]){
                            if (other[keyHash(pair.getKey())] == null){
                                other[keyHash(pair.getKey())] = new ArrayDictionary<>();
                            }
                            other[keyHash(pair.getKey())].put(pair.getKey(), pair.getValue());
                        }
                    }
                }

                this.chains = other;
            }
            this.pairs++;
            if (this.chains[keyHash(key)] == null){
                this.chains[keyHash(key)] = new ArrayDictionary<>();
            }
        }

        this.chains[keyHash(key)].put(key, value);
    }
    @Override
    public V remove(K key) {
        if (containsKey(key)){
            this.pairs--;
            return this.chains[keyHash(key)].remove(key);
        }else {
            throw new NoSuchKeyException();
        }
    }
    @Override
    public boolean containsKey(K key) {
        if (this.chains[keyHash(key)] != null){

            if (this.chains[keyHash(key)].containsKey(key)){
                return true;
            }
        }
        return false;
    }
    @Override
    public int size() {
        return this.pairs;
    }

    public V get1(K key) {
        if (this.chains[keyHash(key)] != null){
            for (KVPair<K, V> pair : this.chains[keyHash(key)]){
                if (this.chains[keyHash(key)].containsKey(key)){
                    return pair.getValue();
                }
            }
        }
        return null;
    }


    public V getOrDefault(K key, V defaultValue) {
        V value = get1(key);
        return value == null ? defaultValue : value;
    }


    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    private int keyHash(K key){
        if (key == null){
            return 0;
        }else {
            if (key.hashCode() < 0){
                return (-1) * key.hashCode() % this.capacity;
            }else {
                return key.hashCode() % this.capacity;
            }
        }
    }
    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be
     *    true once the constructor is done setting up the class AND
     *    must *always* be true both before and after you call any
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {

        private IDictionary<K, V>[] chains;
        private Iterator<KVPair<K, V>> iter;
        private int level;


        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.level = 0;

            if (this.chains == null){
                this.iter = null;
            }else {
                for (int i = 0; i < this.chains.length; i++){
                    if (!(this.chains[i] == null)){
                        this.level = i;
                        this.iter = this.chains[i].iterator();
                        break;
                    }

                    //what is this for?
                    // if (i == this.chains.length - 1){
                    //     this.iter = null;
                    // }
                }
            }

        }

        @Override
        public boolean hasNext() {
            if (this.iter == null){
                return false;
            }

            if (this.iter.hasNext()){
                return true;
            }else {
                this.level++;
                for (int i = this.level; i < this.chains.length; i++){
                    if (!(this.chains[i] == null)){
                        if (this.chains[i].iterator().hasNext()){
                            this.level = i;
                            this.iter = this.chains[i].iterator();
                            return true;
                        }
                    }

                    if (i == this.chains.length - 1){
                        this.iter = null;
                    }
                }
            }

            return false;

        }

        @Override
        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            return iter.next();
        }
    }

    public static void main(String[] args) {
        IDictionary dict = new ChainedHashDictionary();
        dict.put(11, 222);
        dict.put(33, 55555);

        IDictionary ad = new ArrayDictionary();
        ad.put(11, 222);
        ad.put(22, 333);

        System.out.println(dict.getOrDefault("", "sdfgh"));
        System.out.println(ad.getOrDefault(131, "dddddd"));
    }

}