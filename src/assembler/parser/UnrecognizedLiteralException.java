package assembler.parser;

/**
 * Created by joseluislaso on 14/09/15.
 */
public class UnrecognizedLiteralException extends Exception{
    public UnrecognizedLiteralException(String expression, char offendingChar, int index) {
        super("Unrecognized literal in expression '"+expression+"', offending char '"+offendingChar+"' in pos "+index);
    }
}
