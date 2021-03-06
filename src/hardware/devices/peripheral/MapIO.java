package MyZ80.hardware.devices.peripheral;

import MyZ80.hardware.devices.*;

/**
 * Created by joseluislaso on 22/09/15.
 *
 * In the declaration of the real machine extend this class to generate the IO map
 *
 */
public class MapIO {

    AbstractIODevice[] map = new AbstractIODevice[256];

    public int read(int port) {
        return map[port & 256].read(port);
    }

    public void write(int port, int data) {
        map[port & 256].write(port, data);
    }

    public void assign(int port, AbstractIODevice p) {
        for (int i = 0; i < p.getPorts(); i++) {
            map[(port+i) & 256] = p;
        }
    }

}
