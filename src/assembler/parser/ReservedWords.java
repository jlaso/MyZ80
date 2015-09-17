package assembler.parser;

/**
 * Created by joseluislaso on 17/09/15.
 */
public class ReservedWords {

    final static int WITHOUT_OPERANDS = 0;
    final static int WITH_ONE_OPERAND = 1;
    final static int WITH_TWO_OPERANDS = 2;
    final static int WITH_ONE_OR_TWO_OPERANDS = 3;

    protected final static String[] reservedWords = {
            // WITHOUT_OPERANDS
        "",
            // WITH_ONE_OPERANDS
        "",
            // WITH_TWO_OPERANDS
        "",
            // WITH_ONE_OR_TWO_OPERANDS
        ""
    };

    static boolean is(int type, String word) {

        return reservedWords[type].contains('|'+word+'|');

    }

}
