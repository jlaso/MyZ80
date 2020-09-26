package MyZ80.assembler.parser;

import MyZ80.DI.Container;
import MyZ80.assembler.items.Valuable;
import MyZ80.assembler.parser.exceptions.*;

import java.util.ArrayList;

/**
 * Created by joseluislaso on 14/09/15.
 *
 */
public class ExpressionParser {

    final protected static char END = ' ';
    protected Container container;
    protected String expression;
    protected int index;
    protected char current;
    final protected static String validCharsInLiteral = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
    protected ArrayList<String> pendingList = new ArrayList<String>();

    public ExpressionParser(Container container){
        this.container = container;
    }

    public ExpressionParser() { this(Container.getContainer()); }

    protected void eatSpaces() {

        while (expression.charAt(index) == ' ') {
            index++;
        }

    }

    protected char getChar() {

        return expression.charAt(index++);

    }

    protected char look() {

        if (index >= expression.length()) return END;
        return expression.charAt(index);

    }

    protected boolean isSeparator(char c) {
        return ((c == END) || (c == ' ') || (c == '+') || (c == '-') || (c == '*') || (c == '/') || (c == ','));
    }

    protected boolean isDoubleQuote(char c) {
        return c=='"';
    }

    protected int getHexNumber() {

        String temp = "";

        for (; ; ) {
            char c = look();
            if (isSeparator(c)) break;
            getChar();
            if ((c < '0') || (c > 'f'))
                throw new NumberFormatException("Unrecognized '" + temp + c + "' in '" + expression + "' as a hexadecimal number");
            temp += c;
        }

        return Integer.parseInt(temp, 16);
    }

    protected int getBinaryNumber() {

        String temp = "";

        for (; ; ) {
            char c = look();
            if (isSeparator(c)) break;
            getChar();
            if ((c != '0') && (c != '1'))
                throw new NumberFormatException("Unrecognized '" + temp + c + "' in '" + expression + "' as a binary number");
            temp += c;
        }

        return Integer.parseInt(temp, 2);
    }

    protected int getDecimalNumber() {

        String temp = "";

        for (; ; ) {
            char c = look();
            if (isSeparator(c)) break;
            getChar();
            if ((c < '0') || (c > '9'))
                throw new NumberFormatException("Unrecognized '" + temp + c + "' in '" + expression + "' as a decimal number");
            temp += c;
        }

        return Integer.parseInt(temp, 10);
    }

    protected char getAlpha() throws ExpectedAlphaException {
        char c = look();

        if (validCharsInLiteral.indexOf(c) >= 0) {
            getChar();
            return c;
        }else{
            throw new ExpectedAlphaException();
        }
    }

    protected char getAlphaNumeric() throws ExpectedAlphaNumericException {
        char c = look();
        String validChars = validCharsInLiteral + "01234556789";

        if (validChars.indexOf(c) >= 0) {
            return getChar();
        }else{
            throw new ExpectedAlphaNumericException();
        }
    }

    protected String getLiteral() throws ExpectedAlphaNumericException, ExpectedAlphaException {
        eatSpaces();
        String temp = "" + getAlpha();
        for(;;) {
            if (isSeparator(look())) return temp;
            temp += getAlphaNumeric();
        }
    }

    protected String getTerm() throws UnrecognizedLiteralException {

        eatSpaces();

        switch (look()) {

            case '$':
                getChar();
                return "" + getHexNumber();

            case '%':
                getChar();
                return "" + getBinaryNumber();

            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return "" + getDecimalNumber();

            default:
                try {
                    String literal = getLiteral();
                    try {
                        return getConstantValue(literal);
                    } catch (UndefinedConstantException e1) {
                        pendingList.add(literal);
                        return " " + literal + " ";
                    }
                }catch (Exception e){
                    throw new UnrecognizedLiteralException(expression, look(), index);
                }

        }
    }

    protected String getConstantValue(String constantName) throws UndefinedConstantException {
        for (int i = 0; i < container.constants.size(); i++) {
            Valuable constant = container.constants.get(i);
            if (constant.match(constantName)) {
                return constant.getValue();
            }
        }

        throw new UndefinedConstantException(constantName);
    }

    public String preParse(String expression) throws UnrecognizedLiteralException {

        pendingList = new ArrayList<String>();
        index = 0;
        this.expression = expression;
        String result = "";

        while(index < expression.length()) {

            eatSpaces();

            char c = look();

            if (isDoubleQuote(c)){
                // respect literals quoted
                result += getChar();
                while (!isDoubleQuote(look())) {
                    result += getChar();
                }
                result += getChar();

            }else if (isSeparator(c)){
                result += getChar();
            }else{
                result += getTerm();
            }

        }

        return result;

    }

    public boolean arePendingLiterals() {
        return pendingList.size() > 0;
    }

    public ArrayList<String> getPendingList() {
        return pendingList;
    }

    public String solvePending(String expression, String pendingLabel, String pendingValue) throws UnrecognizedLiteralException {
        pendingLabel = " "+pendingLabel+" ";
        expression = preParse(expression);
        if (expression.contains(pendingLabel)) {
            expression = expression.replace(pendingLabel, pendingValue);
        }

        return preParse(expression);
    }

}
