package Bluetooth;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.intel.bluetooth.BluetoothConsts;


public class BluetoothServer implements Runnable{

	private static LocalDevice lDevic = null;
	private UUID uuid = new UUID("102030405060708090A0B0C0D0E0F011", false);
    private String url = BluetoothConsts.PROTOCOL_SCHEME_RFCOMM+"://localhost:" + uuid.toString()
    +";authenticate=true;authorize=true;encrypt=true";//
    private List<BtSession> sessions;
    private StreamConnectionNotifier connector;
    private boolean run;
    private Event event;

	public BluetoothServer() {
		try {
			lDevic = LocalDevice.getLocalDevice();
			init();			
			run=false;;
			sessions=new CopyOnWriteArrayList<BtSession>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void start(){	
		run=true;		
		new Thread(this).start();		
	}
	
	public void stop(){
		run=false;
		for(BtSession session:sessions){
			try {
				session.close();
			} catch (IOException e) {				
				e.printStackTrace();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		if(connector!=null)
			try {
				connector.close();
			} catch (IOException e) {	
				e.printStackTrace();
			}
		
	}
	
	public void addListener(Event e){
		event=e;
	}
		
	public void removeSession(BtSession session){
		sessions.remove(session);
		System.out.println("Session left size:"+sessions.size());
	}
	
	private void init() throws IOException{
		connector = (StreamConnectionNotifier) Connector.open(url);
		ServiceRecord record = lDevic.getRecord(connector);
	    record.setAttributeValue( 0x0008, new DataElement( DataElement.U_INT_1,0xFF));
	}
	
	@Override
	public void run() {
		while (run) {
			try {
				StreamConnection conn = connector.acceptAndOpen();
				BtSession session=new BtSession(conn).addCallable(event);
				sessions.add(session);	
				if(event!=null)
					event.onOpened(session);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	
	public static void main(String[] args) {
		BluetoothServer server =new BluetoothServer();
		server.addListener(new Event(){

			@Override
			public void onOpened(BtSession session) {				
				System.out.println("open session:"+session.getId());
			}

			@Override
			public void onRead(String data) {
				System.out.println("Recive:" + data);
				if ("STOP".equals(data)) {
					server.stop();
				}
			}

			@Override
			public void onClosed(BtSession session) {
				System.out.println("close session:"+session.getId());
				server.removeSession(session);
			}});
		server.start();
	}
}
