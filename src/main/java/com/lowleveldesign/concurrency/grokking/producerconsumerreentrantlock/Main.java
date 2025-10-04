package com.lowleveldesign.concurrency.grokking.producerconsumerreentrantlock;

public class Main {
    public static void main(String[] args) {
        BlockingQueue<String> queue = new BlockingQueue<>(5); // capacity of the queue

        Thread producer1 = new Thread(new Producer(queue), "Producer-1");
        Thread producer2 = new Thread(new Producer(queue), "Producer-2");

        Thread consumer1 = new Thread(new Consumer(queue), "Consumer-1");
        Thread consumer2 = new Thread(new Consumer(queue), "Consumer-2");

        producer1.start();
        producer2.start();

        consumer1.start();
        consumer2.start();
    }
}
