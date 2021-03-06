package MyZ80.machines.simpleZ80.tests;

import MyZ80.fileFormat.Z80FileFormat;
import MyZ80.machines.simpleZ80.Memory;
import MyZ80.samples.Samples;

/**
 * Created by joseluislaso on 23/09/15.
 */
public class TestMemory {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String romFile = Samples.getFile("test-out.bin");
        System.out.println("System memory is assigned to " + romFile);

        Z80FileFormat z80file = new Z80FileFormat(Memory.ROM_SIZE);
        z80file.readFromFile(romFile);

        Memory memory = new Memory(z80file);

        // test the RAM

        for (int i = memory.firstRAMpos(); i < memory.lastRAMpos(); i++) {
            int v = (int)(Math.random() * 100);

            memory.poke(i, v);
            if (v != memory.peek(i)) {
                System.out.println("Position "+i+" has not been poked as expected!");
            }
        }


    }
}
