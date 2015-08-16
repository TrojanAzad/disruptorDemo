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
 * 
 * 
 * @author Chaitanya
 *
 */
public class MultiProducerConsumerQDemo {
	
	private final BlockingQueue<ValueEvent> blockingQueue;
	
	private final Thread[] producers;
	private final Thread[] consumers;
	
	private final Thread statisticsCaptor;
	
	private final ScheduledExecutorService executorService;
	

	/**
	 * 
	 */
	public MultiProducerConsumerQDemo() {
		
		blockingQueue=new ArrayBlockingQueue<>(1024);
		
		producers = new Thread[5];
		for (int i=5; i --> 0; ) {
			producers[i] = new Thread(){
				@Override
				public void run() {
					startProducerThread();
				}
			};
		}
		
		consumers = new Thread[5];
		for (int i=5; i --> 0; ) {
			consumers[i] = new Thread(){
				@Override
				public void run() {
					startConsumerThread();
				}
			};
		}
		
		statisticsCaptor=new Thread(){
			@Override
			public void run() {
				System.out.println("Queue statistics "+blockingQueue.size());
			}
		};
		
		startProducers();
		startConsumers();
		//statisticsCaptor.start();
		
		executorService=Executors.newSingleThreadScheduledExecutor();
		
		executorService.scheduleAtFixedRate(statisticsCaptor, 1, 1, TimeUnit.SECONDS);
		
	}

	private void startConsumers() {
		for (Thread thread : consumers) {
			thread.start();
		}
		
	}

	private void startProducers() {
		for (Thread thread : producers) {
			thread.start();
		}
	}

	protected void startConsumerThread() {
		
		ValueEvent product=null;

		try {
			while((product=blockingQueue.take())!=null){
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}

	protected void startProducerThread() {
		
		for (int j = Integer.MAX_VALUE/5; j --> 0; ) {
				ValueEvent event=new ValueEvent();
				event.setValue(j);
				blockingQueue.offer(event);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MultiProducerConsumerQDemo blockingQueueDemo=new MultiProducerConsumerQDemo();

	}

}
