package hardware.system;

/**
 * Created by joseluislaso on 05/09/15.
 */
public interface MemorySystemInterface {

    public byte peek(int address);

    public void poke(int address, byte value);

    public void reset();

}
