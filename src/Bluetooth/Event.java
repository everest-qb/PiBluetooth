package Bluetooth;


public interface Event {
	public void onOpened(BtSession session);
	public void onRead(String data);
	public void onClosed(BtSession session);
}
