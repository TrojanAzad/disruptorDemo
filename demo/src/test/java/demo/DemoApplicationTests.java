package demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.trojanslab.demo.main.SingleProducerConsumerDisruptorDemo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SingleProducerConsumerDisruptorDemo.class)
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testRemainder(){
		Assert.assertTrue((1024%1024) == (1024 & (1^(2<<10))));
	}

}
