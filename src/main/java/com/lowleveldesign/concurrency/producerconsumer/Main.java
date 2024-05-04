package com.lowleveldesign.concurrency.producerconsumer;

public class Main {

  public static void main(String[] args) throws InterruptedException {

   BlockingQueue<String> blockingQueue = new BlockingQueue<>(3);

   Thread producer1 = new Thread(new Producer(blockingQueue));
   Thread producer2 = new Thread(new Producer(blockingQueue));
   Thread producer3 = new Thread(new Producer(blockingQueue));

   Thread consumer1 = new Thread(new Consumer(blockingQueue));
   Thread consumer2 = new Thread(new Consumer(blockingQueue));



   producer1.start();
   producer2.start();


   consumer1.start();
   consumer2.start();



   Thread.sleep(30000);




  }

}
