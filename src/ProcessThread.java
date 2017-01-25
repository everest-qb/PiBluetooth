import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.microedition.io.StreamConnection;

public class ProcessThread extends Thread {

	private InputStream inStream;
	private OutputStream outStream;
	
	public ProcessThread(StreamConnection conn) throws Exception {
		super();
		this.inStream=conn.openInputStream();
		this.outStream=conn.openOutputStream();
	}

	@Override
	public void run() {
		BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
		String lineRead;
		try {
			lineRead = bReader.readLine();
			System.out.println(lineRead);
			if(lineRead!=null && lineRead.contains("exit"))
				SampleSPPServer.STOP_FLAG=true;
			PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
			pWriter.write("OK\r\n");
			pWriter.flush();

			pWriter.close();
			
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}	

}
