import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.Console;

public class PwmTest {
	 final private Console console = new Console();
	 private  GpioPinDigitalOutput  pwm;
	 
	public static void main(String[] args) throws Exception {		 	
		 		
		PwmTest sound=new PwmTest();
		for(int i=1;i<7;i++){
			
			
			
			if (i % 2 == 0) {
				System.out.println("KZ:" + 100);
				sound.sound(100);
			}else{
				System.out.println("KZ:" + 3000);
				sound.sound(4000);
			}
		}
		 
		System.out.println("Press ENTER to set the PWM to a rate of 250");
        System.console().readLine();
		
		 /*pwm.setPwm(1000);
	        console.println("PWM rate is: " + pwm.getPwm());

	        console.println("Press ENTER to set the PWM to a rate of 250");
	        System.console().readLine();

		 
		 pwm.setPwm(500);
	        console.println("PWM rate is: " + pwm.getPwm());

	        console.println("Press ENTER to set the PWM to a rate of 250");
	        System.console().readLine();

	        // set the PWM rate to 250
	        pwm.setPwm(250);
	        console.println("PWM rate is: " + pwm.getPwm());


	        console.println("Press ENTER to set the PWM to a rate to 0 (stop PWM)");
	        System.console().readLine();

	        // set the PWM rate to 0
	        pwm.setPwm(0);
	        console.println("PWM rate is: " + pwm.getPwm());*/

	     
	}

	public PwmTest() {		
		 console.title("<-- The Pi4J Project -->", "PWM Example");
		 console.promptForExit();
		 GpioController gpio = GpioFactory.getInstance();
		 pwm =gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "SOUND");	
		 pwm.setShutdownOptions(true, PinState.LOW);
	}

	public void sound(int kz){//50 ~50000
		long startTime=System.currentTimeMillis();
		boolean one=true;
		while(System.currentTimeMillis()-startTime<800){
			if(one){
				pwm.high();
			}else{
				pwm.low();
			}
			one=!one;			
			try {
				TimeUnit.MICROSECONDS.sleep(kz);
			} catch (InterruptedException e) {
				
			}
		}
		
	}
	
}
