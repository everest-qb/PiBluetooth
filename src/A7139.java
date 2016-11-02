
public class A7139 {

	public static byte[] CMD_SOFT_RESET		=new byte[]{1,1,1,1,1,1,1,1};
	public static byte[] CMD_SLEEP			=new byte[]{0,0,0,1,0,0,0,0};
	public static byte[] CMD_DEEP_SLEEP		=new byte[]{0,0,0,1,1,1,1,1};
	public static byte[] CMD_STANDBY		=new byte[]{0,0,0,1,0,1,0,0};
	
	public static byte[] WRITE_ID			=new byte[]{0,0,1,0,0,0,0,0};
	public static byte[] WRITE_CONTROL		=new byte[]{0,0,0,0,0,0,0,0};
	public static byte[] READ_ID			=new byte[]{1,0,1,0,0,0,0,0};
	public static byte[] READ_CONTROL		=new byte[]{1,0,0,0,0,0,0,0};
	
	//public static byte GIO_PAGE=(byte) 0b00001000;
}
