import java.io.IOException;

import com.pi4j.system.NetworkInfo;

public class TestAddress {

	public static void main(String[] args) throws Exception, InterruptedException {
		System.out.println(NetworkInfo.getIPAddress());
		String[] ips=NetworkInfo.getIPAddresses();
		System.out.println("---");
		for(String s:ips){
			System.out.println(s);
		}
		System.out.println("Hostname---");
		System.out.println(NetworkInfo.getHostname());
		System.out.println("FQDN---");
		System.out.println(NetworkInfo.getFQDN());
		
		String[] fqdns=NetworkInfo.getFQDNs();
		System.out.println("---");
		for(String s:fqdns){
			System.out.println(s);
		}
		
	}

}
