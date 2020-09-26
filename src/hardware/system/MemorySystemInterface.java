package MyZ80.hardware.system;

/**
 * Created by joseluislaso on 05/09/15.
 */
public interface MemorySystemInterface {

    public int peek(int address);

    public void poke(int address, int value);

    public void reset();

    public int firstRAMpos();

    public int lastRAMpos();

}
