import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;


public class Spi1G {

	public static SpiDevice spi = null;

	protected static final Console console = new Console();
	
	
	public static void main(String[] args) throws Exception {
		console.title("<-- The Pi4J Project -->", "SPI test");
		console.promptForExit();

		
		
		
		//SpiDevice.DEFAULT_SPI_SPEED
		spi = SpiFactory.getInstance(SpiChannel.CS0,
				SpiDevice.DEFAULT_SPI_SPEED/2, 
                SpiMode.MODE_0);
		
		
		byte[] result =null;		
		
		
		spi.write((byte)0b11111111);				
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		
		byte addr07W=(byte)0b00000000|(byte)0x07;
		byte[] pageCommand=new byte[]{addr07W,(byte)0b01000000,(byte)0b00010001};
		spi.write(pageCommand);		
		byte addr08W=(byte)0b00000000|(byte)0x08;
		byte[] pmWCommand=new byte[]{addr08W,(byte)0b10011011,(byte)0b01110000};
		spi.write(pmWCommand);	
		//TimeUnit.MILLISECONDS.sleep(1);
		
		/*byte addr07W=(byte)0b00000000|(byte)0x07;
		byte[] pageCommand=new byte[]{addr07W,(byte)0b01000000,(byte)0b00010001};
		spi.write(addr07W);
		spi.write((byte)0b01000000);
		spi.write((byte)0b00010001);
		TimeUnit.MILLISECONDS.sleep(1);
		byte addr08W=(byte)0b00000000|(byte)0x08;
		byte[] pmWCommand=new byte[]{addr08W,(byte)0b10011011,(byte)0b01110000};
		spi.write(addr08W);
		spi.write((byte)0b10011011);
		spi.write((byte)0b01110000);
		TimeUnit.MILLISECONDS.sleep(1);*/
		
		
		
		
		/*byte addr0DW=(byte)0b00000000|(byte)0x0D;
		byte[] pinCommand=new byte[]{addr0DW,(byte)0b00001000,(byte)0b00000000};
		spi.write(pinCommand);
		*/
			
		
		pageCommand=new byte[]{addr07W,(byte)0b10000000,(byte)0b00010001};
		spi.write(pageCommand);		
		byte[] wire4Command=new byte[]{addr08W,(byte)0b00000000,(byte)0b01011001};
		spi.write(wire4Command);
		TimeUnit.MILLISECONDS.sleep(1);
		
		/*pageCommand=new byte[]{addr07W,(byte)0b10000000,(byte)0b00010001};
		spi.write(addr07W);
		spi.write((byte)0b10000000);
		spi.write((byte)0b00010001);
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] wire4Command=new byte[]{addr08W,(byte)0b00000000,(byte)0b01011001};
		spi.write(addr08W);
		spi.write((byte)0b00000000);
		spi.write((byte)0b01011001);
		TimeUnit.MILLISECONDS.sleep(1);*/
		
		//fifo test
		byte fifoWrite=(byte) 0b01000000;
		spi.write(fifoWrite,(byte)0xFF);
		byte fifoRead =(byte) 0b11000000;
		byte[] r= spi.write(fifoRead,(byte)0x00);
		
		
		
		
		//spi.write((byte)0b00100000,(byte)0xFF,(byte)0b1010000);	
		
		TimeUnit.SECONDS.sleep(1);
				
		/*spi.write((byte)0b00010000);		
		spi.write((byte)0b00011111);		
		spi.write((byte)0b00010100);
		*/		
		
		//init RF
		
		
		byte addr00W=(byte)0b00000000;		
		byte[] command00=new byte[]{addr00W,(byte)0b00001000,(byte)0b00100011 };		
		spi.write(command00);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr01W=(byte)0b00000000|(byte)0x01;
		byte[] command01=new byte[]{addr01W,(byte)0b00001010,(byte)0b00100100 };		
		spi.write(command01);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr02W=(byte)0b00000000|(byte)0x02;
		byte[] command02=new byte[]{addr02W,(byte)0b10111000,(byte)0b00000101 };		
		spi.write(command02);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr03W=(byte)0b00000000|(byte)0x03;
		byte[] command03=new byte[]{addr03W,(byte)0b00000000,(byte)0b00000000 };		
		spi.write(command03);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr04W=(byte)0b00000000|(byte)0x04;
		byte[] command04=new byte[]{addr04W,(byte)0b00001110,(byte)0b00100000 };		
		spi.write(command04);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr05W=(byte)0b00000000|(byte)0x05;
		byte[] command05=new byte[]{addr05W,(byte)0b00000000,(byte)0b00100100 };		
		spi.write(command05);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr06W=(byte)0b00000000|(byte)0x06;
		byte[] command06=new byte[]{addr06W,(byte)0b00000000,(byte)0b00000000 };		
		spi.write(command06);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		//07 down
		
		
		byte addr0AW=(byte)0b00000000|(byte)0x0A;
		byte[] command0A=new byte[]{addr0AW,(byte)0b00011000,(byte)0b11010000 };		
		spi.write(command0A);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr0BW=(byte)0b00000000|(byte)0x0B;
		byte[] command0B=new byte[]{addr0BW,(byte)0b01110000,(byte)0b00001001 };		
		spi.write(command0B);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr0CW=(byte)0b00000000|(byte)0x0C;
		byte[] command0C=new byte[]{addr0CW,(byte)0b01000000,(byte)0b00000000 };		
		spi.write(command0C);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr0DW=(byte)0b00000000|(byte)0x0D;
		byte[] pinCommand=new byte[]{addr0DW,(byte)0b00011100,(byte)0b00000000};
		spi.write(pinCommand);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr0EW=(byte)0b00000000|(byte)0x0E;
		byte[] command0E=new byte[]{addr0EW,(byte)0b01001100,(byte)0b01000101};
		spi.write(command0E);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		byte addr0FW=(byte)0b00000000|(byte)0x0F;
		byte[] command0F=new byte[]{addr0FW,(byte)0b00100000,(byte)0b11000000};
		spi.write(command0F);
		TimeUnit.MILLISECONDS.sleep(1);
		
		//PAGEA
		pageCommand=new byte[]{addr07W,(byte)0b00000000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0800=new byte[]{addr08W,(byte)0b11110110,(byte)0b00000110 };		
		spi.write(command0800);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b00010000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0801=new byte[]{addr08W,(byte)0b00000000,(byte)0b00000000 };		
		spi.write(command0801);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b00100000,(byte)0b00010001};
		spi.write(pageCommand);		
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0802=new byte[]{addr08W,(byte)0b11111000,(byte)0b00000000 };		
		spi.write(command0802);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b00110000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0803=new byte[]{addr08W,(byte)0b00011001,(byte)0b00000111 };		
		spi.write(command0803);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		//0804 done
		
		pageCommand=new byte[]{addr07W,(byte)0b01010000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0805=new byte[]{addr08W,(byte)0b00000010,(byte)0b00000001 };		
		spi.write(command0805);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b01100000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0806=new byte[]{addr08W,(byte)0b00100000,(byte)0b00001111 };		
		spi.write(command0806);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b01110000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0807=new byte[]{addr08W,(byte)0b00101010,(byte)0b11000000 };		
		spi.write(command0807);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		//0808 done
		
		
		pageCommand=new byte[]{addr07W,(byte)0b10010000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0809=new byte[]{addr08W,(byte)0b11010001,(byte)0b10000001 };		
		spi.write(command0809);
		TimeUnit.MILLISECONDS.sleep(1);	
		
		
		pageCommand=new byte[]{addr07W,(byte)0b10100000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command080A=new byte[]{addr08W,(byte)0b00000000,(byte)0b00000100 };		
		spi.write(command080A);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b10110000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command080B=new byte[]{addr08W,(byte)0b00001000,(byte)0b00100101 };		
		spi.write(command080B);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b11000000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command080C=new byte[]{addr08W,(byte)0b00000001,(byte)0b00100111 };		
		spi.write(command080C);
		TimeUnit.MILLISECONDS.sleep(1);
		
				
		pageCommand=new byte[]{addr07W,(byte)0b11010000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command080D=new byte[]{addr08W,(byte)0b00000000,(byte)0b00111111 };		
		spi.write(command080D);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b11100000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command080E=new byte[]{addr08W,(byte)0b00010101,(byte)0b00000111 };		
		spi.write(command080E);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b11110000,(byte)0b00010001};
		spi.write(pageCommand);
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command080F=new byte[]{addr08W,(byte)0b00000000,(byte)0b00000000 };		
		spi.write(command080F);
		TimeUnit.MILLISECONDS.sleep(1);
		
		//PAGEB
		pageCommand=new byte[]{addr07W,(byte)0b00000000,(byte)0b00010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte addr09W=(byte)0b00000000|(byte)0x09;
		byte[] command0900=new byte[]{addr09W,(byte)0b00000011,(byte)0b00110111 };		
		spi.write(command0900);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b00000000,(byte)0b10010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0901=new byte[]{addr09W,(byte)0b10000010,(byte)0b00000000 };		
		spi.write(command0901);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b00000001,(byte)0b00010001};
		spi.write(pageCommand);		
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0902=new byte[]{addr09W,(byte)0b00000000,(byte)0b00000000 };		
		spi.write(command0902);
		TimeUnit.MILLISECONDS.sleep(1);

		
		pageCommand=new byte[]{addr07W,(byte)0b00000001,(byte)0b10010001};
		spi.write(pageCommand);	
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0903=new byte[]{addr09W,(byte)0b00000000,(byte)0b00000000 };		
		spi.write(command0903);
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		pageCommand=new byte[]{addr07W,(byte)0b00000010,(byte)0b00010001};
		spi.write(pageCommand);		
		TimeUnit.MILLISECONDS.sleep(1);
		byte[] command0904=new byte[]{addr09W,(byte)0b00000000,(byte)0b00000000 };		
		spi.write(command0904);
		TimeUnit.MILLISECONDS.sleep(1);
			
		
		//read id code for test spi
		byte[] idwCommand=new byte[]{(byte)0b00100000
				,(byte)0b00110100
				,(byte)0b01110101
				,(byte)0b11000101
				,(byte)0b10001100};								
		spi.write(idwCommand);
		TimeUnit.MILLISECONDS.sleep(1);	
		
		
		spi.write((byte)0b00010100);
		TimeUnit.MILLISECONDS.sleep(1);	
		
		//for(int i=0;i<10;i++)
		//Spi1G.writeFIFO(new byte[]{1,2,3,4,5,6,7,8,9,10});
		
		
		//read id code for test spi
		result =spi.write((byte)0b10100000);
		console.println("RESUT:"+Spi1G.bytesPrint(result));		
		TimeUnit.MILLISECONDS.sleep(1);
		
		
		//PM rregister red for test spi
		byte sysCommand= (byte)0b10000000|(byte)0x07;		
		result =spi.write(sysCommand,(byte)0x00,(byte)0x00);
		
		for(byte b:result){
			console.println(b);
		}

		TimeUnit.SECONDS.sleep(30);
				
	}

	public static void writeFIFO(byte[] data) throws Exception{		
		byte txAddrReset=(byte)0b01100000;
		spi.write(txAddrReset);
		byte[] txFIFOWrite=new byte[data.length+1];
		for(int i=0;i<txFIFOWrite.length;i++){
			if(i==0){
				txFIFOWrite[i]=(byte)0b01000000;
			}else{
				txFIFOWrite[i]=data[i-1];
			}
		}			
		spi.write(txFIFOWrite);
		byte txMode=(byte)0b00011010;
		spi.write(txMode);		
		TimeUnit.MILLISECONDS.sleep(1);
	}
	
	public static String bytesPrint(byte[] bs){
		String returnValue="";
		int i=1;
		for(byte b:bs){
			returnValue+=b;
			if(i!=1 && i%8==0){
				returnValue+=" ";
			}
			i++;
		}
		return returnValue;
	}
		
	public static byte[] hexToBtes(int hex){
		byte[] unit8=new byte[8];
		for(int i=0;i<8;i++){
			unit8[7-i]=(byte)(hex & 1);
			hex=hex>>>1;
		}
		return unit8;
	}
	
	public static byte[] address(byte[] cmd,int hex){
		byte[] unit8=new byte[8];
		byte[] addr=hexToBtes(hex);
		for(int i=0;i<8;i++){			
			unit8[i]=(byte)(cmd[i] | addr[i]);			
		}
		return unit8;
	}
	
	public static byte[] combined(byte[] a,byte[] b){
		byte[] returnValue=new byte[a.length+b.length];
		System.arraycopy(a, 0, returnValue, 0, a.length);
		System.arraycopy(b, 0, returnValue, a.length, b.length);
		return returnValue;
	}
}
