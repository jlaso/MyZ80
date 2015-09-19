package machines.simpleZ80;

import assembler.Program;
import assembler.Tools;
import fileFormat.Z80FileFormat;
import hardware.cpu.z80.Z80;
import samples.Samples;
import common._;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Created by joseluislaso on 08/09/15.
 */
public class TestMachine {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String romFile = Samples.getFile("test2.bin");
        System.out.println("System memory is assigned to " + romFile);

        Z80FileFormat z80file = new Z80FileFormat(Memory.ROM_SIZE);
        z80file.readFromFile(romFile);

        Memory systemMemory = new Memory(z80file);
        Z80 cpu = new Z80(_.DEBUG);
        cpu.attachSystemMemory(systemMemory);

        cpu.reset();
        Tools.println("red", "PC="+z80file.getPC());
        cpu.run(z80file.getPC());

    }
}
