package MyZ80.hardware.devices.peripheral;

import MyZ80.hardware.devices.AbstractDevice;
import MyZ80.hardware.devices.Peripheral;

/**
 * Created by joseluislaso on 22/09/15.
 */
abstract class AbstractLcd extends AbstractDevice implements Peripheral{

    public static int MODE_PARALLEL_BYTE = 0;
    public static int MODE_PARALLEL_NIBBLE = 0;

    String[] buffer;
    int mode, numLines, width, temp;

    public AbstractLcd(int mode, int numLines, int width) {
        this.mode = mode;
        this.numLines = numLines;
        this.width = width;
        buffer = new String[numLines];
        reset();
    }

    @Override
    public void reset() {
        for (int i = 0; i < numLines; i++) {
            buffer[i] = "";
            reDraw();
        }
    }

    @Override
    public void write(int data) {

    }

    @Override
    public int read() {
        return 0;
    }

    public void reDraw() {

    }
}
