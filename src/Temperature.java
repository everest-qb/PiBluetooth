import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

public class Temperature {

	private static final int MAXTIMINGS = 85;
	private int[] dht22_dat = { 0, 0, 0, 0, 0 };
	
	
	public Temperature() {
		if (Gpio.wiringPiSetup() == -1) {
	        System.out.println(" ==>> GPIO SETUP FAILED");
	        return;
	    }

	   GpioUtil.export(27, GpioUtil.DIRECTION_OUT); 
	   Gpio.pinMode(27, Gpio.OUTPUT);
	   Gpio.digitalWrite(27, Gpio.HIGH);
	}

	public void getTemperature() {
		   int laststate = Gpio.HIGH;
		   int j = 0;
		   dht22_dat[0] = dht22_dat[1] = dht22_dat[2] = dht22_dat[3] = dht22_dat[4] = 0;		 
		   
		   Gpio.pinMode(27, Gpio.OUTPUT);
		   Gpio.digitalWrite(27, Gpio.LOW);
		   Gpio.delay(18);//18ms
		  

		   Gpio.digitalWrite(27, Gpio.HIGH); 		  
		   //20~40us
		  
		   
		   Gpio.pinMode(27, Gpio.INPUT);

		   //low 80us  high 80us
		 
		   
		   //one bite =50ns low + if 26~28us high(0) or 70us high(1)
		   //  total 40 bit data 8 bit integral RH data+8 bit decimal RH data+8 bit integral T data+8 bit decimal T data
		   //+8 bit check-sum
		   //check-sum should be the last 8 bit of "8 bit integral RH data+8 bit decimal RH
		   //data+8 bit integral T data+8 bit decimal T data".
		   
		   //final 50us low and end
		   
		   
		   
		   for (int i = 0; i < MAXTIMINGS; i++) {
		      int counter = 0;
		      while (Gpio.digitalRead(27) == laststate) {
		          counter++;
		          Gpio.delayMicroseconds(1);
		          if (counter == 255) {
		              break;
		          }
		      }

		      laststate = Gpio.digitalRead(27);

		      if (counter == 255) {
		          break;
		      }

		    
		      
		      /* ignore first 3 transitions */
		      if ((i >= 4) && (i % 2 == 0)) {
		         /* shove each bit into the storage bytes */
		         dht22_dat[j / 8] <<= 1;
		         if (counter > 16) {
		             dht22_dat[j / 8] |= 1;
		         }
		         j++;
		       }
		    }
		
		    // check we read 40 bits (8bit x 5 ) + verify checksum in the last
		    // byte		    
		    if ((j >= 40) && checkParity()) {
		        float h = (float)((dht22_dat[0] << 8) + dht22_dat[1]) / 10;
		        if ( h > 100 )
		        {
		            h = dht22_dat[0];   // for DHT11
		        }
		        float c = (float)(((dht22_dat[2] & 0x7F) << 8) + dht22_dat[3]) / 10;
		        if ( c > 125 )
		        {
		            c = dht22_dat[2];   // for DHT11
		        }
		        if ( (dht22_dat[2] & 0x80) != 0 )
		        {
		            c = -c;
		        }
		        float f = c * 1.8f + 32;
		        System.out.println( "Humidity = " + h + " Temperature = " + c + "(" + f + "f)");
		    }else  {
		        System.out.println( "Data not good, skip" );
		    }

		}
	
	public void getTemperature2() {
		   int laststate = Gpio.HIGH;
		   int j = 0;
		   dht22_dat[0] = dht22_dat[1] = dht22_dat[2] = dht22_dat[3] = dht22_dat[4] = 0;

		   List<Long> list=new ArrayList<Long>();
		   
		   long p1=System.nanoTime();		   		  
		   
		   Gpio.digitalWrite(27, Gpio.LOW);
		   p1=check(list,p1);// 0
		   Gpio.delay(18);//18ms
		   p1=check(list,p1);// 1
		  		
		   Gpio.digitalWrite(27, Gpio.HIGH);   		   		  
		   //Gpio.delayMicroseconds(20);
		   //20~40us
		   //System.out.println("3: "+System.nanoTime());
		   p1=check(list,p1);// 2		   
		   Gpio.pinMode(27, Gpio.INPUT);		   
		   p1=check(list,p1);// 3
		   //int status=Gpio.digitalRead(27);		 
		   //p1=check(list,p1);// 4
		   //System.out.println("status: "+status);
		   
		   //low 80us  high 80us
		 /*if(list.get(1)+list.get(2)>120)
			 return ;
		   
		   int status=0;
		   while(System.nanoTime()-p1<5000000){			  
			   int check=Gpio.digitalRead(27);
			   if(status!=check){
				   p1=check(list,p1);// 5
				   status=check;
			   }
		   }*/
		 
		   
		   Gpio.pinMode(27, Gpio.OUTPUT);
		   Gpio.digitalWrite(27, Gpio.HIGH);
		   
		   
		   for(int k=0;k<list.size();k++){
			   System.out.print(list.get(k)+" ");
		   }
		   
		   System.out.println("");
		   
		   //one bite =50ns low + if 26~28us high(0) or 70us high(1)
		   //  total 40 bit data 8 bit integral RH data+8 bit decimal RH data+8 bit integral T data+8 bit decimal T data
		   //+8 bit check-sum
		   //check-sum should be the last 8 bit of "8 bit integral RH data+8 bit decimal RH
		   //data+8 bit integral T data+8 bit decimal T data".
		   
		   //final 50us low and end
		   
		   
		   
		   for (int i = 0; i < MAXTIMINGS; i++) {/*
		      int counter = 0;
		      while (Gpio.digitalRead(27) == laststate) {
		          counter++;
		          Gpio.delayMicroseconds(1);
		          if (counter == 255) {
		              break;
		          }
		      }

		      laststate = Gpio.digitalRead(27);

		      if (counter == 255) {
		          break;
		      }

		    
		      
		    
		      if ((i >= 4) && (i % 2 == 0)) {
		       
		         dht22_dat[j / 8] <<= 1;
		         if (counter > 16) {
		             dht22_dat[j / 8] |= 1;
		         }
		         j++;
		       }
		    */}
		
		    // check we read 40 bits (8bit x 5 ) + verify checksum in the last
		    // byte		    
		   /* if ((j >= 40) && checkParity()) {
		        float h = (float)((dht22_dat[0] << 8) + dht22_dat[1]) / 10;
		        if ( h > 100 )
		        {
		            h = dht22_dat[0];   // for DHT11
		        }
		        float c = (float)(((dht22_dat[2] & 0x7F) << 8) + dht22_dat[3]) / 10;
		        if ( c > 125 )
		        {
		            c = dht22_dat[2];   // for DHT11
		        }
		        if ( (dht22_dat[2] & 0x80) != 0 )
		        {
		            c = -c;
		        }
		        float f = c * 1.8f + 32;
		        System.out.println( "Humidity = " + h + " Temperature = " + c + "(" + f + "f)");
		    }else  {
		        System.out.println( "Data not good, skip" );
		    }*/

		}

		private boolean checkParity() {
		  return (dht22_dat[4] == ((dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3]) & 0xFF));
		}
	
		private long check(List<Long> list,long p1){
			long tmp=System.nanoTime();
			 list.add(tmp-p1);
			 return tmp;
		}
		
	public static void main(String[] args) throws Exception {

		Temperature dht = new Temperature();

		    for (int i=0; i<10; i++) {
		       Thread.sleep(5000);
		       dht.getTemperature2();
		    }

		    System.out.println("Done!!");

	}
}
