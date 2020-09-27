package MyZ80.hardware.devices;

/**
 * Created by joseluislaso on 05/09/15.
 */
public interface RW extends Peripheral {

    public int read(int address);

    public void write(int address, int value);

}
