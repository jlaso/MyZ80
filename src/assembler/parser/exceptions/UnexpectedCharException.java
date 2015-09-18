package assembler.parser.exceptions;

/**
 * Created by joseluislaso on 17/09/15.
 */
public class UnexpectedCharException extends Exception {
    public UnexpectedCharException(char current, int index, String buffer, int lineNum) {
        super("Unexpected char '"+current+"' at pos "+index+" in the line "+lineNum+" -> "+buffer);
    }
}
