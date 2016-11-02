import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class FrequencyRun {

	final GpioController gpio;
	final GpioPinDigitalOutput pin;
	final long duration=339000;//339000
	
	
	public FrequencyRun() {
		super();
		gpio = GpioFactory.getInstance();
		pin =gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "MyLED",PinState.LOW);
		pin.setShutdownOptions(true, PinState.LOW); 
	}

	public static void main(String[] args) throws Exception {				 		 		 	
		FrequencyRun r=new FrequencyRun();
		
		
		for(int i=0;i<10;i++){
			r.initClock();
			r.stop();
		}
		
		TimeUnit.MILLISECONDS.sleep(500);
		r.shutdown();		
	}

	public void test() throws Exception{
		pin.high();
		Thread.sleep(30000);
	}
	
	public void  shutdown(){
		if(gpio!=null)
			gpio.shutdown();
	}
	
	public void initClock(){
		long startTime = System.nanoTime();
		long estimatedTime = 0;
		pin.high();
		while (estimatedTime < duration) {
			estimatedTime = System.nanoTime() - startTime;
		}
		pin.low();		
	}

	public void runCycle8() throws Exception {
		long[] all = new long[72];
		byte[] da = new byte[72];
		for (int i = 0; i < all.length; i++) {
			long startTime = System.nanoTime();
			long estimatedTime = 0;
			if (i >= 48 && i <= 53) {//1
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			}else if(i >= 57 && i <= 59) {//1
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			} else {				
				if (i % 3 == 0) {//0
					da[i] = 0;
				} else {
					da[i] = 1;
				}
			}
			if(da[i]==0){
				pin.low();
			}else{
				pin.high();
			}			
			while (estimatedTime < duration) {
				estimatedTime = System.nanoTime() - startTime;
			}
			all[i] = estimatedTime;
		}
		pin.low();
		for (int i = 0; i < all.length; i++) {
			System.out.println(i + ":" + all[i] + " :" + da[i]);
		}
		TimeUnit.MILLISECONDS.sleep(10);
	}
	
	public void runCycle7() throws Exception {
		long[] all = new long[72];
		byte[] da = new byte[72];
		for (int i = 0; i < all.length; i++) {
			long startTime = System.nanoTime();
			long estimatedTime = 0;
			if (i < 48 || i > 53) {
				if (i % 3 == 0) {
					da[i] = 0;
				} else {
					da[i] = 1;
				}
			} else {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			}
			if(da[i]==0){
				pin.low();
			}else{
				pin.high();
			}			
			while (estimatedTime < duration) {
				estimatedTime = System.nanoTime() - startTime;
			}
			all[i] = estimatedTime;
		}
		pin.low();
		for (int i = 0; i < all.length; i++) {
			System.out.println(i + ":" + all[i] + " :" + da[i]);
		}
		TimeUnit.MILLISECONDS.sleep(10);
	}

	public void runCycle6() throws Exception {
		long[] all = new long[72];
		byte[] da = new byte[72];
		for (int i = 0; i < all.length; i++) {
			long startTime = System.nanoTime();
			long estimatedTime = 0;
			if (i >= 48 && i <= 50) {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			}else if (i >= 57 && i <= 62) {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			} else {
				
				if (i % 3 == 0) {
					da[i] = 0;
				} else {
					da[i] = 1;
				}
			}
			if(da[i]==0){
				pin.low();
			}else{
				pin.high();
			}			
			while (estimatedTime < duration) {
				estimatedTime = System.nanoTime() - startTime;
			}
			all[i] = estimatedTime;
		}
		pin.low();
		for (int i = 0; i < all.length; i++) {
			System.out.println(i + ":" + all[i] + " :" + da[i]);
		}
		TimeUnit.MILLISECONDS.sleep(10);
	}
	
	public void runCycle5() throws Exception {
		long[] all = new long[72];
		byte[] da = new byte[72];
		for (int i = 0; i < all.length; i++) {
			long startTime = System.nanoTime();
			long estimatedTime = 0;
			if (i < 51 || i > 53) {
				if (i % 3 == 0) {
					da[i] = 0;
				} else {
					da[i] = 1;
				}
			} else {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			}
			if(da[i]==0){
				pin.low();
			}else{
				pin.high();
			}			
			while (estimatedTime < duration) {
				estimatedTime = System.nanoTime() - startTime;
			}
			all[i] = estimatedTime;
		}
		pin.low();
		for (int i = 0; i < all.length; i++) {
			System.out.println(i + ":" + all[i] + " :" + da[i]);
		}
		TimeUnit.MILLISECONDS.sleep(10);
	}	
	
	public void runCycle4() throws Exception {
		long[] all = new long[72];
		byte[] da = new byte[72];
		for (int i = 0; i < all.length; i++) {
			long startTime = System.nanoTime();
			long estimatedTime = 0;
			if (i >= 51 && i <= 53) {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			}else if (i >= 57 && i <= 59) {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			} else {
				
				if (i % 3 == 0) {
					da[i] = 0;
				} else {
					da[i] = 1;
				}
			}
			if(da[i]==0){
				pin.low();
			}else{
				pin.high();
			}			
			while (estimatedTime < duration) {
				estimatedTime = System.nanoTime() - startTime;
			}
			all[i] = estimatedTime;
		}
		pin.low();
		for (int i = 0; i < all.length; i++) {
			System.out.println(i + ":" + all[i] + " :" + da[i]);
		}
		TimeUnit.MILLISECONDS.sleep(10);
	}
	
	public void runCycle3() throws Exception {
		long[] all = new long[72];
		byte[] da = new byte[72];
		for (int i = 0; i < all.length; i++) {
			long startTime = System.nanoTime();
			long estimatedTime = 0;
			if (i >= 48 && i <= 50) {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			}else if (i >= 57 && i <= 59) {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			}else if (i >= 63 && i <= 65) {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			} else {
				
				if (i % 3 == 0) {
					da[i] = 0;
				} else {
					da[i] = 1;
				}
			}
			if(da[i]==0){
				pin.low();
			}else{
				pin.high();
			}			
			while (estimatedTime < duration) {
				estimatedTime = System.nanoTime() - startTime;
			}
			all[i] = estimatedTime;
		}
		pin.low();
		for (int i = 0; i < all.length; i++) {
			System.out.println(i + ":" + all[i] + " :" + da[i]);
		}
		TimeUnit.MILLISECONDS.sleep(10);
	}
	
	public void runCycle2() throws Exception {
		long[] all = new long[72];
		byte[] da = new byte[72];
		for (int i = 0; i < all.length; i++) {
			long startTime = System.nanoTime();
			long estimatedTime = 0;
			if (i < 48 || i > 50) {
				if (i % 3 == 0) {
					da[i] = 0;
				} else {
					da[i] = 1;
				}
			} else {
				if (i % 3 == 2) {
					da[i] = 1;
				} else {
					da[i] = 0;
				}
			}
			if(da[i]==0){
				pin.low();
			}else{
				pin.high();
			}			
			while (estimatedTime < duration) {
				estimatedTime = System.nanoTime() - startTime;
			}
			all[i] = estimatedTime;
		}
		pin.low();
		for (int i = 0; i < all.length; i++) {
			System.out.println(i + ":" + all[i] + " :" + da[i]);
		}
		TimeUnit.MILLISECONDS.sleep(10);
	}
	

	
	public void stop() throws Exception {

		long[] all = new long[72];
		byte[] da = new byte[72];
		initByte(da);
		for (int i = 0; i < all.length; i++) {
			long startTime = System.nanoTime();
			long estimatedTime = 0;
			if (i > 47) {
				if (i < 54 || i > 56) {// 0
					if (i % 3 == 0) {
						da[i] = 0;
					} else {
						da[i] = 1;
					}
				} else {
					if (i % 3 == 2) {// 1
						da[i] = 1;
					} else {
						da[i] = 0;
					}
				}
			}
			if(da[i]==0){
				pin.low();
			}else{
				pin.high();
			}			
			while (estimatedTime < duration) {
				estimatedTime = System.nanoTime() - startTime;
			}
			all[i] = estimatedTime;
		}
		pin.low();
		for (int i = 0; i < all.length; i++) {
			System.out.println(i + ":" + all[i] + " :" + da[i]);
		}
		TimeUnit.MILLISECONDS.sleep(10);
	
	}
	
	private void initByte(byte[] da){
		if(da.length==72){
			da[0]=0;da[1]=0;da[2]=1; 
			da[3]=0;da[4]=0;da[5]=1;
			da[6]=0;da[7]=0;da[8]=1;
			da[9]=0;da[10]=0;da[11]=1;
			da[12]=0;da[13]=1;da[14]=1;
			da[15]=0;da[16]=0;da[17]=1;
			da[18]=0;da[19]=1;da[20]=1;
			da[21]=0;da[22]=1;da[23]=1;
			da[24]=0;da[25]=0;da[26]=1;
			da[27]=0;da[28]=0;da[29]=1;
			da[30]=0;da[31]=0;da[32]=1;
			da[33]=0;da[34]=0;da[35]=1;
			da[36]=0;da[37]=1;da[38]=1;
			da[39]=0;da[40]=1;da[41]=1;
			da[42]=0;da[43]=0;da[44]=1;
			da[45]=0;da[46]=1;da[47]=1;
		}		
	}
	
}
