package assembler;

import myz80.Project;

import java.io.FileNotFoundException;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class TestAssembler {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {

        Program program = new Program("./src/assembler/test.asm");

        try {
            program.assemble();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        program.saveBin();


    }

}
