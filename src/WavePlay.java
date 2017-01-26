import java.net.URL;
import javafx.scene.media.AudioClip;

public class WavePlay {

	final URL resource = getClass().getResource("error.wav");
    final AudioClip clip = new AudioClip(resource.toString());

	
    public void play(){
    	clip.play();
    }
    
	public static void main(String[] args) {
		WavePlay p=new WavePlay();
		p.play();
	}

}
