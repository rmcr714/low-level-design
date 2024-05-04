package com.lowleveldesign.concurrency.producerconsumer;

import java.util.Random;

public class Consumer implements Runnable {


  BlockingQueue blockingQueue;

  Random random = new Random();

  Consumer(BlockingQueue blockingQueue) {
    this.blockingQueue = blockingQueue;
  }

  public void run()  {

    while(true) {

      while(blockingQueue.isEmpty()) {

        try {
          System.out.println("CONSUMER Thread "+Thread.currentThread().getName() +" waiting for data");
          blockingQueue.queueIsEmpty();
          System.out.println("CONSUMER Thread "+Thread.currentThread().getName()+" continuing to consume data ");
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

      }


      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      blockingQueue.take();
      blockingQueue.queueIsNotFull();






    }



  }

}
