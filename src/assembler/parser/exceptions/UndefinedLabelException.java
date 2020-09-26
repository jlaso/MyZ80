package MyZ80.assembler.parser.exceptions;

/**
 * Created by joseluislaso on 14/09/15.
 */
public class UndefinedLabelException extends Exception{

    public UndefinedLabelException(String labelName) {
        super("Undefined label "+labelName);
    }
}
