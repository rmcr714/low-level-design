package com.lowleveldesign.concurrency.producerconsumer;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<E> {

  public Queue<E> q = new LinkedList<>();
  int size ;

  BlockingQueue(int size) {
    this.size = size;
  }
  private final Object IS_NOT_FULL = new Object();
  private final Object IS_NOT_EMPTY = new Object();


  public boolean isFull() {
    System.out.println(" The Q size is "+q.size());
    return q.size() >= size;
  }

  public boolean isEmpty() {
    return q.isEmpty();
  }

  public void put(E data)  {
     synchronized (IS_NOT_FULL){

       if(q.size() < size ) {
         q.add(data);
       }
     }

  }

  public void queueIsFull() throws InterruptedException {
    synchronized (IS_NOT_FULL) {
      IS_NOT_FULL.wait();
    }

  }

  public void queueIsNotFull() {
    synchronized (IS_NOT_FULL) {
      IS_NOT_FULL.notify();
    }
  }

  public void queueIsEmpty() throws InterruptedException {
    synchronized (IS_NOT_EMPTY) {
      IS_NOT_EMPTY.wait();
    }
  }

  public void queueIsNotEmpty()  {
    synchronized (IS_NOT_EMPTY) {
      IS_NOT_EMPTY.notify();
    }
  }



  public void take()  {

    synchronized (IS_NOT_EMPTY) {

      E data =  q.remove();
      System.out.println("Consumer THREAD " +Thread.currentThread().getName()+ " data is "+data);


    }

  }

}
