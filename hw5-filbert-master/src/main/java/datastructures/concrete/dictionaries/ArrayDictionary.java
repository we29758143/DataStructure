package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * @see datastructures.interfaces.IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field.
    // We will be inspecting it in our private tests.
    private Pair<K, V>[] pairs;

    // You may add extra fields or helper methods though!
    private int elementNumber;
    private int size;
    private int position;



    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(1);
        this.elementNumber = 0;
        this.size = 1;
        this.position = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     * <p>
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        } else {
            return this.pairs[this.position].value;
        }
    }

    @Override
    public void put(K key, V value) {
        Pair<K, V> newPair = new Pair<>(key, value);
        if (this.containsKey(key)) {
            this.pairs[this.position] = newPair;
        } else {
            if (this.elementNumber == this.size) {
                this.size *= 2;
                Pair<K, V>[] other = makeArrayOfPairs(this.size);
                for (int i = 0; i < this.elementNumber; i++) {
                    other[i] = pairs[i];
                }
                this.pairs = other;
            }

            this.pairs[this.elementNumber] = newPair;
            elementNumber++;
        }
    }

    public V get1(K key) {
        if (!this.containsKey(key)) {
            return null;
        } else {
            return this.pairs[this.position].value;
        }
    }


    public V getOrDefault(K key, V defaultValue) {
        V value = get1(key);
        return value == null ? defaultValue : value;
    }

    // @Override
    // public V getOrDefault(K key, V defaultValue) {
    //     for (int i = 0; i < elementNumber; i++) {
    //         if (this.pairs[i].key == null) {
    //             if (key == null) {
    //                 this.position = i;
    //                 return defaultValue;
    //             }
    //         } else if (this.pairs[i].key.equals(key)) {
    //             this.position = i;
    //             return pairs[position].value;
    //         }
    //     }
    //     return defaultValue;
    // }



    @Override
    public V remove(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        } else {
            Pair<K, V> temp = this.pairs[this.position];
            if (this.position == this.elementNumber - 1) {
                this.pairs[this.position] = null;
            } else {
                this.pairs[this.position] = this.pairs[this.elementNumber - 1];
                this.pairs[this.elementNumber - 1] = null;
            }
            elementNumber--;
            return temp.value;
        }
    }

    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < elementNumber; i++) {
            if (this.pairs[i].key == null) {
                if (key == null) {
                    this.position = i;
                    return true;
                }
            } else if (this.pairs[i].key.equals(key)) {
                this.position = i;
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.elementNumber;
    }

    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<>(this.pairs);

    }


    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + " = " + this.value;
        }
    }

    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {

        private Pair<K, V>[] pairs;
        private int count;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs){
            this.pairs = pairs;
            this.count = 0;
        }

        @Override
        public boolean hasNext() {
            if (count < this.pairs.length){
                return this.pairs[count] != null;
            }else {
                return false;
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (hasNext()){
                this.count++;
                return new KVPair(pairs[this.count - 1].key, pairs[this.count - 1].value);
            }else {
                throw new NoSuchElementException();
            }
        }
    }


    public static void main(String[] args) {
        IDictionary dict = new ArrayDictionary();
        dict.put(11, 222);
        dict.put(33, 55555);

        System.out.println(dict.getOrDefault(11, "sdfgh"));

    }

}
