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
	
	
	
	public FrequencyRun() {
		super();
		gpio = GpioFactory.getInstance();
		pin =gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "MyLED",PinState.LOW);
		pin.setShutdownOptions(true, PinState.LOW); 
	}

	public static void main(String[] args) throws Exception {				 		 		 	
		FrequencyRun r=new FrequencyRun();
		for(int i=0;i<1000;i++)
			r.runCycle();
		r.shutdown();		
	}

	public void  shutdown(){
		if(gpio!=null)
			gpio.shutdown();
	}
	
	public void runCycle() throws Exception {
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
			while (estimatedTime < 339000) {
				estimatedTime = System.nanoTime() - startTime;
			}
			all[i] = estimatedTime;
		}
		pin.low();
		for (int i = 0; i < all.length; i++) {
			System.out.println(i + ":" + all[i] + " :" + da[i]);
		}
		TimeUnit.SECONDS.sleep(5);
	}
		
}
