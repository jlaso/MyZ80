package assembler.parser;

import assembler.items.*;
import assembler.parser.exceptions.OperandNotFoundException;
import assembler.parser.exceptions.UnexpectedCharException;
import assembler.parser.exceptions.UnexpectedWordException;

/**
 * Created by joseluislaso on 17/09/15.
 */
public class ProgramParser {

    String buffer;
    char current;
    int index, lineNum;
    boolean eof, eofc, isSpace, isDoubleQuotes, isComma;

    /**
     * advances the pointer and reads the char
     *
     * @return char
     */
    protected char next() {
        //if (eof) return current;
        current = buffer.charAt(++index);
        eof = (index>=buffer.length()-1);
        eofc = (eof || ((current == ';') && !isDoubleQuotes));
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
    protected void discardSpaces() {
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
        return result;
    }

    /**
     * checks that there is no other operand or something useful
     *
     * @throws UnexpectedCharException
     */
    protected void dontExpectMore() throws UnexpectedCharException {
        discardSpaces();
        if (!eofc) throw new UnexpectedCharException(current, index, buffer, lineNum);
    }

    /**
     * get the next operand from the buffer
     *
     * @return String
     */
    protected String getOperand() throws OperandNotFoundException {
        String result = "";
        discardSpaces();
        while (!isSpace && !isComma && !eofc) {
            result += current;
            next();
        }
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
        if (ReservedWords.is(ReservedWords.WITHOUT_OPERANDS, word)) {
            dontExpectMore();
            return new Token(word, "", "", buffer);
        }
        if (ReservedWords.is(ReservedWords.WITH_ONE_OPERAND, word)) {
            String operand = getOperand();
            dontExpectMore();
            return new Token(word, operand, "", buffer);
        }
        if (ReservedWords.is(ReservedWords.WITH_ONE_OR_TWO_OPERANDS, word)) {
            String operand1 = getOperand();
            String operand2 = "";
            try {
                operand2 = getOperand();
            }catch (OperandNotFoundException e){
                operand2 = "";
            }
            dontExpectMore();
            return new Token(word, operand1, operand2, buffer);
        }
        if (ReservedWords.is(ReservedWords.WITH_TWO_OPERANDS, word)) {
            String operand1 = getOperand();
            String operand2 = getOperand();
            dontExpectMore();
            return new Token(word, operand1, operand2, buffer);
        }
        if (current == ':') {
            dontExpectMore();
            return new Label(word, address, buffer);
        }
        switch (word.charAt(0)) {
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
                    return new Directive(word, expression, buffer);
                }

        }

        throw new UnexpectedWordException(word, index, buffer, lineNum);
    }


}
