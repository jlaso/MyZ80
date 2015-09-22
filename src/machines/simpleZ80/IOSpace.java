package machines.simpleZ80;

import hardware.devices.peripheral.Latch;
import hardware.devices.peripheral.MapIO;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class IOSpace extends MapIO {

    protected Latch latch1 = new Latch(3);

    public IOSpace() {

        assign(0x00, latch1);
        
    }
}
