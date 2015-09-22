package machines.simpleZ80;

import hardware.board.Board;
import hardware.devices.peripheral.Latch;
import hardware.devices.peripheral.Led8;
import hardware.devices.peripheral.MapIO;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class IOSpace extends MapIO {

    protected Latch latch1;
    protected Led8 ledArray;
    protected Board board;

    public IOSpace(Board board) throws Exception {

        this.board = board;

        latch1 = new Latch(3);
        assign(0x00, latch1);

        ledArray = new Led8(board.getLedArray().getLeds());
        assign(0x03, ledArray);

    }
}
