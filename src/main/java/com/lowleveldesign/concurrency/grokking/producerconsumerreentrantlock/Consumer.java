package com.lowleveldesign.concurrency.grokking.producerconsumerreentrantlock;

public class Consumer implements Runnable {
    private final BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String item = queue.take();
                Thread.sleep(1500); // simulate time to consume
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " interrupted.");
                break;
            }
        }
    }
}
