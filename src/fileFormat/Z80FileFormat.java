package fileFormat;

import assembler.parser.Item;

import java.io.*;

/**
 * Created by joseluislaso on 11/09/15.
 */
public class Z80FileFormat implements BinaryFileInterface{

    protected int size;
    protected int PC;
    protected byte[] body;

    public Z80FileFormat(int size) {
        this.size = size;
        body = new byte[size];
        for (int i = 0; i < size; i++) {
            body[i] = 0;
        }
    }

    public void addData(int address, byte[] data) {
        for (int i = 0; i < data.length; i++) {
            body[address++] = data[i];
        }
    }

    public void addData(int address, int[] data) {
        for (int i = 0; i < data.length; i++) {
            body[address++] = (byte) (data[i] & 0xff);
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

    public void readFromFile(String fileName) throws Exception {
        DataInputStream reader = new DataInputStream(new FileInputStream(fileName));

        // read header
        int sizeReaded = reader.readByte() | (reader.readByte() << 8);
        if (size != sizeReaded){
            throw new Exception("Size mismatch!");
        }
        PC = (int) ((reader.readByte() & 0xff) | (reader.readByte() << 8));
        // read body
        body = new byte[size];
        reader.read(body);
        // close reader
        reader.close();
    }

}
