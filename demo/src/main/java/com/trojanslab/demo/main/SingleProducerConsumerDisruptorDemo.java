package com.trojanslab.demo.main;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;



@SpringBootApplication
public class SingleProducerConsumerDisruptorDemo{
	
	public static final int BATCH_SIZE = 256;
	public static final int ringBufferSize = 1024;
	
	static final long startTime=System.currentTimeMillis();
	static final int NO_OF_EVENTS = Integer.MAX_VALUE;
	
    /**
     * @param args
     * 
     * @see {@link https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html }
     */
    public static void main(String[] args) {
    	
    	
    	Executor executor = Executors.newCachedThreadPool();
		final Executor consumerExecutor = Executors.newSingleThreadExecutor();
		
		final Disruptor<ValueEvent> disruptor=new Disruptor<ValueEvent>(ValueEvent::new, ringBufferSize,executor,ProducerType.SINGLE, new YieldingWaitStrategy());
		
		final RingBuffer<ValueEvent> ringBuffer = disruptor.getRingBuffer();
		
		BatchEventProcessor<ValueEvent> batchEventProcessor= new BatchEventProcessor<ValueEvent>(ringBuffer, ringBuffer.newBarrier(), SingleProducerConsumerDisruptorDemo::handleEvent);
		disruptor.handleEventsWith(batchEventProcessor);   
		
		Thread publisher = new Thread() {
			@Override
			public void run() {
				for (int i = NO_OF_EVENTS; i--> 0;) {
					long next = ringBuffer.next();
					ValueEvent event=ringBuffer.get(next);
					event.setValue(i);
					ringBuffer.publish(next);
					if (i==0) {
						System.out.println("Putting  last event @ "+next);
					}
				}
			}
		};
		
		consumerExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				disruptor.start();
				
			}
		});
		
		publisher.start();
		
        SpringApplication.run(SingleProducerConsumerDisruptorDemo.class, args);
    }
    

	public static void handleEvent(ValueEvent event,long sequence,boolean endOfBatch){
		if(event.getValue() == 0L){
			System.out.println("Encountered last event @ "+sequence);
			System.out.println("Throughput:" + (NO_OF_EVENTS*1000L)/(System.currentTimeMillis()-startTime));
			System.exit(0);
		}
	}
}
