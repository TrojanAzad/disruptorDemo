/**
 * 
 */
package com.trojanslab.demo.main;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Time taken to process 2^31-1 events = 3.2785s
 * 
 * @author Chaitanya
 *
 */
public class SingleProducerConsumerQDemo {
	
	private final BlockingQueue<ValueEvent> blockingQueue;
	
	private final Thread producer;
	
	private final Thread consumer;
	
	private final Thread statisticsCaptor;
	
	private final ScheduledExecutorService executorService;
	
	final long startTime = System.currentTimeMillis();
	

	/**
	 * 
	 */
	public SingleProducerConsumerQDemo() {
		
		blockingQueue=new ArrayBlockingQueue<>(1000);
		
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
		
		statisticsCaptor=new Thread(){
			@Override
			public void run() {
				System.out.println("Queue statistics "+blockingQueue.size());
			}
		};
		
		consumer.start();
		
		
		producer.start();
		//statisticsCaptor.start();
		
		executorService=Executors.newSingleThreadScheduledExecutor();
		
		executorService.scheduleAtFixedRate(statisticsCaptor, 1, 1, TimeUnit.SECONDS);
		
	}

	protected void startConsumerThread() {
		ValueEvent event=null;
			while (true) {
				try {
					while ((event = blockingQueue.take()) != null) {
						if (event.getValue() == 0L) {
							System.out.println("Publishing event 0 at"+System.currentTimeMillis());
							System.out.println("Time taken"
									+ (System.currentTimeMillis() - startTime));
							System.exit(0);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		
	}

	protected void startProducerThread() {
		ValueEvent event=new ValueEvent();
		for (int j = Integer.MAX_VALUE; j --> 0; ) {
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
