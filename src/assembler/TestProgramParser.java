package assembler;

import assembler.items.Item;
import assembler.parser.ProgramParser;
import machines.simpleZ80.Memory;
import samples.Samples;

/**
 * Created by joseluislaso on 17/09/15.
 */
public class TestProgramParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String[] lines = {
            "#include \"file1.asm\" ; this is a comment\n",
            ".org  1000    ; this is the beginning"
        };

        ProgramParser parser = new ProgramParser();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            Item item = parser.itemizeLine(line, i, i);

            System.out.println (item.toString());
        }

    }

}
