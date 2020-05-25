package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods:
 * @see datastructures.interfaces.IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        if (this.size == 0) {
            this.front = new Node<>(item);
            this.back = this.front;
        } else {
            Node<T> current = this.back;
            current.next = new Node<>(current, item);
            this.back = this.back.next;
        }
        this.size++;
    }

    @Override
    public T remove() {
        if (this.size == 0) {
            throw new EmptyContainerException();
        }
        T data = this.back.data;
        if (this.size == 1) {
            this.back = null;
            this.front = null;
        } else {
            this.back = this.back.prev;
            this.back.next = null;
        }
        this.size--;
        return data;
    }

    @Override
    public T get(int index) {
        checkIndex(index);
        Node<T> current = this.front;
        if (index < this.size / 2) {
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = this.back;
            for (int i = this.size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current.data;
    }

    @Override
    public void set(int index, T item) {
        checkIndex(index);
        Node<T> current = this.front;
        Node<T> end = this.back;

        if (index == 0) {
            if (this.size == 1) {
                this.front = new Node<>(item);
                this.back = this.front;
            } else {
                this.front = new Node<>(item, current.next);
                current.next.prev = this.front;
                current.next = null;
            }
        } else if (index == this.size - 1) {
            this.back = new Node<>(end.prev, item);
            end.prev.next = this.back;
            end.prev = null;
        } else {
            if (index - 1 < this.size / 2) {
                for (int i = 0; i < index - 1; i++) {
                    current = current.next;
                }
                end = current.next;
            } else {
                for (int i = this.size - 1; i > index; i--) {
                    end = end.prev;
                }
                current = end.prev;
            }
            current.next = new Node<>(current, item, end.next);
            end.next.prev = current.next;
            end.prev = null;
            end.next = null;
        }
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> current = this.front;
        Node<T> end = this.back;

        if (this.size == 0) {
            this.front = new Node<>(item);
            this.back = this.front;
        } else if (index == 0) {
            this.front = new Node<>(item, current);
            current.prev = this.front;
        } else if (index == this.size) {
            this.back = new Node<>(end, item);
            end.next = this.back;
        } else {
            if (index < this.size / 2) {
                for (int i = 0; i < index - 1; i++) {
                    current = current.next;
                }
                end = current.next;
            } else {
                for (int i = this.size - 1; i > index; i--) {
                    end = end.prev;
                }
                current = end.prev;
            }
            current.next = new Node<>(current, item, end);
            end.prev = current.next;
        }
        this.size++;
    }

    @Override
    public T delete(int index) {
        checkIndex(index);

        Node<T> current = this.front;
        Node<T> end = this.back;
        T data = this.front.data;

        if (this.size == 1) {
            this.front = null;
            this.back = null;

        } else if (index == 0) {
            this.front = this.front.next;
            this.front.prev = null;

        } else if (index == this.size - 1) {
            return this.remove();

        } else {

            if (index < this.size / 2) {
                for (int i = 0; i < index - 1; i++) {
                    current = current.next;
                }
                end = current.next;

            } else {
                for (int i = 0; i < (this.size() - 1 - index); i++) {
                    end = end.prev;
                }
                current = end.prev;
            }

            current.next = end.next;
            end.next.prev = current;
            data = end.data;
            end.prev = null;
            end.next = null;
        }
        this.size--;
        return data;
    }

    @Override
    public int indexOf(T item) {
        if (this.size == 0) {
            return -1;
        }
        Node<T> current = this.front;
        int index = 0;
        while (current != null) {
            if (current.data == null) {
                if (item == null) {
                    return index;
                }
            } else if (current.data.equals(item)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T other) {
        if (this.size == 0) {
            return false;
        }
        Node<T> current = this.front;
        while (current != null) {
            if (current.data == null) {
                if (other == null) {
                    return true;
                }
            } else if (current.data.equals(other)) {
                return true;
            }
            current = current.next;

        }
        return false;
    }

    // private helper function to check if the List has the legal index
    private void checkIndex(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
        public Node(Node<E> prev, E data) {
            this(prev, data, null);
        }

        public Node(E data, Node<E> next) {
            this(null, data, next);
        }
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *                                there are no more elements to look at.
         */
        public T next() {
            if (hasNext()) {
                T item = current.data;
                current = current.next;
                return item;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
