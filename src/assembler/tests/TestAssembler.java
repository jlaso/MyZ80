package assembler.tests;

import assembler.Program;
import machines.simpleZ80.Memory;
import samples.Samples;
import common.$;

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
