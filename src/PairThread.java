import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PairThread extends Thread {

	
	public void run() {
		try{//bt-device -r 78:24:AF:21:1E:88
			Runtime r0=Runtime.getRuntime();
			Process ps0=r0.exec("/usr/bin/bt-adapter -s Discoverable 1");
			BufferedReader bReader=new BufferedReader(new InputStreamReader(ps0.getInputStream()));
			System.out.println("Re:"+bReader.readLine());	
			bReader=new BufferedReader(new InputStreamReader(ps0.getErrorStream()));
			System.out.println("Er:"+bReader.readLine());	
			System.out.println("Ststus:"+ps0.exitValue());	
			
		/*Runtime r1=Runtime.getRuntime();
		Process ps1=r1.exec("/usr/bin/hcitool cc --role=m 78:24:AF:21:1E:88");
		bReader=new BufferedReader(new InputStreamReader(ps1.getInputStream()));
		System.out.println("Re:"+bReader.readLine());	
		bReader=new BufferedReader(new InputStreamReader(ps1.getErrorStream()));
		System.out.println("Er:"+bReader.readLine());	
		System.out.println("Ststus:"+ps1.exitValue());
		
		Runtime r2=Runtime.getRuntime();
		Process ps2=r2.exec("/usr/bin/hcitool auth 78:24:AF:21:1E:88");
		bReader=new BufferedReader(new InputStreamReader(ps2.getInputStream()));
		System.out.println("Re:"+bReader.readLine());
		bReader=new BufferedReader(new InputStreamReader(ps2.getErrorStream()));
		System.out.println("Er:"+bReader.readLine());
		System.out.println("Ststus:"+ps2.exitValue());*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
