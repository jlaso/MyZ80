package MyZ80.hardware.devices;

/**
 * Created by joseluislaso on 05/09/15.
 */
public class RAM implements Memory {

    protected static byte UNKNOWN = 0;
    protected int baseAddress;
    protected int size;
    protected byte[] memory;

    public RAM(int size, int baseAdress) {
        this.size = size;
        this.baseAddress = baseAdress;

        memory = new byte[size];
        reset();
    }

    @Override
    public int read(int address) {

        int pos = address - baseAddress;

        if (pos < size) {
            byte b = memory[pos];
            return (b >= 0) ? (int) b : (int) (256 + b);
        }

        return UNKNOWN;
    }

    @Override
    public void write(int address, int value) {
        int pos = address - baseAddress;

        if (pos < size) {
            memory[pos] = (byte) value;
        }
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void reset() {

        for (int i = 0; i < size; i++) {
            memory[i] = 0;
        }
    }
}
