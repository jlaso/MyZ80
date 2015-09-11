package assembler;

import machines.simpleZ80.Memory;
import myz80.Project;
import samples.Samples;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class TestAssembler {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String file = "test2";

        Program program = new Program(Samples.getFile(file+".asm"));

        program.assemble();

        program.saveBin(Samples.getFile(file+".bin"), Memory.ROM_SIZE);

    }

}
