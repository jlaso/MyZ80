package hardware.devices;

import fileFormat.BinaryFileInterface;
import fileFormat.Z80FileFormat;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by joseluislaso on 05/09/15.
 */
public class ROM extends RAM {

    protected BinaryFileInterface romFile;

    public ROM(int size, int baseAddress, BinaryFileInterface romFile) throws Exception {
        super(size, baseAddress);
        this.romFile = romFile;
        memory = romFile.getBytes();
    }

    @Override
    public void write(int address, int value) {
        // do nothing because ROM is read only
    }

    @Override
    public void reset() {

    }
}
