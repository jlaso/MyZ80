package assembler.parser;

import assembler.items.Constant;

import java.util.ArrayList;

/**
 * Created by joseluislaso on 14/09/15.
 */
public class ExpressionParser {

    protected static char END = ' ';
    protected String expression;
    protected int index;
    protected char current;
    protected ArrayList<Constant> constants = new ArrayList<Constant>();

    public ExpressionParser(ArrayList<Constant> constants) {
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

    protected char lookLowerCase() {

        return expression.toLowerCase().charAt(index);

    }

    protected boolean isSeparator(char c) {
        return ((c==END) || (c == ' ') || (c == '+') || (c == '-') || (c == '*') || (c == '/'));
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

    protected String getLiteral() {
        String temp = "";
        for (; ; ) {
            char c = look();
            if (isSeparator(c)) break;
            getChar();
            temp += c;
        }

        return temp;
    }

    protected String getTerm() {

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
                String literal = getLiteral();
                try {
                    int value = getConstantValue(literal);
                    return "" + value;
                } catch (UndefinedConstantException e) {
                    return " #"+literal+"# ";
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

        throw new UndefinedConstantException();
    }

    public String preParse(String expression) {

        index = 0;
        this.expression = expression;
        String result = "";

        while(index < expression.length()) {

            eatSpaces();

            char c = look();

            if (isSeparator(c)){
                result += getChar();
            }else{
                result += getTerm();
            }

        }

        return result;

    }


}
