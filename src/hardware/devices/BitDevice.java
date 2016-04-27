package hardware.devices;

/**
 * Created by joseluislaso on 22/09/15.
 */
public interface BitDevice extends SimpleRW {

    public int read();

    public void write(int data);

}
