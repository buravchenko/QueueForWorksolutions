package ru.worksolutions.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SimplifiableJUnitAssertion")
class MyQueueTest {

    @org.junit.jupiter.api.Test
    void changingCapacity() {
        MyQueue<Integer> q1 = new MyQueue<>(5);
        q1.addAll(Arrays.asList(1, 2, 3, 4, 5));
        assertEquals(5, q1.buffer.length);
        q1.add(6);
        // by default capacity multiplies by two
        assertEquals(10, q1.buffer.length);
        assertEquals("{1, 2, 3, 4, 5, 6}", q1.toString());

        MyQueue<Integer> q2 = new MyQueue<>(5, x -> x + 100);
        q2.addAll(Arrays.asList(1, 2, 3, 4, 5));
        assertEquals(5, q2.buffer.length);
        q2.add(6);
        assertEquals(105, q2.buffer.length);
        assertEquals("{1, 2, 3, 4, 5, 6}", q2.toString());
    }

    @org.junit.jupiter.api.Test
    void contains() {
        MyQueue<Integer> q = new MyQueue<>();
        for (int i = 0; i < 1000; i++) q.add(i);
        for (int i = 0; i < 1000; i++) assertEquals(true, q.contains(i));
        for (int i = 0; i < 50; i++) q.remove();
        for (int i = 0; i < 50; i++) assertEquals(false, q.contains(i));
        for (int i = 50; i < 1000; i++) assertEquals(true, q.contains(i));
    }

    @org.junit.jupiter.api.Test
    void iterator() {
        MyQueue<Integer> q = new MyQueue<>(5);
        q.addAll(Arrays.asList(1, 8, 7, 9));
        assertEquals("{1, 8, 7, 9}", q.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for(int i : q) {
            if(!first) {
                sb.append(", ");
            } else first = false;
            sb.append(String.valueOf(i));
        }
        sb.append("}");
        assertEquals("{1, 8, 7, 9}", sb.toString());
    }

    @org.junit.jupiter.api.Test
    void toArray() {
        MyQueue<Integer> q = new MyQueue<>(5);
        for(int i = 0; i < 5; i++) q.add(i);
        assertEquals("{0, 1, 2, 3, 4}", q.toString());
        Object[] arr = q.toArray();
        assertEquals(5, arr.length);
        for(int i = 0; i < 5; i++) assertEquals(i, arr[i]);
        q.remove();
        q.remove();
        assertEquals("{2, 3, 4}", q.toString());
        q.add(5);
        q.add(6);
        assertEquals("{2, 3, 4, 5, 6}", q.toString());
        arr = q.toArray();
        assertEquals(5, arr.length);
        for(int i = 0; i < 5; i++) assertEquals(i, (int)arr[i] - 2);
    }

    @org.junit.jupiter.api.Test
    void toArrayT() {
        MyQueue<Integer> q = new MyQueue<>(5);
        for(int i = 0; i < 5; i++) q.add(i);
        assertEquals("{0, 1, 2, 3, 4}", q.toString());
        Object[] arr = q.toArray(new Integer[5]);
        assertEquals(5, arr.length);
        for(int i = 0; i < 5; i++) assertEquals(i, arr[i]);
        q.remove();
        q.remove();
        assertEquals("{2, 3, 4}", q.toString());
        q.add(5);
        q.add(6);
        assertEquals("{2, 3, 4, 5, 6}", q.toString());
        arr = q.toArray(new Integer[5]);
        assertEquals(5, arr.length);
        for(int i = 0; i < 5; i++) assertEquals(i, (int)arr[i] - 2);
    }

    @org.junit.jupiter.api.Test
    void remove() {
        MyQueue<Integer> q = new MyQueue<>(5);
        for(int i = 1; i <= 5; i++) q.add(i);
        assertEquals("{1, 2, 3, 4, 5}", q.toString());
        assertEquals(1, (int)q.remove());
        assertEquals(2, (int)q.remove());
        assertEquals("{3, 4, 5}", q.toString());
        q.add(8);
        q.add(9);
        assertEquals("{3, 4, 5, 8, 9}", q.toString());
        q.add(10); // new buffer should be allocated at this point
        assertEquals("{3, 4, 5, 8, 9, 10}", q.toString());
        assertEquals(3, (int)q.remove());
        assertEquals("{4, 5, 8, 9, 10}", q.toString());
        for(int i = 0; i < 5; i++) {
            q.remove();
        }
        //noinspection ResultOfMethodCallIgnored
        assertThrows(NoSuchElementException.class, q::remove);
    }

    @org.junit.jupiter.api.Test
    void removeObject() {
        MyQueue<Integer> q = new MyQueue<>(5);
        for(int i = 1; i <= 5; i++) q.add(i);
        assertEquals("{1, 2, 3, 4, 5}", q.toString());
        assertEquals(true, q.remove(2));
        assertEquals("{1, 3, 4, 5}", q.toString());
        assertEquals(true, q.remove(1));
        assertEquals("{3, 4, 5}", q.toString());
        q.add(6);
        assertEquals("{3, 4, 5, 6}", q.toString());
        assertEquals(true, q.remove(4));
        assertEquals("{3, 5, 6}", q.toString());
        assertEquals(false, q.remove(10));
        assertEquals("{3, 5, 6}", q.toString());
        assertEquals(3, (int)q.remove());
        assertEquals(5, (int)q.remove());
        assertEquals("{6}", q.toString());
        q.add(7);
        q.add(8);
        q.add(9);
        q.add(10);
        assertEquals("{6, 7, 8, 9, 10}", q.toString());
        assertEquals(true, q.remove(9));
        assertEquals("{6, 7, 8, 10}", q.toString());
    }

    @org.junit.jupiter.api.Test
    void containsAll() {
        MyQueue<Integer> q = new MyQueue<>(5);
        for(int i = 1; i <= 5; i++) q.add(i);
        assertEquals(true, q.containsAll(Arrays.asList(1, 2, 3, 4, 5)));
        assertEquals(true, q.containsAll(Arrays.asList(1, 2, 3, 4)));
        assertEquals(true, q.containsAll(Arrays.asList(1, 2, 4, 5)));
        assertEquals(false, q.containsAll(Arrays.asList(1, 2, 4, 5, 6)));
    }

    @org.junit.jupiter.api.Test
    void addAll() {
        MyQueue<Integer> q = new MyQueue<>(5);
        q.addAll(Arrays.asList(1, 2, 3, 4, 5));
        assertEquals("{1, 2, 3, 4, 5}", q.toString());
    }

    @org.junit.jupiter.api.Test
    void removeAll() {
        MyQueue<Integer> q = new MyQueue<>(5);
        q.addAll(Arrays.asList(1, 2, 3, 4, 5));
        assertEquals("{1, 2, 3, 4, 5}", q.toString());
        q.removeAll(Arrays.asList(2, 3));
        assertEquals("{1, 4, 5}", q.toString());
        assertEquals(1, (int)q.remove());
        assertEquals("{4, 5}", q.toString());
        q.addAll(Arrays.asList(6, 7, 8, 9));
        assertEquals("{4, 5, 6, 7, 8, 9}", q.toString());
        q.removeAll(Arrays.asList(5, 8));
        assertEquals("{4, 6, 7, 9}", q.toString());
    }

    @org.junit.jupiter.api.Test
    void retainAll() {
        MyQueue<Integer> q = new MyQueue<>(5);
        q.addAll(Arrays.asList(1, 2, 3, 4, 5));
        assertEquals("{1, 2, 3, 4, 5}", q.toString());
        q.retainAll(Arrays.asList(2, 3, 10, 18, 5));
        assertEquals("{2, 3, 5}", q.toString());
        assertEquals(2, (int)q.remove());
        assertEquals("{3, 5}", q.toString());
        q.addAll(Arrays.asList(6, 7, 8, 9));
        assertEquals("{3, 5, 6, 7, 8, 9}", q.toString());
        q.retainAll(Arrays.asList(100, 5, 8, 9, 21));
        assertEquals("{5, 8, 9}", q.toString());
    }

    @org.junit.jupiter.api.Test
    void element() {
        MyQueue<Integer> q = new MyQueue<>(5);
        //noinspection ResultOfMethodCallIgnored
        assertThrows(NoSuchElementException.class, q::element);
        q.addAll(Arrays.asList(1, 2, 3));
        assertEquals("{1, 2, 3}", q.toString());
        assertEquals(1, (int)q.element());
        assertEquals("{1, 2, 3}", q.toString());
        assertEquals(1, (int)q.remove());
        assertEquals(2, (int)q.element());
    }
}