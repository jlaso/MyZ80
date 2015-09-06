package hardware.system;

/**
 * Created by joseluislaso on 05/09/15.
 */
public interface IOSystemInterface {

    public byte inb(int address);

    public void outb(int address, byte value);

}
