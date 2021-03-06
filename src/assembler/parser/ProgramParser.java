package MyZ80.assembler.parser;

import MyZ80.assembler.Tools;
import MyZ80.assembler.items.*;
import MyZ80.assembler.parser.exceptions.OperandNotFoundException;
import MyZ80.assembler.parser.exceptions.UnexpectedCharException;
import MyZ80.assembler.parser.exceptions.UnexpectedWordException;
import MyZ80.assembler.parser.exceptions.UnrecognizedLiteralException;
import MyZ80.DI.Container;

/**
 * Created by joseluislaso on 17/09/15.
 *
 */
public class ProgramParser {

    private String buffer;
    private char current;
    private int index, lineNum;
    private boolean eof, eofc, isSpace, isDoubleQuotes, isComma, isSemicolon;
    private Container container;

    public ProgramParser() {
        container = Container.getContainer();
    }

    /**
     * advances the pointer and reads the char
     *
     * @return char
     */
    protected char next() {
        //if (eof) return current;
        current = buffer.charAt(++index);
        eof = (index>=buffer.length()-1);
        isSemicolon = (current == ';');
        eofc = (eof || (isSemicolon && !isDoubleQuotes));
        if (current == '"'){
            isDoubleQuotes = !isDoubleQuotes;
        }
        isSpace = (current == ' ');
        isComma = (current == ',');
        return current;
    }

    /**
     * advances the pointer until a char that is not a space
     */
    private void discardSpaces() {
        while (!eof && isSpace) next();
    }

    /**
     * gets the next word in the buffer
     *
     * @return String
     */
    protected String getWord() {
        String result = "";
        discardSpaces();
        while (!eofc && !isSpace) {
            result += current;
            next();
        }
        if (!isSpace && !isSemicolon) result += current;

        return result;
    }

    /**
     * checks that there is no other operand or something useful
     *
     * @throws UnexpectedCharException
     */
    private void dontExpectMore() throws UnexpectedCharException {
        discardSpaces();
        if (!eofc) throw new UnexpectedCharException(current, index, buffer, lineNum);
    }

    /**
     * get the next operand from the buffer
     *
     * @return String
     */
    private String getOperand() throws OperandNotFoundException {
        String result = "";
        discardSpaces();
        while (!isSpace && !isComma && !eofc) {
            result += current;
            next();
        }
        if (isSpace || isComma) next();  // discards comma or space
            else result+=current;

        if (result.equals("")) throw new OperandNotFoundException();

        return result;
    }

    /**
     * get all the rest of the buffer until eof or a comment (;)
     *
     * @return String
     */
    protected String getUntilEofc() {
        String result = "";
        discardSpaces();
        while (!eofc) {
            result += current;
            next();
        }
        if (!isSemicolon) result += current;

        return result.trim();
    }

    protected String getQuotesContent() throws UnexpectedCharException {
        String result = "";
        discardSpaces();
        if (!isDoubleQuotes) throw new UnexpectedCharException(current, index, buffer, lineNum);
        next();
        while(current != '"') {
            result += current;
            next();
        }

        return result;
    }


    public Item itemizeLine(String line, int address, int lineNum) throws Exception {

        buffer = line;
        this.lineNum = lineNum;
        isDoubleQuotes = false;
        index = -1;
        next();
        discardSpaces();
        String word = getWord();
        if (word.isEmpty()) return null;
        if (ReservedWords.is(ReservedWords.WITHOUT_OPERANDS, word)) {
            dontExpectMore();
            return new Token(word, "", "", address, buffer);
        }
        if (ReservedWords.is(ReservedWords.WITH_ONE_OR_NONE_OPERANDS, word)) {
            discardSpaces();
            String operand = "";
            if (!eofc) operand = getOperand();
            dontExpectMore();
            return new Token(word, operand, "", address, buffer);
        }
        if (ReservedWords.is(ReservedWords.WITH_ONE_OPERAND, word)) {
            String operand = getOperand();
            dontExpectMore();
            return new Token(word, operand, "", address, buffer);
        }
        if (ReservedWords.is(ReservedWords.WITH_ONE_OR_TWO_OPERANDS, word)) {
            String operand1 = getOperand();
            String operand2 = "";
            discardSpaces();
            if (!eofc) operand2 = getOperand();
            dontExpectMore();
            return new Token(word, operand1, operand2, address, buffer);
        }
        if (ReservedWords.is(ReservedWords.WITH_TWO_OPERANDS, word)) {
            String operand1 = getOperand();
            String operand2 = getOperand();
            dontExpectMore();
            return new Token(word, operand1, operand2, address, buffer);
        }
        if ((word.length()>0) && (word.charAt(word.length()-1) == ':')) {
            dontExpectMore();
            word = word.substring(0, word.length()-1);
            return new Label(word, address, buffer);
        }
        switch (word.charAt(0)) {
            case ';':
                return null;   // it's a comment

            case '#':
                switch (word.toLowerCase()) {
                    case "#include":
                        String file = getQuotesContent();
                        return new Include(file, buffer);

                    case "#define":
                        String constantName = getWord();
                        String expression = getUntilEofc();
                        return new Constant(constantName, expression, buffer);
                }
                break;

            case '.':
                if (Directive.is(word)) {
                    String expression = getUntilEofc();
                    try {
                        expression = container.expressionParser.preParse(expression);
                        try {
                            double d = Tools.eval(expression);
                            expression = "" + (int) d;
                        }catch(Exception ex){

                        }
                    }catch (UnrecognizedLiteralException e){
                        System.out.println("Unrecognized literal in '" + expression + "' => '"+buffer+"'");
                        System.exit(-1);
                    }
                    return new Directive(word, expression, address, buffer);
                }

        }

        throw new UnexpectedWordException(word, index, buffer, lineNum);
    }


}
