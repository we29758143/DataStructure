package misc;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;


public class Sorter {
    public static <T extends Comparable<T>> IList<T> topKSort(int k, IList<T> input) {
        if (k < 0) {
            throw new IllegalArgumentException();
        }

        if (input == null) {
            throw new IllegalArgumentException();
        }

        IList<T> list = new DoubleLinkedList<>();

        int arraySize = 0;

        if (k < input.size()) {
            arraySize = k;
        } else {
            arraySize = input.size();
        }

        if (arraySize == 0) {
            return list;
        }

        IPriorityQueue<T> arrayHeap = new ArrayHeap<>();
        int count = 0;

        for (T element : input) {
            count++;
            if (count <= arraySize) {
                arrayHeap.add(element);

            } else {
                if (element.compareTo(arrayHeap.peekMin()) > 0) {
                    arrayHeap.removeMin();
                    arrayHeap.add(element);
                }
            }
        }

        for (int i = 0; i < arraySize; i++) {
            list.add(arrayHeap.removeMin());
        }

        return list;
    }
}
