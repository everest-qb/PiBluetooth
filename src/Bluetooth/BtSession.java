package Bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.io.StreamConnection;

public class BtSession implements Runnable{

	private long id;
	private StreamConnection conn;
	private DataInputStream in;
	private DataOutputStream out;
	private Event event;
	private boolean run;
	private Thread t;
	private static ExecutorService executor=Executors.newFixedThreadPool(3);

	public BtSession(StreamConnection con) throws IOException {	
		id=System.currentTimeMillis();
		conn = con;
		run=true;
		in=conn.openDataInputStream();
		out=conn.openDataOutputStream();
		t=new Thread(this);
		t.start();
	}
	
	public BtSession addCallable(Event e){
		event=e;
		return this;
	}
	
	public boolean isOpened(){
		boolean retuenValue=false;
		if(!(in==null || out==null)){
			retuenValue=true;
		}			
		return retuenValue;
	}
	
	public void close() throws IOException, InterruptedException{
		run=false;
		t.join(2000);		
		in=null;
		out=null;
		executor.shutdown();
		conn.close();		
	}
	
	public void write(String msg) throws IOException{
		if(out!=null){
			out.writeUTF(msg);
			out.flush();
		}
	}

	public long getId() {
		return id;
	}
	
	
	@Override
	public void run() {		
		while(run){
			try {
				String data=in.readUTF();
				if(event!=null){
					executor.submit(new Runnable() {						
						@Override
						public void run() {
							event.onRead(data);								
						}
					});
				}
			} catch (IOException e) {
				run=false;
				e.printStackTrace();
			}			
		}
		if(event!=null){
			event.onClosed(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BtSession other = (BtSession) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
