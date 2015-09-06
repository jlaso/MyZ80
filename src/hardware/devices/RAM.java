package hardware.devices;

/**
 * Created by joseluislaso on 05/09/15.
 */
public class RAM implements Memory {

    protected static byte UNKNOWN = 0;
    protected int baseAdress;
    protected int size;
    protected byte[] memory;

    public RAM(int size, int baseAdress){
        this.size = size;
        this.baseAdress = baseAdress;

        memory = new byte[size];
        reset();
    }

    @Override
    public byte read(int address) {

        int pos = address - baseAdress;

        if (pos < size){
            return memory[pos];
        }

        return UNKNOWN;
    }

    @Override
    public void write(int address, byte value) {
        int pos = address - baseAdress;

        if (pos < size){
            memory[pos] = value;
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

        for (int i=0; i<size; i++){
            memory[i] = 0;
        }
    }
}
