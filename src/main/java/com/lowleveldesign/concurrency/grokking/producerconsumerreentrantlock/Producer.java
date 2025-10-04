package com.lowleveldesign.concurrency.grokking.producerconsumerreentrantlock;

import java.util.Random;

public class Producer implements Runnable {
    private final BlockingQueue<String> queue;
    private final Random random = new Random();

    public Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            String item = "Item-" + random.nextInt(1000);
            try {
                queue.put(item);
                Thread.sleep(1000); // simulate time to produce
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " interrupted.");
                break;
            }
        }
    }
}
