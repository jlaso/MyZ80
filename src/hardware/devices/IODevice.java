package MyZ80.hardware.devices;

/**
 * Created by joseluislaso on 22/09/15.
 */
public interface IODevice {

    public int read(int port);

    public void write(int port, int data);

    public int mapPort(int port);

}
