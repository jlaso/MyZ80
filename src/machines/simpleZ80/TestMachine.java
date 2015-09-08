package machines.simpleZ80;

import assembler.Program;
import hardware.cpu.z80.Z80;

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

        URL path = Program.class.getResource("test.bin");
        System.out.println("System memory is assigned to "+path.getFile());

        Memory systemMemory = new Memory(path.getFile());
        Z80 cpu = new Z80(Z80.DEBUG);
        cpu.attachSystemMemory(systemMemory);

        cpu.reset();
        cpu.run(0);

    }
}
