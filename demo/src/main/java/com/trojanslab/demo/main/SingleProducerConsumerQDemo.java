/**
 * 
 */
package com.trojanslab.demo.main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * 
 * @author Chaitanya
 *
 */
public class SingleProducerConsumerQDemo {
	
	private final BlockingQueue<ValueEvent> blockingQueue;
	
	private final Thread producer;
	
	private final Thread consumer;
	private final int NO_OF_EVENTS = Integer.MAX_VALUE;
	
	final long startTime = System.currentTimeMillis();
	

	/**
	 * 
	 */
	public SingleProducerConsumerQDemo() {
		
		blockingQueue=new LinkedBlockingQueue<>(1024);
		
		producer=new Thread(){
			@Override
			public void run() {
				startProducerThread();
			}
		};
		
		consumer=new Thread(){
			@Override
			public void run() {
				startConsumerThread();
			}
		};
		

		
		consumer.start();
		
		
		producer.start();
		//statisticsCaptor.start();
		
		
		
	}

	protected void startConsumerThread() {
		ValueEvent event=null;
			while (true) {
				try {
					while ((event = blockingQueue.take()) != null) {
						if (event.getValue() == 0L) {
							System.out.println("Received event 0 at"+System.currentTimeMillis());
							System.out.println("Size of the Q :"+ blockingQueue.size());
							System.out.println("Throughput : "+ (NO_OF_EVENTS * 1000)/(System.currentTimeMillis() - startTime));
							System.exit(0);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		
	}

	protected void startProducerThread() {
		

		for (int j = NO_OF_EVENTS; j --> 0; ) {
			ValueEvent event=new ValueEvent();
				event.setValue(j);
				blockingQueue.offer(event);
				if(j==0)
					System.out.println("Publishing event 0 at"+System.currentTimeMillis());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SingleProducerConsumerQDemo blockingQueueDemo=new SingleProducerConsumerQDemo();

	}

}
