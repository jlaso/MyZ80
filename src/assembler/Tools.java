package assembler;

/**
 * Created by joseluislaso on 08/09/15.
 */
public class Tools {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    final protected static String hexChars = "0123456789ABCDEF";

    public static String byteToHex(int b)
    {
        b = b & 0xff;
        return "" + hexChars.charAt(b >>> 4) + hexChars.charAt(b & 0x0F);
    }

    public static String addressToHex(int a)
    {
        return byteToHex(a >>> 8) + byteToHex(a);
    }

    public static String bytesToHex(int[] bytes) {
        String result = "";
        for ( int j = 0; j < bytes.length; j++ ) {
            result += byteToHex(bytes[j]);
        }
        return result;
    }

    protected static String color(String color) {
        switch (color ){
            case "black": return ANSI_BLACK;
            case "red": return ANSI_RED;
            case "yellow": return ANSI_YELLOW;
            case "cyan": return ANSI_CYAN;
            case "white": return ANSI_WHITE;
            case "blue": return ANSI_BLUE;
            case "green": return ANSI_GREEN;
            case "purple": return ANSI_PURPLE;
        }

        return "";
    }

    public static void println(String color, String text) {
        System.out.println(color(color)+text+ANSI_RESET);
    }

    public static void println_if(boolean print, String color, String text) {
        if (print) println(color, text);
    }

    /**
     * try to figure out which type of literal is and return as an integer value
     *
     * @param literal
     * @return
     */
    public static int figureOut(String literal)
    {
        int r;
        String lo = literal.toLowerCase();
        // trying hexadecimal ?
        if ((lo.charAt(0) == '&') || (lo.charAt(0) == '$')) {
            r = Integer.parseInt(lo.substring(1,lo.length()), 16);
            return r & 0xffff;
        }
        if (lo.charAt(literal.length() - 1) == 'h') {
            r = Integer.parseInt(lo.substring(0,lo.length()-1), 16);
            return r & 0xffff;
        }
        // trying binary ?
        if (lo.charAt(0) == '%') {
            r = Integer.parseInt(lo.substring(1,lo.length()), 2);
            return r & 0xffff;
        }
        // trying octal ?
        if (lo.charAt(literal.length() - 1) == 'o') {
            r = Integer.parseInt(lo.substring(0,lo.length()-1), 8);
            return r & 0xffff;
        }
        // in other case should be decimal
        r = Integer.parseInt(lo);

        return r & 0xffff;
    }

    /**
     * Thanks http://stackoverflow.com/users/964243/boann
     *
     * @param str
     * @return
     */
    public static double eval(final String str) {
        class Parser {
            int pos = -1, c;

            void eatChar() {
                c = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            void eatSpace() {
                while (Character.isWhitespace(c)) eatChar();
            }

            double parse() {
                eatChar();
                double v = parseExpression();
                if (c != -1) throw new RuntimeException("Unexpected: " + (char)c);
                return v;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor | term brackets
            // factor = brackets | number | factor `^` factor
            // brackets = `(` expression `)`

            double parseExpression() {
                double v = parseTerm();
                for (;;) {
                    eatSpace();
                    if (c == '+') { // addition
                        eatChar();
                        v += parseTerm();
                    } else if (c == '-') { // subtraction
                        eatChar();
                        v -= parseTerm();
                    } else {
                        return v;
                    }
                }
            }

            double parseTerm() {
                double v = parseFactor();
                for (;;) {
                    eatSpace();
                    if (c == '/') { // division
                        eatChar();
                        v /= parseFactor();
                    } else if (c == '*' || c == '(') { // multiplication
                        if (c == '*') eatChar();
                        v *= parseFactor();
                    } else {
                        return v;
                    }
                }
            }

            double parseFactor() {
                double v;
                boolean negate = false;
                eatSpace();
                if (c == '+' || c == '-') { // unary plus & minus
                    negate = c == '-';
                    eatChar();
                    eatSpace();
                }
                if (c == '(') { // brackets
                    eatChar();
                    v = parseExpression();
                    if (c == ')') eatChar();
                } else { // numbers
                    StringBuilder sb = new StringBuilder();
                    while ((c >= '0' && c <= '9') || c == '.') {
                        sb.append((char)c);
                        eatChar();
                    }
                    if (sb.length() == 0) throw new RuntimeException("Unexpected: " + (char)c);
                    v = Double.parseDouble(sb.toString());
                }
                eatSpace();
                if (c == '^') { // exponentiation
                    eatChar();
                    v = Math.pow(v, parseFactor());
                }
                if (negate) v = -v; // unary minus is applied after exponentiation; e.g. -3^2=-9
                return v;
            }
        }
        return new Parser().parse();
    }
}
