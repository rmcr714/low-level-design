package com.lowleveldesign.concurrency.producerconsumer;

import java.util.Random;

public class Producer implements Runnable {

  BlockingQueue blockingQueue;

  Random random = new Random();

  Producer(BlockingQueue blockingQueue) {
    this.blockingQueue = blockingQueue;
  }


  public void run() {

    while(true) {

      while (blockingQueue.isFull()) {

        System.out.println("  PRODUCER Thread " +Thread.currentThread().getName()+ " waiting as Queue is FULL");
        try {
          blockingQueue.queueIsFull();
          System.out.println(" PRODUCER Thread " +Thread.currentThread().getName()+ " Continuing to add data");
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

      }

      String data = generateMessage();
      System.out.println("PRODUCER THREAD "+Thread.currentThread().getName()+ " adding "+data+" to the queue");


      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      blockingQueue.put(data);
      blockingQueue.queueIsNotEmpty();

    }



  }


  public String generateMessage() {

     return "message number "+random.nextInt();


  }



}
