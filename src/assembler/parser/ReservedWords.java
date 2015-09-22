package assembler.parser;

/**
 * Created by joseluislaso on 17/09/15.
 */
public class ReservedWords {

    final static int WITHOUT_OPERANDS = 0;
    final static int WITH_ONE_OPERAND = 1;
    final static int WITH_TWO_OPERANDS = 2;
    final static int WITH_ONE_OR_NONE_OPERANDS = 3;
    final static int WITH_ONE_OR_TWO_OPERANDS = 4;

    protected final static String[] reservedWords = {
            // WITHOUT_OPERANDS
        "|nop|rrca|rra|cpl|ccf|ccf|rlca|rla|daa|scf|di|ei|halt|",
            // WITH_ONE_OPERANDS
        "|djnz|cp|im|sub|or|and|xor|rst|",
            // WITH_TWO_OPERANDS
        "|ld|inc|dec|add|out|",
            // WITH_ONE_OR_NONE_OPERANDS
        "|ret|",
            // WITH_ONE_OR_TWO_OPERANDS
        "|jr|call|jp|"
    };

    static boolean is(int type, String word) {

        return reservedWords[type].contains('|'+word+'|');

    }

}
