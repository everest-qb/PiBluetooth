import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.Console;

public class PwmTest {

	public static void main(String[] args) {
		 final Console console = new Console();
		 console.title("<-- The Pi4J Project -->", "PWM Example");
		 console.promptForExit();
		 GpioController gpio = GpioFactory.getInstance();
		 GpioPinPwmOutput  pwm =gpio.provisionPwmOutputPin(RaspiPin.GPIO_26, "PWN_LED", 0);
		 
		 pwm.setPwm(1000);
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
	        console.println("PWM rate is: " + pwm.getPwm());

	     
	}

}
