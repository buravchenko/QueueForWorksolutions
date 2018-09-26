package ru.worksolutions.util;

import java.util.*;
import java.util.function.IntUnaryOperator;

@SuppressWarnings({"unchecked", "WeakerAccess"})
public class MyQueue<E> implements Queue<E> {

    private static final int STARTING_CAPACITY_BY_DEFAULT = 100;
    private static final IntUnaryOperator CALC_NEW_CAPACITY_BY_DEFAULT = x -> x * 2;

    Object[] buffer;
    int count;
    int indexToPut;
    int indexToGet;
    IntUnaryOperator calcNewCapacity;

    public MyQueue() {
        this(STARTING_CAPACITY_BY_DEFAULT, CALC_NEW_CAPACITY_BY_DEFAULT);
    }

    public MyQueue(int startingCapacity) {
        this(startingCapacity, CALC_NEW_CAPACITY_BY_DEFAULT);
    }

    public MyQueue(int startingCapacity, IntUnaryOperator calcNewCapacity) {
        if (startingCapacity <= 0)
            throw new IllegalArgumentException();
        this.calcNewCapacity = calcNewCapacity;
        buffer = new Object[startingCapacity];
        count = 0;
        indexToPut = 0;
        indexToGet = 0;
    }

    /**
     * Returns the number of elements in this collection.  If this collection
     * contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this collection
     */
    public int size() {
        return count;
    }

    /**
     * Returns {@code true} if this collection contains no elements.
     *
     * @return {@code true} if this collection contains no elements
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Returns {@code true} if this collection contains the specified element.
     * More formally, returns {@code true} if and only if this collection
     * contains at least one element {@code e} such that
     * {@code Objects.equals(o, e)}.
     *
     * @param o element whose presence in this collection is to be tested
     * @return {@code true} if this collection contains the specified
     * element
     * @throws ClassCastException   if the type of the specified element
     *                              is incompatible with this collection
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *                              collection does not permit null elements
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    public boolean contains(Object o) {
        for (int i = 0; i < count; i++) {
            int index = (indexToGet + i) % buffer.length;
            if (buffer[index] == null) {
                if (o == null) return true;
            } else if (buffer[index].equals(o))
                return true;
        }
        return false;
    }

    /**
     * Returns an iterator over the elements in this collection.  There are no
     * guarantees concerning the order in which the elements are returned
     * (unless this collection is an instance of some class that provides a
     * guarantee).
     *
     * @return an {@code Iterator} over the elements in this collection
     */
    public Iterator<E> iterator() {
        return new MyQueueIterator();
    }

    /**
     * Returns an array containing all of the elements in this collection.
     * If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order. The returned array's {@linkplain Class#getComponentType
     * runtime component type} is {@code Object}.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this collection.  (In other words, this method must
     * allocate a new array even if this collection is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array, whose {@linkplain Class#getComponentType runtime component
     * type} is {@code Object}, containing all of the elements in this collection
     */
    public Object[] toArray() {
        int endIndex = indexToGet + count;
        Object[] array = Arrays.copyOfRange(buffer, indexToGet, endIndex);
        // The final index of the range (to), which must be greater than or equal to from, may be greater
        // than original.length, in which case null is placed in all elements of the copy whose index is
        // greater than or equal to original.length - from. The length of the returned array will be to - from.

        // now copying the rest of the queue if it exists from the beginning of the buffer
        if (endIndex > buffer.length)
            System.arraycopy(buffer, 0, array, buffer.length - indexToGet, endIndex - buffer.length);
        return array;
    }

    /**
     * Returns an array containing all of the elements in this collection;
     * the runtime type of the returned array is that of the specified array.
     * If the collection fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this collection.
     *
     * <p>If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element
     * in the array immediately following the end of the collection is set to
     * {@code null}.  (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this collection does
     * not contain any {@code null} elements.)
     *
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose {@code x} is a collection known to contain only strings.
     * The following code can be used to dump the collection into a newly
     * allocated array of {@code String}:
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     * <p>
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param <T> the component type of the array to contain the collection
     * @param a   the array into which the elements of this collection are to be
     *            stored, if it is big enough; otherwise, a new array of the same
     *            runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException  if the runtime type of any element in this
     *                              collection is not assignable to the {@linkplain Class#getComponentType
     *                              runtime component type} of the specified array
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public <T> T[] toArray(T[] a) {
        int endIndex = indexToGet + count;
        // copying the beginning of the queue
        if (a.length < count) {
            // The queue does not fit in the specified array. So a new array is allocated
            // with the runtime type of the specified array and the size of this queue.
            a = (T[]) Arrays.copyOfRange(buffer, indexToGet, endIndex, a.getClass());
        } else {
            // copying the beginning of the queue
            int length = endIndex > buffer.length ? buffer.length - indexToGet : count;
            System.arraycopy(buffer, indexToGet, a, 0, length);
        }
        // copying the rest of the queue if it exists from the beginning of the buffer
        if (endIndex > buffer.length)
            System.arraycopy(buffer, 0, a, buffer.length - indexToGet, endIndex - buffer.length);
        return a;
    }

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns {@code true} if this collection changed as a
     * result of the call.  (Returns {@code false} if this collection does
     * not permit duplicates and already contains the specified element.)<p>
     * <p>
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection.  In particular, some
     * collections will refuse to add {@code null} elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     * <p>
     * If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <i>must</i> throw
     * an exception (rather than returning {@code false}).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     *
     * @param e element whose presence in this collection is to be ensured
     * @return {@code true} if this collection changed as a result of the
     * call
     * @throws UnsupportedOperationException if the {@code add} operation
     *                                       is not supported by this collection
     * @throws ClassCastException            if the class of the specified element
     *                                       prevents it from being added to this collection
     * @throws NullPointerException          if the specified element is null and this
     *                                       collection does not permit null elements
     * @throws IllegalArgumentException      if some property of the element
     *                                       prevents it from being added to this collection
     * @throws IllegalStateException         if the element cannot be added at this
     *                                       time due to insertion restrictions
     */
    public boolean add(E e) {
        // if there is no more space in the buffer, allocating a new buffer two times more
        if (count == buffer.length) {
            buffer = toArray(new Object[calcNewCapacity.applyAsInt(buffer.length)]);
            indexToGet = 0;
            indexToPut = count;
        }
        buffer[indexToPut] = e;
        indexToPut++;
        if (indexToPut == buffer.length) indexToPut = 0;
        count++;
        return true;
    }

    private void removeAt(int indexInQueue) {
        // this method is called internally, so it is not necessary to validate parameters
        // presuming the queue in not empty and index is correct
        int indexInBuffer = (indexToGet + indexInQueue) % buffer.length;
        if (indexInBuffer == indexToGet) { // first in the queue
            indexToGet++;
            if (indexToGet == buffer.length) indexToGet = 0;
            count--;
            return;
        }
        int lastIndex = indexToPut - 1;
        if (lastIndex < 0) lastIndex = buffer.length - 1;
        if (indexInBuffer == lastIndex) { // last in the queue
            indexToPut = lastIndex;
            count--;
            return;
        }
        // in the middle of the queue
        // shifting the rest of the queue
        /*
        for(int i = indexInQueue + 1; i < count; i++) {
            int indexFrom = (indexToGet + i) % buffer.length;
            int indexTo = (indexToGet + i - 1) % buffer.length;
            buffer[indexTo] = buffer[indexFrom];
        }
        */
        int lengthToShift = count - (indexInQueue + 1);
        int endIndex = indexInBuffer + 1 + lengthToShift;
        int lengthOfFirstHalf = endIndex > buffer.length ? buffer.length - (indexInBuffer + 1) : lengthToShift;
        System.arraycopy(buffer, indexInBuffer + 1, buffer, indexInBuffer, lengthOfFirstHalf);
        if (endIndex > buffer.length) {
            buffer[buffer.length - 1] = buffer[0];
            System.arraycopy(buffer, 1, buffer, 0, lengthToShift - lengthOfFirstHalf - 1);
        }
        indexToPut = lastIndex;
        count--;
    }

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).  More formally,
     * removes an element {@code e} such that
     * {@code Objects.equals(o, e)}, if
     * this collection contains one or more such elements.  Returns
     * {@code true} if this collection contained the specified element (or
     * equivalently, if this collection changed as a result of the call).
     *
     * @param o element to be removed from this collection, if present
     * @return {@code true} if an element was removed as a result of this call
     * @throws ClassCastException            if the type of the specified element
     *                                       is incompatible with this collection
     *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if the specified element is null and this
     *                                       collection does not permit null elements
     *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws UnsupportedOperationException if the {@code remove} operation
     *                                       is not supported by this collection
     */
    public boolean remove(Object o) {
        for (int i = 0; i < count; i++) {
            int index = (indexToGet + i) % buffer.length;
            if (buffer[index] == null) {
                if (o == null) {
                    removeAt(i);
                    return true;
                }
            } else if (buffer[index].equals(o)) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this collection contains all of the elements
     * in the specified collection.
     *
     * @param c collection to be checked for containment in this collection
     * @return {@code true} if this collection contains all of the elements
     * in the specified collection
     * @throws ClassCastException   if the types of one or more elements
     *                              in the specified collection are incompatible with this
     *                              collection
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection contains one
     *                              or more null elements and this collection does not permit null
     *                              elements
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>),
     *                              or if the specified collection is null.
     * @see #contains(Object)
     */
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    /**
     * Adds all of the elements in the specified collection to this collection
     * (optional operation).  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this collection, and this collection is
     * nonempty.)
     *
     * @param c collection containing elements to be added to this collection
     * @return {@code true} if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the {@code addAll} operation
     *                                       is not supported by this collection
     * @throws ClassCastException            if the class of an element of the specified
     *                                       collection prevents it from being added to this collection
     * @throws NullPointerException          if the specified collection contains a
     *                                       null element and this collection does not permit null elements,
     *                                       or if the specified collection is null
     * @throws IllegalArgumentException      if some property of an element of the
     *                                       specified collection prevents it from being added to this
     *                                       collection
     * @throws IllegalStateException         if not all the elements can be added at
     *                                       this time due to insertion restrictions
     * @see #add(Object)
     */
    public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E e : c) {
            add(e);
            result = true;
        }
        return result;
    }

    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection (optional operation).  After this call returns,
     * this collection will contain no elements in common with the specified
     * collection.
     *
     * @param c collection containing elements to be removed from this collection
     * @return {@code true} if this collection changed as a result of the
     * call
     * @throws UnsupportedOperationException if the {@code removeAll} method
     *                                       is not supported by this collection
     * @throws ClassCastException            if the types of one or more elements
     *                                       in this collection are incompatible with the specified
     *                                       collection
     *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if this collection contains one or more
     *                                       null elements and the specified collection does not support
     *                                       null elements
     *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>),
     *                                       or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        int i = 0;
        while (i < count) {
            int index = (indexToGet + i) % buffer.length;
            if (c.contains(buffer[index])) {
                result = true;
                removeAt(i);
            } else
                i++;
        }
        return result;
    }

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection.
     *
     * @param c collection containing elements to be retained in this collection
     * @return {@code true} if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the {@code retainAll} operation
     *                                       is not supported by this collection
     * @throws ClassCastException            if the types of one or more elements
     *                                       in this collection are incompatible with the specified
     *                                       collection
     *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if this collection contains one or more
     *                                       null elements and the specified collection does not permit null
     *                                       elements
     *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>),
     *                                       or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean retainAll(Collection<?> c) {
        boolean result = false;
        int i = 0;
        while (i < count) {
            int index = (indexToGet + i) % buffer.length;
            if (!c.contains(buffer[index])) {
                result = true;
                removeAt(i);
            } else
                i++;
        }
        return result;
    }

    /**
     * Removes all of the elements from this collection (optional operation).
     * The collection will be empty after this method returns.
     *
     * @throws UnsupportedOperationException if the {@code clear} operation
     *                                       is not supported by this collection
     */
    public void clear() {
        count = 0;
        indexToPut = 0;
        indexToGet = 0;
    }

    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions.
     * When using a capacity-restricted queue, this method is generally
     * preferable to {@link #add}, which can fail to insert an element only
     * by throwing an exception.
     *
     * @param e the element to add
     * @return {@code true} if the element was added to this queue, else
     * {@code false}
     * @throws ClassCastException       if the class of the specified element
     *                                  prevents it from being added to this queue
     * @throws NullPointerException     if the specified element is null and
     *                                  this queue does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *                                  prevents it from being added to this queue
     */
    public boolean offer(E e) {
        return add(e);
    }

    /**
     * Retrieves and removes the head of this queue.  This method differs
     * from {@link #poll() poll()} only in that it throws an exception if
     * this queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    @SuppressWarnings("Duplicates")
    public E remove() {
        if (count == 0) throw new NoSuchElementException();
        E e = (E) buffer[indexToGet];
        count--;
        indexToGet++;
        if (indexToGet == buffer.length) indexToGet = 0;
        return e;
    }

    /**
     * Retrieves and removes the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    public E poll() {
        if (count == 0) return null;
        return remove();
    }

    /**
     * Retrieves, but does not remove, the head of this queue.  This method
     * differs from {@link #peek peek} only in that it throws an exception
     * if this queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    public E element() {
        if (count == 0) throw new NoSuchElementException();
        return (E) buffer[indexToGet];
    }

    /**
     * Retrieves, but does not remove, the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    public E peek() {
        if (count == 0) return null;
        return (E) buffer[indexToGet];
    }

    private class MyQueueIterator implements Iterator<E> {
        int iCount;
        int iIndexToGet;

        MyQueueIterator() {
            iCount = count;
            iIndexToGet = indexToGet;
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        public boolean hasNext() {
            return iCount > 0;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @SuppressWarnings("Duplicates")
        public E next() {
            if (iCount == 0) throw new NoSuchElementException();
            E e = (E) buffer[iIndexToGet];
            iCount--;
            iIndexToGet++;
            if (iIndexToGet == buffer.length) iIndexToGet = 0;
            return e;
        }

        public void remove() {

        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (int i = 0; i < count; i++) {
            if (!first) {
                sb.append(", ");
            } else first = false;
            int index = (indexToGet + i) % buffer.length;
            if (buffer[index] == null) {
                sb.append("null");
            } else {
                sb.append(buffer[index].toString());
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
