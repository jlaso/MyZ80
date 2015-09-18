package assembler.tests;

import assembler.items.Directive;
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
            "#include \"file1.asm\" ; this is a comment",
            ".org  1000    ; this is the beginning",
            "jr start",
            ".db 1,2,3,4,5,6",
            "start:",
            "ld a,10",
            "ld (hl),$25"
        };

        ProgramParser parser = new ProgramParser();
        int PC = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            Item item = parser.itemizeLine(line, i, i);
            if ((item instanceof Directive) && (((Directive)item).isORG())){
                PC = Integer.parseInt(((Directive)item).getValue());
            }
            item.setAddress(PC);
            PC += item.getSize();

            System.out.println (item.toString());
        }

    }

}
