package MyZ80.assembler.tests;

import MyZ80.assembler.Tools;
import MyZ80.assembler.items.Directive;
import MyZ80.assembler.items.Item;
import MyZ80.assembler.items.Label;
import MyZ80.assembler.items.Pending;
import MyZ80.assembler.parser.ProgramParser;
import MyZ80.common.$;

import java.util.ArrayList;

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

        ArrayList<Item> program = new ArrayList<>();

        ProgramParser parser = new ProgramParser();
        int PC = 0;

        // first pass
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            Item item = parser.itemizeLine(line, PC, i);
            program.add(item);
            if ((item instanceof Directive) && (((Directive)item).isORG())){
                PC = Integer.parseInt(((Directive)item).getValue());
            }
            PC += item.getSize();

        }

        dumpPendings(program);

        // second pass resolve pending labels
        for (Item item : program) {
            if (item instanceof Label) propagateLabel(program, (Label)item);
        }

        dumpPendings(program);

        Tools.println("cyan", "\n\nprogram listing\n\n");

        // third, print result
        for (Item item : program) {
            System.out.println (item.toString());
        }

    }

    protected static void propagateLabel(ArrayList<Item> program, Label label) {
        for (Item item: program) {
            item.solvePending(label.getLabel(), label.getAddress());
        }
    }

    protected static void dumpPendings(ArrayList<Item> program) {
        Tools.println("red", $.CR + $.CR + "~~~~~~~~~~ pendings ~~~~~~~~~~");
        for (Item item : program) {
            for (Pending pending : item.getPendingList()) {
                Tools.println("", pending.toString() + " " + item.toString());
            }
        }
    }
}
