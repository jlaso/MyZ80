package MyZ80.assembler.parser.exceptions;

/**
 * Created by joseluislaso on 17/09/15.
 */
public class UnexpectedWordException extends Exception {
    public UnexpectedWordException(String word, int index, String buffer, int lineNum) {
        super("Unexpected word '"+word+"' at pos "+index+" in line "+lineNum+" -> "+buffer);
    }
}
