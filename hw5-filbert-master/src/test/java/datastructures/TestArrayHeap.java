package datastructures;

import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import misc.BaseTest;
import misc.exceptions.EmptyContainerException;
import misc.exceptions.InvalidElementException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;


/**
 * See spec for details on what kinds of tests this class should include.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestArrayHeap extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(3);
        assertEquals(1, heap.size());
        assertFalse(heap.isEmpty());
    }

    @Test(timeout=SECOND)
    public void testBasicAddReflection() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(3);
        Comparable<Integer>[] array = getArray(heap);
        assertEquals(3, array[0]);
    }

    @Test(timeout=SECOND)
    public void testUpdateDecrease() {
        IntWrapper[] values = IntWrapper.createArray(new int[]{1, 2, 3, 4, 5});
        IPriorityQueue<IntWrapper> heap = this.makeInstance();

        for (IntWrapper value : values) {
            heap.add(value);
        }

        IntWrapper newValue = new IntWrapper(0);
        heap.replace(values[2], newValue);

        assertEquals(newValue, heap.removeMin());
        assertEquals(values[0], heap.removeMin());
        assertEquals(values[1], heap.removeMin());
        assertEquals(values[3], heap.removeMin());
        assertEquals(values[4], heap.removeMin());
    }

    @Test(timeout=SECOND)
    public void testUpdateIncrease() {
        IntWrapper[] values = IntWrapper.createArray(new int[]{0, 2, 4, 6, 8});
        IPriorityQueue<IntWrapper> heap = this.makeInstance();

        for (IntWrapper value : values) {
            heap.add(value);
        }

        IntWrapper newValue = new IntWrapper(5);
        heap.replace(values[0], newValue);

        assertEquals(values[1], heap.removeMin());
        assertEquals(values[2], heap.removeMin());
        assertEquals(newValue, heap.removeMin());
        assertEquals(values[3], heap.removeMin());
        assertEquals(values[4], heap.removeMin());
    }

    protected IPriorityQueue<Integer> makeBasicHeap() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(1);
        heap.add(3);
        heap.add(2);
        heap.add(4);
        return heap;
    }


    protected IPriorityQueue<Integer> makeNegHeap() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(-1);
        heap.add(3);
        heap.add(0);
        return heap;
    }

    @Test(timeout=SECOND)
    public void testInsertBasic(){
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(3);
        assertEquals(1, heap.size());
        try {
            heap.add(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Do nothing: this is ok
        }
    }


    @Test(timeout=SECOND)
    public void testPeekOneElement() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(1);
        assertEquals(1, heap.peekMin());
        assertEquals(1, heap.size());
    }

    @Test(timeout=SECOND)
    public void testRemoveOneElement() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(1);
        assertEquals(1, heap.size());
        assertEquals(1, heap.removeMin());
        assertEquals(0, heap.size());
    }

    @Test(timeout=SECOND)
    public void testRemoveMultipleElement() {
        IPriorityQueue<Integer> heap = this.makeBasicHeap();
        assertEquals(1, heap.removeMin());
        assertEquals(3, heap.size());
        assertEquals(2, heap.removeMin());
        assertEquals(2, heap.size());
        assertEquals(3, heap.removeMin());
        assertEquals(1, heap.size());
        assertEquals(4, heap.removeMin());
        assertEquals(0, heap.size());
    }

    @Test(timeout=SECOND)
    public void testRemoveWhenHeapIsNull() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        assertEquals(0, heap.size());
        assertTrue(heap.isEmpty());
        try {
            heap.removeMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // Do nothing: this is ok
        }
    }

    @Test(timeout=SECOND)
    public void testRemoveMin() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(1);
        heap.add(2);
        heap.add(3);
        heap.add(4);

        assertEquals(1, heap.removeMin());
        assertEquals(3, heap.size());
        assertEquals(2, heap.removeMin());
        assertEquals(2, heap.size());
        assertEquals(3, heap.removeMin());
        assertEquals(1, heap.size());
        assertEquals(4, heap.removeMin());
        assertEquals(0, heap.size());
    }


    @Test(timeout=SECOND)
    public void testRemoveMultipleElementWithNeg() {
        IPriorityQueue<Integer> heap = this.makeNegHeap();
        assertEquals(-1, heap.removeMin());
        assertEquals(2, heap.size());
        assertEquals(0, heap.removeMin());
        assertEquals(1, heap.size());
        assertEquals(3, heap.removeMin());
        assertEquals(0, heap.size());
    }

    @Test(timeout=SECOND)
    public void testPeekBasic() {
        IPriorityQueue<Integer> heap = this.makeBasicHeap();
        assertEquals(1, heap.peekMin());
        assertEquals(4, heap.size());
    }

    @Test(timeout=SECOND)
    public void testAddSameItem() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(1);
        assertEquals(1, heap.size());
        try {
            heap.add(1);
            fail("Expected InvalidElementException");
        } catch (InvalidElementException ex) {
            // Do nothing: this is ok

        }
    }

    @Test(timeout=SECOND)
    public void testPeekWithNeg() {
        IPriorityQueue<Integer> heap = this.makeNegHeap();
        assertEquals(-1, heap.peekMin());
        assertEquals(3, heap.size());
    }

    @Test(timeout=SECOND)
    public void testPeekWhenHeapIsNull() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        assertEquals(0, heap.size());
        assertTrue(heap.isEmpty());
        try {
            heap.peekMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // Do nothing: this is ok
        }
    }

    @Test(timeout=SECOND)
    public void testReplaceInvalid() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.add(1);
        assertEquals(1, heap.size());
        try {
            heap.replace(2, 3);
            fail("Expected InvalidElementException");
        } catch (InvalidElementException ex) {
            // Do nothing: this is ok
        }
    }



    @Test(timeout=SECOND)
    public void testIsEmpty() {
        IPriorityQueue<Integer> heap = this.makeBasicHeap();
        for (int i = 0; i < 4; i++) {
            heap.removeMin();
        }
        assertTrue(heap.isEmpty());
    }

    @Test(timeout=SECOND)
    public void testAddAndRemoveMany() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 100000; i > 0; i--) {
            heap.add(i);
            assertEquals(100001 - i, heap.size());
        }
        for (int i = 1; i <= 100000; i++) {
            assertEquals(i, heap.removeMin());
        }
    }


    @Test(timeout=SECOND)
    public void testRemove() {

        IPriorityQueue<Integer> heap = this.makeInstance();

        heap.add(0);
        heap.add(1);
        heap.add(6);
        heap.add(7);
        heap.add(8);
        heap.add(2);
        heap.add(3);
        heap.add(4);

        heap.remove(1);


        //result: {0, 2, 6, 7, 8, 4, 3}
        IPriorityQueue<Integer> results = this.makeInstance();

        results.add(0);
        results.add(2);
        results.add(6);
        results.add(7);
        results.add(8);
        results.add(4);
        results.add(3);

        assertEquals(results.removeMin(), heap.removeMin());
        assertEquals(results.removeMin(), heap.removeMin());
        assertEquals(results.removeMin(), heap.removeMin());
        assertEquals(results.removeMin(), heap.removeMin());
        assertEquals(results.removeMin(), heap.removeMin());
        assertEquals(results.removeMin(), heap.removeMin());
        assertEquals(results.removeMin(), heap.removeMin());


    }




    /**
     * A comparable wrapper class for ints. Uses reference equality so that two different IntWrappers
     * with the same value are not necessarily equal--this means that you may have multiple different
     * IntWrappers with the same value in a heap.
     */
    public static class IntWrapper implements Comparable<IntWrapper> {
        private final int val;

        public IntWrapper(int value) {
            this.val = value;
        }

        public static IntWrapper[] createArray(int[] values) {
            IntWrapper[] output = new IntWrapper[values.length];
            for (int i = 0; i < values.length; i++) {
                output[i] = new IntWrapper(values[i]);
            }
            return output;
        }

        @Override
        public int compareTo(IntWrapper o) {
            return Integer.compare(val, o.val);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return this.val;
        }

        @Override
        public String toString() {
            return Integer.toString(this.val);
        }
    }

    /**
     * A helper method for accessing the private array inside a heap using reflection.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> Comparable<T>[] getArray(IPriorityQueue<T> heap) {
        return getField(heap, "heap", Comparable[].class);
    }

}
