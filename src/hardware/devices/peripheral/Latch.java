package MyZ80.hardware.devices.peripheral;

import MyZ80.hardware.devices.AbstractIODevice;
import MyZ80.hardware.devices.ComplexIODevice;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class Latch extends AbstractIODevice{

    int numberOfPorts;
    int[] latch;
    ComplexIODevice[] map;
    int[] mapDevicePort;

    public Latch(int numberOfPorts) {
        this.numberOfPorts = numberOfPorts;
        latch = new int[numberOfPorts];
        mapDevicePort = new int[numberOfPorts];
        map = new ComplexIODevice[numberOfPorts];
        for (int i = 0; i < numberOfPorts; i++) {
            map[i] = null;
        }
    }

    @Override
    public int read(int port) {
        port = mapPort(port);
        if (map[port] != null) {
            latch[port] = map[port].read(mapDevicePort[port]);
        }

        return latch[port];
    }

    @Override
    public void write(int port, int data) {
        port = mapPort(port);
        latch[port] = data;
        if (map[port] != null) {
            map[port].write(mapDevicePort[port], data);
        }
    }

    @Override
    public int mapPort(int port) {
        return (port & (numberOfPorts - 1));
    }

    public void connect(int port, ComplexIODevice device, int internalPort){
        port = mapPort(port);
        map[port] = device;
        mapDevicePort[port] = internalPort;
    }

}
