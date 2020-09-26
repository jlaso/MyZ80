package MyZ80.hardware.devices.peripheral;

import MyZ80.hardware.board.LedIcon;
import MyZ80.hardware.devices.BitDevice;

import java.awt.*;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class Led implements BitDevice {

    protected boolean status;
    protected LedIcon image;

    public Led(LedIcon image) {
        this.status = false;
        this.image = image;
    }

    @Override
    public int read() {
        return status ? 1 : 0;
    }

    @Override
    public void write(int data) {
        boolean prev = status;
        status = (data != 0);
        if ((prev != status) && (image != null)) {
            image.change(status);
        }
    }


}
