package datastructures.concrete;


import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

import misc.exceptions.InvalidElementException;

import java.util.Iterator;


/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;
    private static final int INITIAL_CAP = 1;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int length;
    private int capacity;
    private IDictionary<T, Integer> indexdict;

    // Feel free to add more fields and constants.

    public ArrayHeap() {
        this.capacity = INITIAL_CAP;
        this.heap = makeArrayOfT(capacity);
        this.length = 0;
        this.indexdict = new ChainedHashDictionary<>();
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    /**
     * A method stub that you may replace with a helper method for percolating
     * upwards from a given index, if necessary.
     */
    private void percolateUp(int index) {
        int parent = (index - 1) / NUM_CHILDREN;
        if (this.heap[index].compareTo(this.heap[parent]) < 0){
            swap(index, parent);
            // T temp = this.heap[index];
            // this.heap[index] = this.heap[parent];
            // this.heap[parent] = temp;
            percolateUp(parent); //check later
        }
    }

    /**
     * A method stub that you may replace with a helper method for percolating
     * downwards from a given index, if necessary.
     */
    private void percolateDown(int index) {
        if (capacity > (NUM_CHILDREN * index + 1)) {
            if (this.heap[NUM_CHILDREN * index + 1] != null) {
                int minIndex = findMin(index);
                if (this.heap[index].compareTo(this.heap[minIndex]) > 0) {   //child is smaller than parent
                    swap(index, minIndex);
                    // T temp = this.heap[index];
                    // this.heap[index] = this.heap[minIndex];
                    // this.heap[minIndex] = temp;
                    percolateDown(minIndex);
                }
            }
        }
    }

    /**
     * A method stub that you may replace with a helper method for determining
     * which direction an index needs to percolate and percolating accordingly.
     */
    private void percolate(int index) {
        int parent = (index - 1) / NUM_CHILDREN;
        if (index == 0){
            percolateDown(index);
        }else {
            if (this.heap[parent].compareTo(this.heap[index]) > 0){
                percolateUp(index);
            }else if (this.heap[index].compareTo(this.heap[parent]) > 0){
                percolateDown(index);
            }
        }
    }

    private int findMin(int index){
        if (this.heap[NUM_CHILDREN * index + 2] == null){
            return NUM_CHILDREN * index + 1;
        }else {
            int i = 1;
            T minValue = this.heap[NUM_CHILDREN * index + 1];
            int minIndex = NUM_CHILDREN * index + 1;
            while (this.heap[NUM_CHILDREN * index + i] != null && i < NUM_CHILDREN + 1) {
                //right node is smaller than current node
                if (minValue.compareTo(this.heap[NUM_CHILDREN * index + i]) > 0) {
                    minValue = this.heap[NUM_CHILDREN * index + i];
                    minIndex = NUM_CHILDREN * index + i;
                }
                i++;
            }

            return minIndex;
        }
    }

    /**
     * A method stub that you may replace with a helper method for swapping
     * the elements at two indices in the 'heap' array.
     */

    //don't necessary need this method
    private void swap(int a, int b) {
        T temp = this.heap[a];
        this.heap[a] = this.heap[b];
        this.heap[b] = temp;

        this.indexdict.put(this.heap[b], b);
        this.indexdict.put(this.heap[a], a);
    }

    @Override
    public T removeMin() {
        if (this.length == 0){
            throw new EmptyContainerException();
        }
        T temp = heap[0];
        swap(0, length - 1);
        indexdict.remove(heap[length - 1]);
        heap[length - 1] = null;
        if (length > 0){
            percolateDown(0);
        }
        length--;
        return temp;
    }

    @Override
    public T peekMin() {
        if (this.length == 0){
            throw new EmptyContainerException();
        }
        return heap[0];
    }

    @Override
    public void add(T item) {
        if (item == null){
            throw new IllegalArgumentException();
        }

        if (contains(item)){
            throw new InvalidElementException();
        }
        checkResize();

        if (length == 0){
            this.heap[0] = item;
            this.indexdict.put(item, 0);
        }else {
            this.heap[length] = item;
            this.indexdict.put(item, length);
        }
        percolateUp(this.length);
        length++;
    }

    @Override
    public boolean contains(T item) {
        // for (int i = 0; i < this.length; i++){
        //     if (item == null){
        //         throw new IllegalArgumentException();
        //     }else if (this.heap[i].compareTo(item) == 0){
        //         this.position = i;
        //         return true;
        //     }
        // }
        // return false;
        if (indexdict.containsKey(item)){
            return true;
        }else if (item == null){
            throw new IllegalArgumentException();
        }else {
            return false;
        }
    }

    @Override
    public void remove(T item) {
        if (!contains(item)){
            throw new InvalidElementException();
        }else if (item == null){
            throw new IllegalArgumentException();
        }else if (indexdict.get(item) == this.length - 1){
            this.heap[length - 1] = null;
            this.indexdict.remove(item);
            length--;
        }else {
            if (contains(item)){
                int index = this.indexdict.remove(item);
                swap(index, this.length - 1);
                this.indexdict.remove(this.heap[length - 1]);
                this.heap[this.length - 1] = null;
                percolate(index);
            }
            length--;
        }
    }

    @Override
    public void replace(T oldItem, T newItem) {
        if (!contains(oldItem) || contains(newItem)){
            throw new InvalidElementException();
        }
        if (contains(oldItem)){
            int index = indexdict.remove(oldItem);
            this.heap[index] = newItem;
            indexdict.put(newItem, index);
            percolate(index);
        }
    }

    @Override
    public int size() {
        return this.length;
    }

    private void print(){
        for (int i = 0; i < this.length; i++){
            System.out.println("this is index: " + i + " = " + this.heap[i]);

        }
    }

    private void printIndex(){
        Iterator it = indexdict.iterator();
        while (it.hasNext()){
            System.out.println(it.next());
        }
    }

    private void checkResize(){
        if (length == capacity - 1){
            capacity *=2;
            T[] temp = this.heap;
            this.heap = makeArrayOfT(capacity);
            for (int i = 0; i < length; i++){
                heap[i] = temp[i];
            }
        }
    }

}
