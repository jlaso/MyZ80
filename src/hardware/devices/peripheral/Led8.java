package hardware.devices.peripheral;

import hardware.board.LedIcon;
import hardware.devices.AbstractIODevice;
import hardware.devices.BitDevice;
import hardware.devices.GroupByteDevice;

import java.awt.*;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class Led8 extends GroupByteDevice {

    Led[] led = new Led[8];
    protected LedIcon[] images = new LedIcon[8];

    public Led8(LedIcon[] images) throws Exception {
        this.images = images;
        if (images.length != 8) {
            throw new Exception("You have to pass an array of 8 pictures");
        }
        for (int bit = 0; bit < led.length; bit++) {
            led[bit] = new Led(images[bit]);
            assignToBit(bit, led[bit]);
        }
    }

}
