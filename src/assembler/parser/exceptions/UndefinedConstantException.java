package assembler.parser.exceptions;

/**
 * Created by joseluislaso on 14/09/15.
 */
public class UndefinedConstantException extends Exception{

    public UndefinedConstantException(String constantName) {
        super("Undefined constant "+constantName);
    }
}
