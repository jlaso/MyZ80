package MyZ80.hardware.devices;

import MyZ80.fileFormat.BinaryFileInterface;

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
        // @TODO: Maybe handle exception if some config flag is set, to avoid programs writing in here?
    }

    @Override
    public void reset() {

    }
}
