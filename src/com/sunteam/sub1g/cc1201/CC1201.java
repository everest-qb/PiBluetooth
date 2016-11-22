package com.sunteam.sub1g.cc1201;

public class CC1201 {

	public static byte WRITE=(byte) 0x00;
	public static byte READ=(byte) 0x80;
	public static byte WRITE_BURST=(byte) 0x40;
	public static byte READ_BURST=(byte) 0xc0;	
	
	public static byte ADDR_EXTEND=(byte) 0x2f;	
	public static byte ADDR_FIFO=(byte) 0x3f;
	
	
	public static byte STORE_SRX=(byte)0x34;
	public static byte STORE_STX=(byte)0x35;
	public static byte STORE_SFRX=(byte)0x3a;
	public static byte STORE_SFTX=(byte)0x3b;
	public static byte STORE_SIDLE=(byte)0x36;
	
	public static byte MARCSTATE_RX_FIFO_ERR=(byte)0x11;
	public static byte MARCSTATE_TX_FIFO_ERR=(byte)0x16;
	public static byte MARCSTATE_RX=(byte)0x6d;
	public static byte MARCSTATE_TX=(byte)0x33;
	public static byte MARCSTATE_IDLE=(byte)0x41;
	
}
