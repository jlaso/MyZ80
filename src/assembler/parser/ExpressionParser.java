package assembler.parser;

import assembler.items.Constant;
import assembler.items.Label;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by joseluislaso on 14/09/15.
 */
public class ExpressionParser {

    final protected static char END = ' ';
    protected String expression;
    protected int index;
    protected char current;
    protected ArrayList<Constant> constants = new ArrayList<Constant>();
    protected ArrayList<Label> labels = new ArrayList<Label>();
    final protected static String validCharsInLiteral = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
    protected ArrayList<String> pendingList = new ArrayList<String>();

    public ExpressionParser(ArrayList<Constant> constants, ArrayList<Label> labels) {
        this.labels = labels;
        this.constants = constants;
    }

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
        return ((c==END) || (c == ' ') || (c == '+') || (c == '-') || (c == '*') || (c == '/') || (c == ','));
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
                        int value = getConstantValue(literal);
                        return "" + value;
                    } catch (UndefinedConstantException e1) {
                        try {
                            int value = getLabelValue(literal);
                            return ""+value;
                        }catch (UndefinedLabelException e2) {
                            pendingList.add(literal);
                            return " " + literal + " ";
                        }
                    }
                }catch (Exception e){
                    throw new UnrecognizedLiteralException(expression, look(), index);
                }

        }
    }

    protected int getConstantValue(String constantName) throws UndefinedConstantException {
        for (int i = 0; i < constants.size(); i++) {
            Constant constant = constants.get(i);
            if (constant.getName().equals(constantName)) {
                return constant.getValue();
            }
        }

        throw new UndefinedConstantException(constantName);
    }

    protected int getLabelValue(String labelName) throws UndefinedLabelException {
        for (int i = 0; i < labels.size(); i++) {
            Label label = labels.get(i);
            if (label.getLabel().equals(labelName)) {
                return label.getAddress();
            }
        }

        throw new UndefinedLabelException(labelName);
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
