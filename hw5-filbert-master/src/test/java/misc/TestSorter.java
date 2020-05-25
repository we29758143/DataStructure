package misc;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * See spec for details on what kinds of tests this class should include.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSorter extends BaseTest {
    @Test(timeout=SECOND)
    public void testSimpleUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Sorter.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }

    }

    @Test(timeout=SECOND)
    public void testLargeK() {
        List<Integer> test = new ArrayList<Integer>();
        List<Integer> top = new ArrayList<Integer>();
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 19; i >= 0; i--) {
            list.add(i);
            test.add(i);
        }
        IList<Integer> largerK = Sorter.topKSort(30, list);
        for (int element : largerK) {
            top.add(element);
        }
        Collections.sort(test);
        assertEquals(test, top);
    }

    @Test(timeout=SECOND)
    public void testSmallAndEqualK() {
        List<Integer> test = new ArrayList<Integer>();
        List<Integer> top1 = new ArrayList<Integer>();
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 19; i >= 0; i--) {
            list.add(i);
            test.add(i);
        }
        Collections.sort(test);

        IList<Integer> normal = Sorter.topKSort(20, list);

        for (int element : normal) {
            top1.add(element);
        }
        assertEquals(test, top1);

        List<Integer> top2 = new ArrayList<Integer>();

        IList<Integer> less = Sorter.topKSort(10, list);

        for (int element : less) {
            top2.add(element);
        }
        assertEquals(test.subList(10, 20), top2);
    }

    @Test(timeout=SECOND)
    public void testNegAndZeroK() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 19; i >= 0; i--) {
            list.add(i);
        }
        IList<Integer> zeroK = Sorter.topKSort(0, list);
        assertTrue(zeroK.isEmpty());

        try {
            IList<Integer> negative = Sorter.topKSort(-10, list);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Do nothing: this is ok
        }
    }

    @Test(timeout=SECOND)
    public void testEmptyList() {
        IList<Integer> list = new DoubleLinkedList<>();
        IList<Integer> emptyList = Sorter.topKSort(10, list);
        assertTrue(emptyList.isEmpty());
    }

    @Test(timeout=SECOND)
    public void testSortBasic() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(-3);
        list.add(5);
        list.add(1);
        list.add(-100);
        list.add(50);
        list.add(0);
        IList<Integer> basicList = Sorter.topKSort(2, list);




        assertEquals(5, basicList.get(0));
        assertEquals(50, basicList.get(1));

    }
}
