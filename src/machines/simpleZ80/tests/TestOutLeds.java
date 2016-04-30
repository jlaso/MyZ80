package machines.simpleZ80.tests;

import assembler.Tools;
import common.$;
import fileFormat.Z80FileFormat;
import hardware.board.Board;
import hardware.cpu.z80.Z80;
import machines.simpleZ80.IOSpace;
import machines.simpleZ80.Memory;


/**
 * Created by joseluislaso on 08/09/15.
 */
public class TestOutLeds {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        Z80FileFormat z80file = new Z80FileFormat(Memory.ROM_SIZE);
        z80file.readDirect(getTestProgram());

        Board board = new Board();

        // let the board draws itself
        Thread.sleep(2000);

        IOSpace ioSpace = new IOSpace(board);
        Memory systemMemory = new Memory(z80file);
        Z80 cpu = new Z80($.DEBUG);
        cpu.attachSystemMemory(systemMemory);
        cpu.attachIOSpace(ioSpace);

        cpu.reset();
        Tools.println("red", "PC=" + z80file.getPC());
        cpu.attachStatusPanel(board.getStatusBarPanel());
        cpu.run(z80file.getPC());

    }

    private static byte[] getTestProgram() {
        return new byte[] {
                0x00,       // NOP
                0x3E, 0x0F, // LD A,0x0F
          (byte)0xD3, 0x03, // OUT A,(0x03)
                0x76        // HALT
        };
    }
}
