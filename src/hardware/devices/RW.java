package hardware.devices;

/**
 * Created by joseluislaso on 05/09/15.
 */
public interface RW extends Peripheral{

    public byte read(int address);

    public void write(int address, byte value);

}
