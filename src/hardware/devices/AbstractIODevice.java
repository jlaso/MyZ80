package MyZ80.hardware.devices;

/**
 * Created by joseluislaso on 22/09/15.
 */
public abstract class AbstractIODevice implements IODevice {

    protected int ports = 1;

    public int getPorts() {
        return ports;
    }
}
