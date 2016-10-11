import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.Position;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

public class SerialExample {
	   public static void main(String args[]) throws InterruptedException, IOException {

	        // !! ATTENTION !!
	        // By default, the serial port is configured as a console port
	        // for interacting with the Linux OS shell.  If you want to use
	        // the serial port in a software program, you must disable the
	        // OS from using this port.
	        //
	        // Please see this blog article for instructions on how to disable
	        // the OS console for this port:
	        // https://www.cube-controls.com/2015/11/02/disable-serial-port-terminal-output-on-raspbian/

	        // create Pi4J console wrapper/helper
	        // (This is a utility class to abstract some of the boilerplate code)
	        final Console console = new Console();

	        // print program title/header
	        console.title("<-- The Pi4J Project -->", "Serial Communication Example");

	        // allow for user to exit program using CTRL-C
	        console.promptForExit();

	        // create an instance of the serial communications class
	        final Serial serial = SerialFactory.createInstance();
	        final SentenceFactory sf = SentenceFactory.getInstance();	        	      	        
	        
	        // create and register the serial data listener
	        serial.addListener(new SerialDataEventListener() {
	        	
	        	private String line="";
	        	
	        	private void parse(String str){
	        		if(SentenceValidator.isSentence(str)){	                			
            			Sentence s = sf.createParser(str);
            			if(s.isValid())	    						
            				if (s.getTalkerId().toString().equals(TalkerId.GP.toString())
            						&& s.getSentenceId().toString().equals(SentenceId.RMC.toString())) {
            					RMCSentence rs = (RMCSentence) s;
            					Position p = rs.getPosition();
            					console.println(p.getLatitude() + "  " + p.getLongitude());
            				}
            		}
	        	}
	        	
	            @Override
	            public void dataReceived(SerialDataEvent event) {
	             
	                try {
	                    //console.println("[HEX DATA]   " + event.getHexByteString());
	                    //console.println("[ASCII DATA] " + event.getAsciiString());
	                	String tmpStr=event.getAsciiString();	                	
	                	if(line.contains("\r\n")){
	                		String[] data=line.split("\r\n");
	                			                		
	                		switch(data.length){
	                			case 1:	                				
	                				parse(data[0]);
	                				line="";
	                				break;
	                			case 2:	                				
	                				parse(data[0]);
	                				line=data[1];
	                				break;
	                			case 3:	                				
	                				parse(data[0]);
	                				parse(data[1]);	                				
	                				line=data[2];
	                				break;
	                			default:
	                				for(int i=0;i<data.length;i++){
	                					console.println("[ASCII DATA "+i+"] "+data[i]);
	                				}	                				
	                				line="";
	                		}
	                	}
	                	line+=tmpStr;
	                	
	                	
	                	
					if (false ) {//SentenceValidator.isSentence(tmpStr)
						Sentence s = sf.createParser(tmpStr);						
						console.println("Valid:" + s.isValid());
						console.println(s.getTalkerId()+"  "+s.getSentenceId());
						if (s.getTalkerId().equals(TalkerId.GP) && s.getSentenceId().equals(SentenceId.RMC)) {
							RMCSentence rs = (RMCSentence) s;
							Position p = rs.getPosition();
							console.println(p.getLatitude() + "  " + p.getLongitude());
						}
					}
	                	
	                	
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        });

	        try {
	            // create serial config object
	            SerialConfig config = new SerialConfig();

	            // set default serial settings (device, baud rate, flow control, etc)
	            //
	            // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
	            // NOTE: this utility method will determine the default serial port for the
	            //       detected platform and board/model.  For all Raspberry Pi models
	            //       except the 3B, it will return "/dev/ttyAMA0".  For Raspberry Pi
	            //       model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
	            //       environment configuration.
	            config.device("/dev/ttyUSB0")
	                  .baud(Baud._4800)	               
	                  .dataBits(DataBits._8)
	                  .parity(Parity.NONE)
	                  .stopBits(StopBits._1)
	                  .flowControl(FlowControl.NONE);

	            // parse optional command argument options to override the default serial settings.
	            if(args.length > 0){
	                config = CommandArgumentParser.getSerialConfig(config, args);
	            }

	            // display connection details
	            console.box(" Connecting to: " + config.toString(),
	                    " We are sending ASCII data on the serial port every 1 second.",
	                    " Data received on serial port will be displayed below.");


	            // open the default serial device/port with the configuration settings
	            serial.open(config);

	            // continuous loop to keep the program running until the user terminates the program
	            while(console.isRunning()) {
	                try {
	                    
	                    /*serial.write("CURRENT TIME: " + new Date().toString());

	                    
	                    serial.write((byte) 13);
	                    serial.write((byte) 10);

	                    
	                    serial.write("Second Line");

	                  
	                    serial.write('\r');
	                    serial.write('\n');

	                   
	                    serial.writeln("Third Line");
	                    */
	                }
	                catch(IllegalStateException ex){
	                    ex.printStackTrace();
	                }

	                // wait 1 second before continuing
	                Thread.sleep(1000);
	            }

	        }
	        catch(IOException ex) {
	            console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
	            return;
	        }
	    }
}
