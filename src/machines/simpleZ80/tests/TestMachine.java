package machines.simpleZ80.tests;

import assembler.Tools;
import fileFormat.Z80FileFormat;
import hardware.board.Board;
import hardware.cpu.z80.Z80;
import machines.simpleZ80.IOSpace;
import machines.simpleZ80.Memory;
import samples.Samples;
import common._;


/**
 * Created by joseluislaso on 08/09/15.
 */
public class TestMachine {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String romFile = Samples.getFile("test-out.bin");
        System.out.println("System memory is assigned to " + romFile);

        Z80FileFormat z80file = new Z80FileFormat(Memory.ROM_SIZE);
        z80file.readFromFile(romFile);

        long clock = 4000000; // 4MHz

        Board board = new Board("Z80 "+romFile+" @ "+(clock/1000000)+"MHz");

        IOSpace ioSpace = new IOSpace(board);
        Memory systemMemory = new Memory(z80file);
        Z80 cpu = new Z80(clock,_.NO_DEBUG);
        cpu.attachSystemMemory(systemMemory);
        cpu.attachIOSpace(ioSpace);
        cpu.attachStatusPanel(board.getStatusBarPanel());

        cpu.reset();
        Tools.println("red", "starting on PC "+Tools.addressToHex(z80file.getPC()));
        cpu.run(z80file.getPC());

    }
}
