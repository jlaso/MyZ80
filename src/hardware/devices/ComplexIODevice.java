package MyZ80.hardware.devices;

/**
 * Created by joseluislaso on 22/09/15.
 */
public interface ComplexIODevice {

    public int read(int internalPort);

    public void write(int internalPort, int data);

}
