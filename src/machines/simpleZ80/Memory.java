package machines.simpleZ80;

import fileFormat.BinaryFileInterface;
import hardware.devices.RAM;
import hardware.devices.ROM;
import hardware.devices.RW;
import hardware.system.MemorySystemInterface;

/**
 * Created by joseluislaso on 05/09/15.
 */
public class Memory implements MemorySystemInterface {

    protected RW[][] map;
    protected int bank = 0;
    protected static int MAXBANK = 0;
    public final static int ROM_SIZE = 16384;

    public Memory(BinaryFileInterface romFile) throws Exception {

        ROM rom0 = new ROM(ROM_SIZE, 0, romFile);
        RAM ram0 = new RAM(16384*3, ROM_SIZE);

        map = new RW[1][2];
        map[0][0] = rom0;
        map[0][1] = ram0;
    }

    public void switchBank (int bank) throws Exception {
        if ((bank<0) || (bank>MAXBANK)) {
            throw new Exception("bank allowed from 0 to "+MAXBANK);
        }
        this.bank = bank;
    }

    @Override
    public int firstRAMpos() {
        return ROM_SIZE;
    }

    @Override
    public int lastRAMpos() {
        return 0xffff;
    }

    private int getBlock(int address) {
        return  address < ROM_SIZE ? 0 : 1;
    }

    public void reset() {
        for (int bnk=0; bnk<=MAXBANK; bnk++){
            for (int blk=0; blk<map[bnk].length; blk++){
                map[bnk][blk].reset();
            }
        }
    }

    @Override
    public int peek(int address) {
        return map[bank][getBlock(address)].read(address);
    }

    @Override
    public void poke(int address, int value) {

        map[bank][getBlock(address)].write(address, value);

    }
}
