package com.lowleveldesign.concurrency.grokking.producerconsumerreentrantlock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a serial kind of Producer consumer solution, here only producer can produce
 * or consumer can consume, both cant do at the same time because we are using
 * a single reentrant lock. The other one in this repo in the folder producerconsumer uses two object
 * and producer and consumer can work concurrently but there is a chance of race condition
 * when we check the queue size as the queue used is not thread safe. So to make that fully
 * concurrent just replace the queue with an atomic linkedList or whatever.
 *
 * This implementation is thread safe as we are using the same lock to access the queue so only
 * one thread can access it
 *
 * */
public class BlockingQueue<E> {
    private final Queue<E> queue = new LinkedList<>();
    private final int capacity;

    private final ReentrantLock lock = new ReentrantLock();         // Single lock
    private final Condition notFull = lock.newCondition();          // Condition for producers
    private final Condition notEmpty = lock.newCondition();         // Condition for consumers

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void put(E item) throws InterruptedException {
        lock.lock();
        try {
            while (isFull()) {
                System.out.println(Thread.currentThread().getName() + " waiting: Queue FULL");
                notFull.await();  // Wait until space is available
            }

            queue.add(item);
            System.out.println(Thread.currentThread().getName() + " produced: " + item);

            // Signal one waiting consumer that item is available
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public E take() throws InterruptedException {
        lock.lock();
        try {
            while (isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " waiting: Queue EMPTY");
                notEmpty.await();  // Wait until item is available
            }

            E item = queue.remove();
            System.out.println(Thread.currentThread().getName() + " consumed: " + item);

            // Signal one waiting producer that space is available
            notFull.signal();

            return item;
        } finally {
            lock.unlock();
        }
    }

    // Optional: Thread-safe helpers if needed externally
    public boolean isFull() {
        lock.lock();
        try {
            return queue.size() == capacity;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
