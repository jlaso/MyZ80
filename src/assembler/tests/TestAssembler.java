package MyZ80.assembler.tests;

import MyZ80.assembler.Program;
import MyZ80.machines.simpleZ80.Memory;
import MyZ80.samples.Samples;
import MyZ80.common.$;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class TestAssembler {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String file = "test";

        Program program = new Program(Samples.getFile(file+".asm"), $.DEBUG);

        program.assemble();

        program.saveBin(Samples.getFile(file+".bin"), Memory.ROM_SIZE);

        program.dumpProgram();

    }

}
