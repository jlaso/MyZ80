package fileFormat;

import assembler.parser.Item;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by joseluislaso on 11/09/15.
 */
public class Z80FileFormat {

    protected int PC;
    protected byte[] body;

    public Z80FileFormat(int size) {
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

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public void saveToFile(String file) throws IOException {

        DataOutputStream writer = new DataOutputStream(new FileOutputStream(file));
        // write header
        writer.writeByte(PC & 0xff);
        writer.writeByte(PC >>> 8);
        // write body
        writer.write(body);
        // close writer
        writer.close();


    }

}
