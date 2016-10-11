import javax.bluetooth.*;
import javax.microedition.io.*;



public class SampleSPPServer {

	public static boolean STOP_FLAG=false;
	
	//start server
	private void startServer() throws Exception{
			
	//BlueCoveImpl.setConfigProperty("bluecove.deviceID", "0");
		
	//Create a UUID for SPP
	UUID uuid = new UUID("1101", true);
	//Create the servicve url
	String connectionString = "btspp://localhost:" + uuid +";authenticate=true;encrypt=true;name=Run SPP Server";//authenticate=false;encrypt=false;

	//open server url
	StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );

	//Wait for client connection
	System.out.println("\nServer Started. Waiting for clients to connect¡K");
	
	
		while (!STOP_FLAG) {
			StreamConnection connection = streamConnNotifier.acceptAndOpen();
			if(!STOP_FLAG){
			RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
			System.out.println("Remote device address: " + dev.getBluetoothAddress());
			System.out.println("Remote device name: " + dev.getFriendlyName(true));
			ProcessThread r = new ProcessThread(connection);
			r.start();
			}
		}
		
	streamConnNotifier.close();

	}
	
	public static void main(String[] args) throws Exception {	
		
		PairThread r=new PairThread();
		r.start();
		
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("Address: "+localDevice.getBluetoothAddress());
		System.out.println("Name: "+localDevice.getFriendlyName());		
		SampleSPPServer sampleSPPServer=new SampleSPPServer();
		sampleSPPServer.startServer();
	}

}
