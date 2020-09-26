package MyZ80.hardware.devices;

/**
 * Created by joseluislaso on 22/09/15.
 */
public abstract class GroupByteDevice extends AbstractIODevice implements ComplexIODevice, SimpleRW{

    protected BitDevice[] bitDeviceMap = new BitDevice[8];

    public GroupByteDevice() {
        for (int i = 0; i < 8; i++) {
            bitDeviceMap[i] = null;
        }
    }

    public void assignToBit(int bit, BitDevice bitDevice){
        bitDeviceMap[bit & 0x07] = bitDevice;
    }

    @Override
    public int read() {
        int read = 0;
        for (int bit = 7; bit >= 0; bit--) {
            if (bitDeviceMap[bit & 0x07] != null) {
                read = read | (bitDeviceMap[bit & 0x07].read() << bit);
            }
        }

        return read;
    }

    @Override
    public void write(int data) {
        for (int bit = 7; bit >= 0; bit--) {
            if (bitDeviceMap[bit & 0x07] != null) {
                bitDeviceMap[bit & 0x07].write((data >>> bit) & 0x01 );
            }
        }
    }

    @Override
    public int read(int port) {
        return read();
    }

    @Override
    public void write(int port, int data) {
        write(data);
    }

    @Override
    public int mapPort(int port) {
        return 0;
    }
}
