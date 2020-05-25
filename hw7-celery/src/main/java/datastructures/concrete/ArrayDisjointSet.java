package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;

    // However, feel free to add more fields and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.
    private IDictionary<T, Integer> indexDictionary;
    private int value;

    public ArrayDisjointSet() {
        this.indexDictionary = new ChainedHashDictionary<>();
        this.value = 0;
        this.pointers = new int[value];
    }

    @Override
    public void makeSet(T item) {
        if (this.indexDictionary.containsKey(item)) {
            throw new IllegalArgumentException();
        }

        this.indexDictionary.put(item, this.value);

        int[] temp = new int[this.value + 1];

        for (int i = 0; i < this.value; i++) {
            temp[i] = pointers[i];
        }

        temp[this.value] = -1;
        this.pointers = temp;
        this.value++;
    }

    @Override
    public int findSet(T item) {
        if (!this.indexDictionary.containsKey(item)) {
            throw new IllegalArgumentException();
        }

        int root = this.indexDictionary.get(item);

        while (this.pointers[root] > 0) {
            root = pointers[root];
        }

        int node = this.indexDictionary.get(item);

        while (pointers[node] > 0) {
            pointers[node] = root;
            node = pointers[node];
        }

        return root;
    }

    @Override
    public void union(T item1, T item2) {
        if (!this.indexDictionary.containsKey(item1) || !this.indexDictionary.containsKey(item2)) {
            throw new IllegalArgumentException();
        }

        if (this.pointers[findSet(item1)] >= pointers[findSet(item2)]) {
            pointers[findSet(item1)] = findSet(item2);
            pointers[findSet(item2)]--;
        } else {
            pointers[findSet(item2)] = findSet(item1);
            pointers[findSet(item1)]--;
        }
    }
}
