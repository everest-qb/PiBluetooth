package com.sunteam.sub1g.cc1201;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;

public class SpiRun {

	public static SpiDevice spi = null;
	public static  GpioController gpio;
	public static  GpioPinDigitalOutput pin;
	public static  GpioPinDigitalInput  pin04;
	public static   GpioPinDigitalOutput pin0;
	//public static  GpioPinDigitalInput  pin05;
	public static Object lock = new Object();
	
	public static boolean isTxDown=true;
	public static boolean isSample=false;
	
	static {
		try {
			SpiRun.spi = SpiFactory.getInstance(SpiChannel.CS0,
					SpiDevice.DEFAULT_SPI_SPEED*10, 
			        SpiMode.MODE_0);
			gpio = GpioFactory.getInstance();
			pin =gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "CS",PinState.HIGH);
			pin.setShutdownOptions(false, PinState.HIGH);
			pin04 =gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
			pin04.setShutdownOptions(true); 
			pin0 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "GET_MSG_LED", PinState.LOW);
			pin0.setShutdownOptions(true, PinState.LOW);
			/*pin05 =gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_UP);
			pin05.setShutdownOptions(true);*/ 
		} catch (IOException e) {
			
			e.printStackTrace();
		}	
	}

	protected static final Console console = new Console();
	
	public static void main(String[] args) throws Exception {
		String var;
		if(args.length>0){
			var=args[0].toLowerCase();
		}else{
			var="";
		}
		console.title("<-- The Pi4J Project -->", "SPI test");
		console.promptForExit();			
		console.println("VARIABLES:"+var);
		if("rx".equals(var))
		pin04.addListener(new GpioPinListenerDigital(){
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				long startTime=System.nanoTime();
				if(event.getEdge()==PinEdge.FALLING)
				try {
							synchronized (lock) {
								byte numRx = SpiRun.rExt((byte) 0xD7);
								//byte avableNumRx = SpiRun.rExt((byte) 0xD9);
								//console.println(Thread.currentThread().getName() + " Recive NU:" + numRx);
								
								if (numRx >= 23) {
									for(int i=0;i<(numRx/23);i++){
										byte[] data=SpiRun.srFIFO(21);

										byte lqi = (byte) (data[22] & 0x7f);
										byte[] id = Arrays.copyOfRange(data, 1,13);
										int hr = Arrays.copyOfRange(data, 13, 14)[0] & 0xff;
										int oxygen = Arrays.copyOfRange(data, 14, 15)[0] & 0xff;
										byte[] tem = Arrays.copyOfRange(data, 15, 17);
										int tempH = tem[0] & 0xff;
										int tempL = tem[1] & 0xff;
										String uuid = DatatypeConverter.printHexBinary(id);
												//new String(id, Charset.forName("US-ASCII"));
										float temperature = (tempH * 256 + tempL) / 100f;
										console.println(DatatypeConverter.printHexBinary(data));
										console.println("Result[" + uuid + "] => RSSI:" + data[21] + " LQI:" + lqi);
										console.println("HR:"+hr+" OXY:"+oxygen+" Temperature:"+temperature);										
										pin0.blink(50,100);
									}															
								}
							}
				} catch (Exception e) {					
					e.printStackTrace();
				}				
			}
			
		});
		
		if("tx".equals(var))
		pin04.addListener(new GpioPinListenerDigital(){

		@Override
		public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {	
			if(event.getEdge()==PinEdge.FALLING)
			try {								
				byte status=SpiRun.rExt((byte)0x94);
				console.println("Status:"+SpiRun.bytePrint(status)); 	
				console.println("TX down.");
				SpiRun.isTxDown=true;
			} catch (Exception e) {					
				e.printStackTrace();
			}
		}
		
	});

		/*pin05.addListener(new GpioPinListenerDigital(){

		@Override
		public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {	
			if(event.getEdge()==PinEdge.RISING)
			try {
				//byte status=SpiRun.rExt((byte)0x94);
				//console.println("Status:"+SpiRun.bytePrint(status)); 				
			} catch (Exception e) {					
				e.printStackTrace();
			}
		}
		
	});*/
		
		byte cmd=0;
		byte[] result=null;
		if(var.endsWith("init")){
		//reset
		SpiRun.reset();
		//register configure	
		
		//SpiRun.wReg((byte)0x00,(byte)0x07);//GPIO3		
		//SpiRun.wReg((byte)0x01,(byte)0x14);//GPIO2
		//SpiRun.wReg((byte)0x02,(byte)0x36);//SPI
		SpiRun.wReg((byte)0x03,(byte)0x06);//GPIO0
		//SpiRun.easyConfig();
		
		if("txinit".equals(var))
			SpiRun.Sample_50k_TXConfig();
		if("rxinit".equals(var))
			SpiRun.Sample_50k_RXConfig();
			
		TimeUnit.MILLISECONDS.sleep(1);
		}
		
		
		/*if("rx".equals(var)){			
			SpiRun.wReg((byte)0x01,(byte)0x01);//RXFIFO_OVERFLOW
			SpiRun.wReg((byte)0x1d,(byte)0xff);// CRC_AUTOFLUSH on ,FIFO_THR 22 =96
			SpiRun.wReg((byte)0x27,(byte)0x03);// CRC on,append status off  02; append status on 03 
			SpiRun.wReg((byte)0x28,(byte)0x00); //packet 組態成固定長度
			SpiRun.wReg((byte)0x29,(byte)0x7f);//RXOFF mode >RX  ,RX timeout is disable
			SpiRun.wReg((byte)0x2e,(byte)0x14); //packet 的長度(幾個byte) 0表示256,設為10
		}else if("tx".equals(var)){
			SpiRun.wReg((byte)0x01,(byte)0x05);//TXFIFO_UNDERFLOW
			SpiRun.wReg((byte)0x27,(byte)0x03);// CRC on,append status off on 02
			SpiRun.wReg((byte)0x28,(byte)0x00); //packet 組態成固定長度
			SpiRun.wReg((byte)0x2e,(byte)0x14); //packet 的長度(幾個byte) 0表示256,設為10
			//low power
			SpiRun.wReg((byte)0x2b,(byte)0x5f);
			SpiRun.wReg((byte)0x2c,(byte)0x56);
			SpiRun.wExt((byte)0x1a,(byte)0x02);
			SpiRun.wExt((byte)0x1c,(byte)0xf3);
			SpiRun.wExt((byte)0x1d,(byte)0x13);
			SpiRun.wExt((byte)0x27,(byte)0xb8);
			SpiRun.wExt((byte)0x36,(byte)0x01);
		}*/
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		
		for(int i=0;i<47;i++){		
			console.println("REG["+DatatypeConverter.printHexBinary(new byte[]{(byte)i})+"]:"+SpiRun.bytesPrint(new byte[] {SpiRun.rReg((byte)i)}));
		}
		
		
		//mode rx test	
		if ("rx".equals(var)) {			
				SpiRun.strobes(CC1201.STORE_SFRX);
				SpiRun.strobes(CC1201.STORE_SRX);
				for (int j = 0; j < 999999999; j++) {
					TimeUnit.SECONDS.sleep(1);
					byte check = SpiRun.rExt((byte) 0x73);
					//console.println("MARCSTATE:[" + DatatypeConverter.printHexBinary(new byte[] { check }) + "]");
					if (CC1201.MARCSTATE_RX_FIFO_ERR == check) {
						byte status = SpiRun.rExt((byte) 0x94);
						console.println("Status:" + SpiRun.bytePrint(status));
						SpiRun.strobes(CC1201.STORE_SFRX);
						SpiRun.strobes(CC1201.STORE_SRX);
					}
				}			
		}
		
		DecimalFormat format=new DecimalFormat("000000000000");
		SpiRun.isSample=true;
		//mod tx test
		if ("tx".equals(var)) {
			if (!SpiRun.isSample) {
				SpiRun.strobes(CC1201.STORE_SFTX);
				for (int i = 0; i < 10; i++) {
					byte[] data = new byte[20];
					byte[] uuid = format.format(i).getBytes("US-ASCII");
					for (int j = 0; j < 12; j++)
						data[j] = uuid[j];

					Random r = new Random();
					byte[] gen = new byte[4];
					r.nextBytes(gen);
					data[12] = gen[0];
					data[13] = gen[1];
					data[14] = gen[2];
					data[15] = gen[3];
					for (int j = 16; j < 20; j++)
						data[j] = 0;
					SpiRun.swFIFO(data);
					SpiRun.isTxDown = false;
					SpiRun.strobes(CC1201.STORE_STX);
					long beginT = System.nanoTime();// &&
													// (System.nanoTime()-beginT)<100000
					while (!SpiRun.isTxDown) {
						TimeUnit.MILLISECONDS.sleep(100);// 20 ms 50k 12m; 10ms
															// 100k
															// 6m
					}
				}
				TimeUnit.SECONDS.sleep(20);
			}else{
				console.println("sample tx");	
				SpiRun.strobes(CC1201.STORE_SFTX);
				for (int i = 0; i < 50; i++) {																				
					byte[] data = new byte[21];
					byte[] uuid = format.format(i).getBytes("US-ASCII");
					data[0]=(byte)0xdb;
					for (int j = 1; j < 13; j++)
						data[j] = uuid[j-1];

					Random r = new Random();
					byte[] gen = new byte[4];
					r.nextBytes(gen);
					data[13] = gen[0];
					data[14] = gen[1];
					data[15] = gen[2];
					data[16] = gen[3];
					for (int j = 17; j < 21; j++)
						data[j] = 0;					
					SpiRun.swFIFO(data);
					SpiRun.isTxDown = false;
					SpiRun.strobes(CC1201.STORE_STX);
					long beginT = System.nanoTime();// &&
													// (System.nanoTime()-beginT)<100000					
					while (!SpiRun.isTxDown) {
						TimeUnit.MILLISECONDS.sleep(200);// 20 ms 50k 12m; 10ms  100k  6m
					}					
				}
				TimeUnit.SECONDS.sleep(5);
			
			}
		}
		
				
		SpiRun.strobes(CC1201.STORE_SIDLE);	
		TimeUnit.MILLISECONDS.sleep(1);	
		console.println("Final MARCSTATE:[" + DatatypeConverter.printHexBinary(new byte[]{SpiRun.rExt((byte)0x73)})+"]");		
		gpio.shutdown();		
	}
		

	
	
	
	
	
	
	
	public static void reset() throws Exception{
		SpiRun.ceBegin();
		int counter=0;	
		byte cmd=(byte)(CC1201.WRITE |0x30);				
		byte[] result =spi.write(cmd);
		while(!SpiRun.chpRDYn(result[0]) && counter<1000){			
			result =spi.write(cmd);
			TimeUnit.MILLISECONDS.sleep(1);	
			counter++;
		}
		console.println("RESET:"+SpiRun.bytesPrint(result));	
		
		TimeUnit.MILLISECONDS.sleep(12);		
		SpiRun.ceEnd();
	}
	
	public static byte[] srFIFO(int count) throws Exception{
		SpiRun.ceBegin();
		byte[] cmd=new byte[count+3];// cmd(1 byte) + append status(2 byte)
		Arrays.fill(cmd, (byte)0x00);
		cmd[0]=(byte)(CC1201.READ_BURST|CC1201.ADDR_FIFO);
		byte[] result =spi.write(cmd);
		//console.println("RFIFO:"+SpiRun.bytesPrint(result));
		SpiRun.ceEnd();
		return Arrays.copyOfRange(result, 1, count+3);
	}
	
	public static void swFIFO(byte[] data)throws Exception{
		SpiRun.ceBegin();
		byte[] cmd=new byte[data.length+1];
		for(int i=0;i<cmd.length;i++){
			if(i==0){
				cmd[0]=(byte)(CC1201.WRITE_BURST|CC1201.ADDR_FIFO);
			}else{
				cmd[i]=data[i-1];
			}
		}
		byte[] result =spi.write(cmd);
		//console.println("WFIFO:"+SpiRun.bytesPrint(result));	
		SpiRun.ceEnd();
	}
	
	public static void strobes(byte b) throws Exception{
		SpiRun.ceBegin();
		int counter=0;	
		byte cmd=(byte)(CC1201.WRITE |b);				
		byte[] result =spi.write(cmd);
		SpiRun.ceEnd();
		while(!SpiRun.chpRDYn(result[0]) && counter<1000){	
			randomPass();
			SpiRun.ceBegin();
			result =spi.write(cmd);				
			counter++;
			SpiRun.ceEnd();
		}
		//console.println("STORBES["+DatatypeConverter.printHexBinary(new byte[]{b})+"]:"+SpiRun.bytesPrint(result));			
	}
	
	public static byte rExt(byte address) throws Exception{
		SpiRun.ceBegin();
		int counter=0;	
		byte[] cmd=new byte[] {(byte)(CC1201.READ|CC1201.ADDR_EXTEND),address,(byte)0x00};
		byte[] result =spi.write(cmd);
		SpiRun.ceEnd();
		while((!SpiRun.chpRDYn(result[0]) || !SpiRun.chpRDYn(result[1])) && counter<1000){
			randomPass();
			SpiRun.ceBegin();
			result =spi.write(cmd);
			counter++;
			SpiRun.ceEnd();
		}
		//console.println("RER["+address+"]:"+SpiRun.bytesPrint(result));
		
		return result[2];
	}
	
	public static void wExt(byte address,byte data) throws Exception{
		SpiRun.ceBegin();
		int counter=0;	
		byte[] extCmd=new byte[]{CC1201.ADDR_EXTEND,address,data};
		byte[] result =spi.write(extCmd);	
		SpiRun.ceEnd();
		while((!SpiRun.chpRDYn(result[0]) || !SpiRun.chpRDYn(result[1]) || !SpiRun.chpRDYn(result[2])) && counter<1000){
			randomPass();
			SpiRun.ceBegin();
			result =spi.write(extCmd);
			counter++;
			SpiRun.ceEnd();
		}
		//console.println("WER["+DatatypeConverter.printHexBinary(new byte[]{address})+"]:"+SpiRun.bytesPrint(result));	
		TimeUnit.MILLISECONDS.sleep(1);
	}
	
	public static byte rReg(byte address) throws Exception{
		SpiRun.ceBegin();
		int counter=0;
		byte cmd=(byte)(CC1201.READ|address);
		byte[] result =spi.write(cmd,(byte)0x00);
		SpiRun.ceEnd();
		while(!SpiRun.chpRDYn(result[0]) && counter<1000){
			randomPass();
			SpiRun.ceBegin();
			result =spi.write(cmd,(byte)0x00);
			counter++;
			SpiRun.ceEnd();
		}
		return result[1];
	}
	
	public static void wReg(byte address,byte data) throws Exception{
		SpiRun.ceBegin();
		int counter=0;
		byte[] cmd=new byte[]{address,data};
		byte[] result=spi.write(cmd);
		
		SpiRun.ceEnd();
		while((!SpiRun.chpRDYn(result[0]) || !SpiRun.chpRDYn(result[1])) && counter<1000){
			randomPass();
			SpiRun.ceBegin();
			result=spi.write(cmd);			
			counter++;
			SpiRun.ceEnd();
		}
		//console.println("WR["+DatatypeConverter.printHexBinary(new byte[]{address})+"]:"+SpiRun.bytesPrint(result));
		TimeUnit.MILLISECONDS.sleep(1);
	}
	
	public static void randomPass() throws Exception{
		Random r=new Random();
		int test=r.nextInt(10);
		TimeUnit.MILLISECONDS.sleep(test);
	}
	
	public static void ceBegin(){
		/*SpiRun.pin.low();
		long startTime = System.nanoTime();
		long estimatedTime = 0;		
		while (estimatedTime < 100) {
			estimatedTime = System.nanoTime() - startTime;
		}*/		
	}
	
	public static void ceEnd(){
		/*long startTime = System.nanoTime();
		long estimatedTime = 0;		
		while (estimatedTime < 100) {
			estimatedTime = System.nanoTime() - startTime;
		}		
		SpiRun.pin.high();*/
	}
	
	public static boolean chpRDYn(byte b){
		if(((b&0xff) >>7)==1 ){
			return false;
		}else{
			return true;
		}
	}
	
	public static String bytePrint(byte b){
		
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	}
	
	public static String bytesPrint(byte[] bs){
		String returnValue="";
		int i=1;	
		for(byte b:bs){						
			returnValue+=String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
			returnValue+=" ";
		}
		return returnValue;
	}
	
	public static void Sample_50k_TXConfig() throws Exception{
		SpiRun.isSample=true;
		SpiRun.wReg((byte)0x04,(byte)0x45);
		SpiRun.wReg((byte)0x05,(byte)0x52);
		SpiRun.wReg((byte)0x06,(byte)0x53);
		SpiRun.wReg((byte)0x07,(byte)0x54);
		SpiRun.wReg((byte)0x08,(byte)0xa9);
		SpiRun.wReg((byte)0x0b,(byte)0x0b);
		SpiRun.wReg((byte)0x0e,(byte)0x8a);
		SpiRun.wReg((byte)0x0f,(byte)0xc8);		
		SpiRun.wReg((byte)0x10,(byte)0x10);
		SpiRun.wReg((byte)0x11,(byte)0x40);
		SpiRun.wReg((byte)0x12,(byte)0x05);
		SpiRun.wReg((byte)0x13,(byte)0x94);
		SpiRun.wReg((byte)0x14,(byte)0x7a);
		SpiRun.wReg((byte)0x15,(byte)0xe1);
		SpiRun.wReg((byte)0x16,(byte)0x27);
		SpiRun.wReg((byte)0x17,(byte)0xee);		
		SpiRun.wReg((byte)0x1b,(byte)0x11);		
		SpiRun.wReg((byte)0x1c,(byte)0x94);		
		SpiRun.wReg((byte)0x1d,(byte)0x80);	//crc drop
		SpiRun.wReg((byte)0x1e,(byte)0xdb);// address
		SpiRun.wReg((byte)0x20,(byte)0x14);		
		SpiRun.wReg((byte)0x26,(byte)0x00);
		SpiRun.wReg((byte)0x27,(byte)0x0b);//addree filter type
		SpiRun.wReg((byte)0x2b,(byte)0x5f);
		SpiRun.wReg((byte)0x2e,(byte)0x15);//  fixed packet length
		
		
		SpiRun.wExt((byte)0x00,(byte)0x1c);
		SpiRun.wExt((byte)0x01,(byte)0x22);
		SpiRun.wExt((byte)0x02,(byte)0x03);
		SpiRun.wExt((byte)0x05,(byte)0x02);
		SpiRun.wExt((byte)0x0c,(byte)0x56);
		SpiRun.wExt((byte)0x0d,(byte)0x99);
		SpiRun.wExt((byte)0x0e,(byte)0x99);
		SpiRun.wExt((byte)0x10,(byte)0xee);
		SpiRun.wExt((byte)0x11,(byte)0x10);
		SpiRun.wExt((byte)0x12,(byte)0x07);		
		SpiRun.wExt((byte)0x13,(byte)0xa5);		
		SpiRun.wExt((byte)0x16,(byte)0x40);
		SpiRun.wExt((byte)0x17,(byte)0x0e);
		SpiRun.wExt((byte)0x19,(byte)0x03);
		SpiRun.wExt((byte)0x1a,(byte)0x02);
		SpiRun.wExt((byte)0x1b,(byte)0x33);
		SpiRun.wExt((byte)0x1c,(byte)0xf3);
		SpiRun.wExt((byte)0x1d,(byte)0x13);		
		SpiRun.wExt((byte)0x1f,(byte)0x00);
		SpiRun.wExt((byte)0x20,(byte)0x6e);
		SpiRun.wExt((byte)0x21,(byte)0x1c);
		SpiRun.wExt((byte)0x22,(byte)0xac);
		SpiRun.wExt((byte)0x27,(byte)0xb8);
		SpiRun.wExt((byte)0x2f,(byte)0x09);
		SpiRun.wExt((byte)0x32,(byte)0x0e);				
	}
	
	public static void Sample_50k_RXConfig() throws Exception{
		wReg((byte)0x04,(byte)0x45);
		wReg((byte)0x05,(byte)0x52);
		wReg((byte)0x06,(byte)0x53);
		wReg((byte)0x07,(byte)0x54);
		wReg((byte)0x08,(byte)0xa9);
		wReg((byte)0x0b,(byte)0x0b);
		wReg((byte)0x0e,(byte)0x8a);
		wReg((byte)0x0f,(byte)0xc8);		
		wReg((byte)0x10,(byte)0x10);
		wReg((byte)0x11,(byte)0x42);
		wReg((byte)0x12,(byte)0x05);
		wReg((byte)0x13,(byte)0x94);
		wReg((byte)0x14,(byte)0x7a);
		wReg((byte)0x15,(byte)0xe1);
		wReg((byte)0x16,(byte)0x27);
		wReg((byte)0x17,(byte)0xee);		
		wReg((byte)0x1b,(byte)0x11);		
		wReg((byte)0x1c,(byte)0x94);				
		wReg((byte)0x1d,(byte)0xff);//crc drop
		wReg((byte)0x1e,(byte)0xdb);// address		
		wReg((byte)0x20,(byte)0x14);		
		wReg((byte)0x26,(byte)0x00);
		wReg((byte)0x27,(byte)0x0b);//addree filter type
		wReg((byte)0x29,(byte)0x3f);//RXOFF_MODE
		wReg((byte)0x2b,(byte)0x5f);//PA power ramp target level
		wReg((byte)0x2e,(byte)0x15);//  fixed packet length
				
		wExt((byte)0x00,(byte)0x1c);		
		wExt((byte)0x02,(byte)0x03);
		wExt((byte)0x05,(byte)0x02);
		wExt((byte)0x0c,(byte)0x56);
		wExt((byte)0x0d,(byte)0x99);
		wExt((byte)0x0e,(byte)0x99);
		wExt((byte)0x10,(byte)0xee);
		wExt((byte)0x11,(byte)0x10);
		wExt((byte)0x12,(byte)0x07);		
		wExt((byte)0x13,(byte)0xa5);		
		wExt((byte)0x16,(byte)0x40);
		wExt((byte)0x17,(byte)0x0e);
		wExt((byte)0x19,(byte)0x03);
		wExt((byte)0x1b,(byte)0x33);		
		wExt((byte)0x1d,(byte)0x17);		
		wExt((byte)0x1f,(byte)0x00);
		wExt((byte)0x20,(byte)0x6e);
		wExt((byte)0x21,(byte)0x1c);
		wExt((byte)0x22,(byte)0xac);
		wExt((byte)0x27,(byte)0xb5);
		wExt((byte)0x2f,(byte)0x09);
		wExt((byte)0x32,(byte)0x0e);		
		wExt((byte)0x36,(byte)0x03);
	}
	
	/*public static void Sample_50k_RXConfig() throws Exception{
		SpiRun.isSample=true;
		SpiRun.wReg((byte)0x04,(byte)0x45);
		SpiRun.wReg((byte)0x05,(byte)0x52);
		SpiRun.wReg((byte)0x06,(byte)0x53);
		SpiRun.wReg((byte)0x07,(byte)0x54);
		SpiRun.wReg((byte)0x08,(byte)0xa9);
		SpiRun.wReg((byte)0x0b,(byte)0x0b);
		SpiRun.wReg((byte)0x0e,(byte)0x8a);
		SpiRun.wReg((byte)0x0f,(byte)0xc8);
		SpiRun.wReg((byte)0x0c,(byte)0x4b);
		SpiRun.wReg((byte)0x10,(byte)0x10);
		SpiRun.wReg((byte)0x11,(byte)0x42);
		SpiRun.wReg((byte)0x12,(byte)0x05);
		SpiRun.wReg((byte)0x13,(byte)0x94);
		SpiRun.wReg((byte)0x14,(byte)0x7a);
		SpiRun.wReg((byte)0x15,(byte)0xe1);
		SpiRun.wReg((byte)0x16,(byte)0x27);
		SpiRun.wReg((byte)0x17,(byte)0xee);		
		SpiRun.wReg((byte)0x1b,(byte)0x11);		
		SpiRun.wReg((byte)0x1c,(byte)0x94);		
		SpiRun.wReg((byte)0x1d,(byte)0x80);	//crc drop
		SpiRun.wReg((byte)0x1e,(byte)0xdb);// address
		SpiRun.wReg((byte)0x20,(byte)0x12);		
		SpiRun.wReg((byte)0x26,(byte)0x00);	
		SpiRun.wReg((byte)0x27,(byte)0x0b);//addree filter type
		SpiRun.wReg((byte)0x29,(byte)0x7f);
		SpiRun.wReg((byte)0x2c,(byte)0x53);
		SpiRun.wReg((byte)0x2e,(byte)0x15);//  fixed packet length
		
		
		SpiRun.wExt((byte)0x00,(byte)0x1c);		
		SpiRun.wExt((byte)0x02,(byte)0x03);
		SpiRun.wExt((byte)0x05,(byte)0x02);
		SpiRun.wExt((byte)0x0c,(byte)0x56);
		SpiRun.wExt((byte)0x0d,(byte)0xcc);
		SpiRun.wExt((byte)0x0e,(byte)0xcc);
		SpiRun.wExt((byte)0x10,(byte)0xee);
		SpiRun.wExt((byte)0x11,(byte)0x10);
		SpiRun.wExt((byte)0x12,(byte)0x04);		
		SpiRun.wExt((byte)0x13,(byte)0x50);		
		SpiRun.wExt((byte)0x16,(byte)0x40);
		SpiRun.wExt((byte)0x17,(byte)0x0e);
		SpiRun.wExt((byte)0x19,(byte)0x03);
		SpiRun.wExt((byte)0x1b,(byte)0x33);
		SpiRun.wExt((byte)0x1c,(byte)0xf7);
		SpiRun.wExt((byte)0x1d,(byte)0x0f);		
		SpiRun.wExt((byte)0x1f,(byte)0x00);
		SpiRun.wExt((byte)0x20,(byte)0x6e);
		SpiRun.wExt((byte)0x21,(byte)0x1c);
		SpiRun.wExt((byte)0x22,(byte)0xac);
		SpiRun.wExt((byte)0x27,(byte)0xb5);
		SpiRun.wExt((byte)0x2f,(byte)0x09);
		SpiRun.wExt((byte)0x32,(byte)0x0e);		
		SpiRun.wExt((byte)0x36,(byte)0x03);
	}*/
	
	public static void Etsi_434_100kRXConfig() throws Exception{
		SpiRun.isSample=false;
		SpiRun.wReg((byte)0x08,(byte)0xa8);
		SpiRun.wReg((byte)0x09,(byte)0x23);
		SpiRun.wReg((byte)0x0a,(byte)0x47);
		SpiRun.wReg((byte)0x0b,(byte)0x0c);
		SpiRun.wReg((byte)0x0c,(byte)0x4b);
		SpiRun.wReg((byte)0x0e,(byte)0x8a);
		SpiRun.wReg((byte)0x0f,(byte)0xd8);
		SpiRun.wReg((byte)0x10,(byte)0x08);
		SpiRun.wReg((byte)0x11,(byte)0x42);
		SpiRun.wReg((byte)0x12,(byte)0x05);
		SpiRun.wReg((byte)0x13,(byte)0xa4);
		SpiRun.wReg((byte)0x14,(byte)0x7a);
		SpiRun.wReg((byte)0x15,(byte)0xe1);
		SpiRun.wReg((byte)0x16,(byte)0x2a);
		SpiRun.wReg((byte)0x17,(byte)0xf6);
		SpiRun.wReg((byte)0x1b,(byte)0x12);
		SpiRun.wReg((byte)0x1c,(byte)0x80);
		SpiRun.wReg((byte)0x1d,(byte)0x80);//00
		SpiRun.wReg((byte)0x20,(byte)0x14);		
		SpiRun.wReg((byte)0x26,(byte)0x00);	
		SpiRun.wReg((byte)0x29,(byte)0x7f);//RXOFF mode
		SpiRun.wReg((byte)0x2e,(byte)0x14);
		
		SpiRun.wExt((byte)0x00,(byte)0x1c);		
		SpiRun.wExt((byte)0x02,(byte)0x03);
		SpiRun.wExt((byte)0x05,(byte)0x02);
		SpiRun.wExt((byte)0x0c,(byte)0x56);
		SpiRun.wExt((byte)0x0d,(byte)0xcc);
		SpiRun.wExt((byte)0x0e,(byte)0xcc);
		SpiRun.wExt((byte)0x10,(byte)0xee);
		SpiRun.wExt((byte)0x11,(byte)0x10);
		SpiRun.wExt((byte)0x12,(byte)0x07);
		SpiRun.wExt((byte)0x13,(byte)0xa5);
		SpiRun.wExt((byte)0x16,(byte)0x40);
		SpiRun.wExt((byte)0x17,(byte)0x0e);
		SpiRun.wExt((byte)0x19,(byte)0x03);
		SpiRun.wExt((byte)0x1b,(byte)0x33);
		SpiRun.wExt((byte)0x1d,(byte)0x17);
		SpiRun.wExt((byte)0x1f,(byte)0x00);
		SpiRun.wExt((byte)0x20,(byte)0x6e);
		SpiRun.wExt((byte)0x21,(byte)0x1c);
		SpiRun.wExt((byte)0x22,(byte)0xac);
		SpiRun.wExt((byte)0x27,(byte)0xb5);
		SpiRun.wExt((byte)0x2f,(byte)0x09);
		SpiRun.wExt((byte)0x32,(byte)0x0e);		
		SpiRun.wExt((byte)0x36,(byte)0x03);
	}
	
	public static void Etsi_434_100kTXConfig() throws Exception{	
		SpiRun.isSample=false;
		SpiRun.wReg((byte)0x08,(byte)0xa8);
		SpiRun.wReg((byte)0x09,(byte)0x23);
		SpiRun.wReg((byte)0x0a,(byte)0x47);
		SpiRun.wReg((byte)0x0b,(byte)0x0c);
		SpiRun.wReg((byte)0x0c,(byte)0x4b);
		SpiRun.wReg((byte)0x0e,(byte)0x8a);
		SpiRun.wReg((byte)0x0f,(byte)0xd8);
		SpiRun.wReg((byte)0x10,(byte)0x08);
		SpiRun.wReg((byte)0x11,(byte)0x42);
		SpiRun.wReg((byte)0x12,(byte)0x05);
		SpiRun.wReg((byte)0x13,(byte)0xa4);
		SpiRun.wReg((byte)0x14,(byte)0x7a);
		SpiRun.wReg((byte)0x15,(byte)0xe1);
		SpiRun.wReg((byte)0x16,(byte)0x2a);
		SpiRun.wReg((byte)0x17,(byte)0xf6);
		SpiRun.wReg((byte)0x1b,(byte)0x12);
		SpiRun.wReg((byte)0x1c,(byte)0x80);
		SpiRun.wReg((byte)0x1d,(byte)0x80);//00
		SpiRun.wReg((byte)0x20,(byte)0x14);		
		SpiRun.wReg((byte)0x26,(byte)0x00);
		SpiRun.wReg((byte)0x2b,(byte)0x5f);
		SpiRun.wReg((byte)0x2e,(byte)0x14);
		
		SpiRun.wExt((byte)0x00,(byte)0x1c);		
		SpiRun.wExt((byte)0x02,(byte)0x03);
		SpiRun.wExt((byte)0x05,(byte)0x02);
		SpiRun.wExt((byte)0x0c,(byte)0x56);
		SpiRun.wExt((byte)0x0d,(byte)0xcc);
		SpiRun.wExt((byte)0x0e,(byte)0xcc);
		SpiRun.wExt((byte)0x10,(byte)0xee);
		SpiRun.wExt((byte)0x11,(byte)0x10);
		SpiRun.wExt((byte)0x12,(byte)0x07);
		SpiRun.wExt((byte)0x13,(byte)0x50);
		SpiRun.wExt((byte)0x16,(byte)0x40);
		SpiRun.wExt((byte)0x17,(byte)0x0e);
		SpiRun.wExt((byte)0x19,(byte)0x03);
		SpiRun.wExt((byte)0x1a,(byte)0x02);
		SpiRun.wExt((byte)0x1b,(byte)0x33);
		SpiRun.wExt((byte)0x1c,(byte)0xf3);
		SpiRun.wExt((byte)0x1d,(byte)0x13);
		SpiRun.wExt((byte)0x1f,(byte)0x00);
		SpiRun.wExt((byte)0x20,(byte)0x6e);
		SpiRun.wExt((byte)0x21,(byte)0x1c);
		SpiRun.wExt((byte)0x22,(byte)0xac);
		SpiRun.wExt((byte)0x27,(byte)0xb8);
		SpiRun.wExt((byte)0x2f,(byte)0x09);
		SpiRun.wExt((byte)0x32,(byte)0x0e);				
	}
	
	
	public static void Etsi100kRXConfig() throws Exception{	
		SpiRun.isSample=false;
		SpiRun.wReg((byte)0x08,(byte)0xa8);
		SpiRun.wReg((byte)0x09,(byte)0x23);
		SpiRun.wReg((byte)0x0a,(byte)0x47);
		SpiRun.wReg((byte)0x0b,(byte)0x0c);
		SpiRun.wReg((byte)0x0c,(byte)0x2b);
		SpiRun.wReg((byte)0x0e,(byte)0x8a);
		SpiRun.wReg((byte)0x0f,(byte)0xd8);
		SpiRun.wReg((byte)0x10,(byte)0x08);
		SpiRun.wReg((byte)0x11,(byte)0x42);
		SpiRun.wReg((byte)0x12,(byte)0x05);
		SpiRun.wReg((byte)0x13,(byte)0xa4);
		SpiRun.wReg((byte)0x14,(byte)0x7a);
		SpiRun.wReg((byte)0x15,(byte)0xe1);
		SpiRun.wReg((byte)0x16,(byte)0x2a);
		SpiRun.wReg((byte)0x17,(byte)0xf6);
		SpiRun.wReg((byte)0x1b,(byte)0x12);
		SpiRun.wReg((byte)0x1c,(byte)0x80);
		SpiRun.wReg((byte)0x1d,(byte)0x80);//00
		SpiRun.wReg((byte)0x20,(byte)0x12);		
		SpiRun.wReg((byte)0x26,(byte)0x00);
		SpiRun.wReg((byte)0x29,(byte)0x7f);//RXOFF mode
		SpiRun.wReg((byte)0x2e,(byte)0x14);
		
		SpiRun.wExt((byte)0x00,(byte)0x1c);		
		SpiRun.wExt((byte)0x02,(byte)0x03);
		SpiRun.wExt((byte)0x05,(byte)0x02);
		SpiRun.wExt((byte)0x0c,(byte)0x56);
		SpiRun.wExt((byte)0x0d,(byte)0xcc);
		SpiRun.wExt((byte)0x0e,(byte)0xcc); 
		SpiRun.wExt((byte)0x10,(byte)0xee);
		SpiRun.wExt((byte)0x11,(byte)0x10);
		SpiRun.wExt((byte)0x12,(byte)0x07);
		SpiRun.wExt((byte)0x13,(byte)0xa5);
		SpiRun.wExt((byte)0x16,(byte)0x40);
		SpiRun.wExt((byte)0x17,(byte)0x0e);
		SpiRun.wExt((byte)0x19,(byte)0x03);
		SpiRun.wExt((byte)0x1b,(byte)0x33);
		SpiRun.wExt((byte)0x1d,(byte)0x17);
		SpiRun.wExt((byte)0x1f,(byte)0x00);
		SpiRun.wExt((byte)0x20,(byte)0x6e);
		SpiRun.wExt((byte)0x21,(byte)0x1c);
		SpiRun.wExt((byte)0x22,(byte)0xac);
		SpiRun.wExt((byte)0x27,(byte)0xb5);
		SpiRun.wExt((byte)0x2f,(byte)0x09);
		SpiRun.wExt((byte)0x32,(byte)0x0e);		
		SpiRun.wExt((byte)0x36,(byte)0x03);
	}
	
	public static void Etsi100kTXConfig() throws Exception{
		SpiRun.isSample=false;
		SpiRun.wReg((byte)0x08,(byte)0xa8);
		SpiRun.wReg((byte)0x09,(byte)0x23);
		SpiRun.wReg((byte)0x0a,(byte)0x47);
		SpiRun.wReg((byte)0x0b,(byte)0x0c);
		SpiRun.wReg((byte)0x0c,(byte)0x4b);
		SpiRun.wReg((byte)0x0e,(byte)0x8a);
		SpiRun.wReg((byte)0x0f,(byte)0xd8);
		SpiRun.wReg((byte)0x10,(byte)0x08);
		SpiRun.wReg((byte)0x11,(byte)0x42);
		SpiRun.wReg((byte)0x12,(byte)0x05);
		SpiRun.wReg((byte)0x13,(byte)0xa4);
		SpiRun.wReg((byte)0x14,(byte)0x7a);
		SpiRun.wReg((byte)0x15,(byte)0xe1);
		SpiRun.wReg((byte)0x16,(byte)0x2a);
		SpiRun.wReg((byte)0x17,(byte)0xf6);
		SpiRun.wReg((byte)0x1b,(byte)0x12);
		SpiRun.wReg((byte)0x1c,(byte)0x80);
		SpiRun.wReg((byte)0x1d,(byte)0x80);//00
		SpiRun.wReg((byte)0x20,(byte)0x12);		
		SpiRun.wReg((byte)0x26,(byte)0x00);
		SpiRun.wReg((byte)0x2b,(byte)0x5f);
		SpiRun.wReg((byte)0x2e,(byte)0x14);
		
		SpiRun.wExt((byte)0x00,(byte)0x1c);		
		SpiRun.wExt((byte)0x02,(byte)0x03);
		SpiRun.wExt((byte)0x05,(byte)0x02);
		SpiRun.wExt((byte)0x0c,(byte)0x56);
		SpiRun.wExt((byte)0x0d,(byte)0xcc);
		SpiRun.wExt((byte)0x0e,(byte)0xcc);
		SpiRun.wExt((byte)0x10,(byte)0xee);
		SpiRun.wExt((byte)0x11,(byte)0x10);
		SpiRun.wExt((byte)0x12,(byte)0x07);
		SpiRun.wExt((byte)0x13,(byte)0x50);
		SpiRun.wExt((byte)0x16,(byte)0x40);
		SpiRun.wExt((byte)0x17,(byte)0x0e);
		SpiRun.wExt((byte)0x19,(byte)0x03);
		SpiRun.wExt((byte)0x1a,(byte)0x02);
		SpiRun.wExt((byte)0x1b,(byte)0x33);
		SpiRun.wExt((byte)0x1c,(byte)0xf3);
		SpiRun.wExt((byte)0x1d,(byte)0x13);
		SpiRun.wExt((byte)0x1f,(byte)0x00);
		SpiRun.wExt((byte)0x20,(byte)0x6e);
		SpiRun.wExt((byte)0x21,(byte)0x1c);
		SpiRun.wExt((byte)0x22,(byte)0xac);
		SpiRun.wExt((byte)0x27,(byte)0xb8);
		SpiRun.wExt((byte)0x2f,(byte)0x09);
		SpiRun.wExt((byte)0x32,(byte)0x0e);				
	}
	
	public static void easyConfig() throws Exception{	
		SpiRun.isSample=false;
		SpiRun.wReg((byte)0x04,(byte)0x55);//sync word
		SpiRun.wReg((byte)0x05,(byte)0x55);//sync word
		SpiRun.wReg((byte)0x06,(byte)0x7a);//sync word
		SpiRun.wReg((byte)0x07,(byte)0x0e);//sync word
		SpiRun.wReg((byte)0x08,(byte)0x48);
		SpiRun.wReg((byte)0x09,(byte)0x3b);//SpiRun.wReg((byte)0x09,(byte)0x23);
		SpiRun.wReg((byte)0x0a,(byte)0x47);
		SpiRun.wReg((byte)0x0b,(byte)0x2b);
		SpiRun.wReg((byte)0x0c,(byte)0x56);
		SpiRun.wReg((byte)0x0e,(byte)0xba);
		SpiRun.wReg((byte)0x0f,(byte)0xc8);//SpiRun.wReg((byte)0x0f,(byte)0xc8);
		SpiRun.wReg((byte)0x10,(byte)0x47);//SpiRun.wReg((byte)0x10,(byte)0x84);
		SpiRun.wReg((byte)0x11,(byte)0x40);
		SpiRun.wReg((byte)0x12,(byte)0x05);
		SpiRun.wReg((byte)0x13,(byte)0xa4);//SpiRun.wReg((byte)0x13,(byte)0x94);
		SpiRun.wReg((byte)0x14,(byte)0x7a);//SpiRun.wReg((byte)0x14,(byte)0x7a);
		SpiRun.wReg((byte)0x15,(byte)0xe1);//SpiRun.wReg((byte)0x15,(byte)0xe1);
		SpiRun.wReg((byte)0x16,(byte)0x39);//SpiRun.wReg((byte)0x16,(byte)0x3e);
		SpiRun.wReg((byte)0x17,(byte)0xf1);
		SpiRun.wReg((byte)0x1b,(byte)0x11);
		SpiRun.wReg((byte)0x1c,(byte)0x90);
		SpiRun.wReg((byte)0x1d,(byte)0xc0);//FIFO config c0
		SpiRun.wReg((byte)0x20,(byte)0x12);		
		SpiRun.wReg((byte)0x27,(byte)0x43);//Whitening on   SpiRun.wReg((byte)0x27,(byte)default);
		SpiRun.wReg((byte)0x26,(byte)0x00);//FIFO mode,Standard packet mode
		SpiRun.wReg((byte)0x28,(byte)0x20);
		SpiRun.wReg((byte)0x2c,(byte)0x52);//SpiRun.wReg((byte)0x2c,(byte)default);
		SpiRun.wReg((byte)0x2e,(byte)0xff);
		
		SpiRun.wExt((byte)0x00,(byte)0x18);		
		SpiRun.wExt((byte)0x02,(byte)0x03);
		SpiRun.wExt((byte)0x05,(byte)0x02);
		SpiRun.wExt((byte)0x0c,(byte)0x56);
		SpiRun.wExt((byte)0x0d,(byte)0xcc);
		SpiRun.wExt((byte)0x0e,(byte)0xcc);
		SpiRun.wExt((byte)0x10,(byte)0xee);
		SpiRun.wExt((byte)0x11,(byte)0x10);
		SpiRun.wExt((byte)0x12,(byte)0x07);
		SpiRun.wExt((byte)0x13,(byte)0xaa);
		SpiRun.wExt((byte)0x16,(byte)0x40);
		SpiRun.wExt((byte)0x17,(byte)0x0e);
		SpiRun.wExt((byte)0x19,(byte)0x03);
		SpiRun.wExt((byte)0x1b,(byte)0x33);
		SpiRun.wExt((byte)0x1d,(byte)0x17);
		SpiRun.wExt((byte)0x1f,(byte)0x00);
		SpiRun.wExt((byte)0x20,(byte)0x6e);
		SpiRun.wExt((byte)0x21,(byte)0x1c);
		SpiRun.wExt((byte)0x22,(byte)0xac);
		SpiRun.wExt((byte)0x27,(byte)0xb5);
		SpiRun.wExt((byte)0x2f,(byte)0x05);
		SpiRun.wExt((byte)0x32,(byte)0x0e);		
		SpiRun.wExt((byte)0x36,(byte)0x03);
	}
}
