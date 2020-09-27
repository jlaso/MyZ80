package MyZ80.fileFormat;

import java.io.*;

/**
 * Created by joseluislaso on 11/09/15.
 *
 * need more ideas ?  take a look http://www.worldofspectrum.org/faq/reference/z80format.htm
 */
public class Z80FileFormat implements BinaryFileInterface {

    protected int size;
    private int PC;
    private byte[] body;

    public Z80FileFormat(int size) {
        this.size = size;
        body = new byte[size];
        for (int i = 0; i < size; i++) {
            body[i] = 0;
        }
    }

    public void addData(int address, byte[] data) {
        for (byte datum : data) {
            body[address++] = datum;
        }
    }

    public void addData(int address, int[] data) {
        for (int datum : data) {
            body[address++] = (byte) (datum & 0xff);
        }
    }

    public byte[] getBytes() {
        return body;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public void saveToFile(String file) throws IOException {

        DataOutputStream writer = new DataOutputStream(new FileOutputStream(file));
        // write header
        writer.writeByte(size & 0xff);
        writer.writeByte(size >>> 8);
        writer.writeByte(PC & 0xff);
        writer.writeByte(PC >>> 8);
        // write body
        writer.write(body);
        // close writer
        writer.close();

    }

    public void readDirect(byte[] content) {
        body = content;
    }

    public void readFromFile(String fileName) throws Exception {
        DataInputStream reader = new DataInputStream(new FileInputStream(fileName));

        // read header
        int sizeRead = reader.readByte() | (reader.readByte() << 8);
        if (size != sizeRead){
            throw new Exception("Size mismatch!");
        }
        PC = (int) ((reader.readByte() & 0xff) | (reader.readByte() << 8));
        // read body
        body = new byte[size];
        if (reader.read(body) != size) {
            throw new Exception("Read size mismatch");
        };
        // close reader
        reader.close();
    }

}
