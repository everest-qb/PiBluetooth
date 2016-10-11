import java.util.Iterator;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioBlinkStateTrigger;
import com.pi4j.io.gpio.trigger.GpioBlinkStopStateTrigger;
import com.pi4j.io.gpio.trigger.GpioInverseSyncStateTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;
import com.pi4j.util.Console;
import com.pi4j.wiringpi.GpioUtil;

public class Run {

	public static void main(String[] args) throws Exception {
		
		if(GpioUtil.isPrivilegedAccessRequired()){
            System.err.println("*****************************************************************");
            System.err.println("Privileged access is required on this system to access GPIO pins!");
            System.err.println("*****************************************************************");
            return;
        }
		
		final Console console = new Console();
		console.promptForExit();
		final GpioController gpio = GpioFactory.getInstance();
		//Platform platform= PlatformManager.getPlatform();
		/*Pin pins[] = RaspiPin.allPins();
		for(Pin pin:pins){
			System.out.println("----------------");
			System.out.println(pin.getName()+" / "+pin.getProvider()+" / "+pin.getAddress());
			System.out.println("Edge:"+pin.supportsPinEdges()+" Event:"+pin.supportsPinEvents()+" PullResistance:"+pin.supportsPinPullResistance());
			Iterator<PinEdge> eItor=pin.getSupportedPinEdges().iterator();
			System.out.println("--    --");
			while(eItor.hasNext()){
				PinEdge edge=eItor.next();
				System.out.println(edge.getName()+" / "+edge.getValue());
			}
			Iterator<PinMode> mItor=pin.getSupportedPinModes().iterator();
			System.out.println("--    --");
			while(mItor.hasNext()){
				PinMode mode=mItor.next();
				System.out.println(mode.getName()+" / "+mode.getValue());
			}
			
			Iterator<PinPullResistance> pItor=pin.getSupportedPinPullResistance().iterator();
			System.out.println("--    --");
			while(pItor.hasNext()){
				PinPullResistance ins=pItor.next();
				System.out.println(ins.getName()+" / "+ins.getValue());
			}
		}*/
		
		
		
		final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.LOW);
		final GpioPinDigitalInput pin2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, PinPullResistance.PULL_DOWN);
		pin.setShutdownOptions(true, PinState.LOW);
		pin2.setShutdownOptions(true, PinState.LOW);
		
		
		pin2.addTrigger(new GpioBlinkStateTrigger(PinState.HIGH, pin, 250));
		pin2.addTrigger(new GpioBlinkStopStateTrigger(PinState.LOW, pin));
		
		
		
      
		console.waitForExit();       
		gpio.shutdown();
		
		
	}

}
