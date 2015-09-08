package hardware.devices;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by joseluislaso on 05/09/15.
 */
public class ROM extends RAM {

    protected String romFile;

    public ROM(int size, int baseAddress, String romFile) throws Exception {
        super(size, baseAddress);
        this.romFile = romFile;
        File file = new File(romFile);
//        if (file.length() != size){
//            System.out.println("Error in size of ROM "+romFile+" file! "+file.length()+" vs "+size);
//            throw new Exception("Error in size of ROM "+romFile+" file! "+file.length()+" vs "+size);
//        }
        DataInputStream dis = new DataInputStream(new FileInputStream(romFile));
        dis.readFully(memory, 0, (int)Math.min(file.length(), size));
        dis.close();
    }

    @Override
    public void write(int address, byte value) {
        // do nothing because ROM is read only
    }

    @Override
    public void reset() {

    }
}
