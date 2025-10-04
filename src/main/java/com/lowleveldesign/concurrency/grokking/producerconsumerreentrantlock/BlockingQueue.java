package com.lowleveldesign.concurrency.grokking.producerconsumerreentrantlock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<E> {
    private final Queue<E> queue = new LinkedList<>();
    private final int capacity;

    private final ReentrantLock putLock = new ReentrantLock();
    private final Condition isFull = putLock.newCondition();

    private final ReentrantLock takeLock = new ReentrantLock();
    private final Condition isEmpty = takeLock.newCondition();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void put(E item) throws InterruptedException {
        putLock.lock();
        try {
            // Use inline check to avoid double locking inside size()
            while (isFull()) {
                System.out.println(Thread.currentThread().getName() + " waiting: Queue FULL");
                isFull.await();
            }
            queue.add(item);
            System.out.println(Thread.currentThread().getName() + " produced: " + item);
        } finally {
            putLock.unlock();
        }

        // Signal consumer that queue is not empty
        takeLock.lock();
        try {
            isEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    public E take() throws InterruptedException {
        E item;

        takeLock.lock();
        try {
            while (isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " waiting: Queue EMPTY");
                isEmpty.await();
            }
            item = queue.remove();
            System.out.println(Thread.currentThread().getName() + " consumed: " + item);
        } finally {
            takeLock.unlock();
        }

        // Signal producer that queue is not full
        putLock.lock();
        try {
            isFull.signal();
        } finally {
            putLock.unlock();
        }

        return item;
    }

    // Public helper methods for external callers


    public boolean isFull() {
            return queue.size() == capacity;
    }

    public boolean isEmpty() {
            return queue.isEmpty();

    }
}
