package com.trojanslab.demo.main;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;



@SpringBootApplication
public class SingleProducerConsumerDisruptorDemo {
	

    @SuppressWarnings("unchecked")
	public static void main(String[] args) {
    	
    	
    	Executor executor = Executors.newSingleThreadExecutor();
		Disruptor<ValueEvent> disruptor=new Disruptor<ValueEvent>(new EventFactory<ValueEvent>() {

			@Override
			public ValueEvent newInstance() {
				return new ValueEvent();
			}
		}, executor, new SingleThreadedClaimStrategy(1024*1024), new SleepingWaitStrategy() );
		
		
		
		long startTime = System.currentTimeMillis();
		disruptor.handleEventsWith(new EventHandler<ValueEvent>() {

			@Override
			public void onEvent(ValueEvent event, long sequence, boolean endOfBatch)
					throws Exception {
				
				/*if((event.getValue()&(1^(2<<10)))==0){
					System.out.println("Consuming event slot :"+ event.getValue()+"@"+System.currentTimeMillis());
				}*/
				if(event.getValue()==0L){
					System.out.println("Process finished in "+ (System.currentTimeMillis()-startTime));
					System.exit(0);
				}
			}
			
		});   
		
		RingBuffer<ValueEvent> buffer=disruptor.start();
		

		Thread publisher = new Thread() {
			@Override
			public void run() {
				for (int i = Integer.MAX_VALUE; i-- > 0;) {
					
					long nextSlot = buffer.next();
					ValueEvent event = buffer.get(nextSlot);
					event.setValue(i);
					/*if (((event.getValue() & (1 ^ (2 << 10))) == 0)){
						System.out.println("Publishing event slot :"+ event.getValue() +"@"+System.currentTimeMillis());
					}*/
					buffer.publish(nextSlot);
				}
			}
		};
		
		publisher.start();
		
		
		
		
		//executorService.scheduleAtFixedRate(statisticsCaptor, 1, 1, TimeUnit.SECONDS);
		
        SpringApplication.run(SingleProducerConsumerDisruptorDemo.class, args);
    }
}
