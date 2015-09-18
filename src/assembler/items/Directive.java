package assembler.items;

import assembler.Tools;
import assembler.parser.ExpressionParser;
import assembler.parser.exceptions.UnrecognizedLiteralException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Directive extends Item {

    final public static int MAXDATA = 0x1000;
    final public static String _DB = ".db";
    final public static String _DW = ".dw";
    final public static String _ORG = ".org";

    protected String name;
    protected String value;
    protected ExpressionParser parser;

    protected final static String directives = "|.db|.dw|.org|";

//    public Directive(String name) throws Exception {
//        this(name, "");
//    }
//
//    public Directive(String name, String value) throws Exception {
//        this(name, "", value);
//    }
//
//    public Directive(String name, String label, String value) throws Exception {
//        this(name, label, value, "", null);
//
//    }

    public Directive(String name, String value, String src) throws Exception {
        this(name, value, src, null);
    }

    public Directive(String name, String value, String src, ExpressionParser parser) throws Exception {
        super(src);
        this.name = name;
        this.value = value;
        this.parser = parser;

        opCode = process();
    }

    public static boolean is(String word) {
        return directives.contains('|'+word+'|');
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    protected String parse(String expression) throws UnrecognizedLiteralException {
        if (parser!=null){
            return parser.preParse(expression);
        }
        return expression;
    }

    protected boolean isDB() {
        return name.toLowerCase().equals(_DB);
    }

    protected boolean isDW() {
        return name.toLowerCase().equals(_DW);
    }

    protected boolean isORG() {
        return name.toLowerCase().equals(_ORG);
    }

    protected int[] tokenizeOperands(String operands) throws Exception {

        int[] tmp = new int[MAXDATA];
        int index = 0;
        String current = "";
        operands += ' ';  // in order to process the last operand
        Tools.println("yellow", operands);
        boolean insideDoubleQuotes = false;
        for (int i = 0; i < operands.length(); i++) {
            char c = operands.charAt(i);

            switch (c) {
                case '"':
                    insideDoubleQuotes = !insideDoubleQuotes;
                    if (!insideDoubleQuotes) {
                        if (!current.equals("")) {
                            for (int j = 0; j < current.length(); j++) {
                                tmp[index++] = (byte) current.charAt(j);
                            }
                            current = "";
                        }
                    }
                    break;

                case ' ':
                case ',':
                    if (!insideDoubleQuotes) {
                        if (!current.equals("")) {
                            current = parse(current).trim();
//                            if (parser.arePendingLiterals()){
//                                if (isDB()) {
//                                    addPending(parser.getPendingList(), index, Pending.BYTE_LO);
//                                    tmp[index++] = 0;
//                                }else if (isDW()) {
//                                    addPending(parser.getPendingList(), index, Pending.BYTE_LO);
//                                    tmp[index++] = 0;
//                                    addPending(parser.getPendingList(), index+1, Pending.BYTE_HI);
//                                    tmp[index++] = 0;
//                                }
//                            }else{
//                                double d = Tools.eval(current);
//                                if (isDB()) {
//                                    tmp[index++] = (int) d & 0xff;
//                                } else if (isDW()) {
//                                    tmp[index++] = (int) d & 0xff;
//                                    tmp[index++] = (int) d >>> 8;
//                                }
//                            }
                            current = "";
                        }
                    } else {
                        current += c;
                    }
                    break;

                default:
                    current += c;
            }
        }

        return index > 0 ? Arrays.copyOfRange(tmp, 0, index) : null;
    }

    protected void addPending(ArrayList<String> pendings, int type, int pos) {
        for (int i = 0; i < pendings.size(); i++) {
            addPending(pendings.get(i), type, pos);
        }
    }

    /**
     * process the directive with the rest of the data
     *
     * @return int[]
     * @throws Exception
     */
    protected int[] process() throws Exception {

        switch (name.toLowerCase()) {

            case _DB:
            case _DW:
                return tokenizeOperands(value);

            case ".org":
                break;

        }

        return null;
    }


    @Override
    public String toString() {
        return src + " => Directive{ name='" + name +
                "', value='" + value + "' " +
                (hasPending() ? "    pending " : "") +
                "  [" + getOpCodeAsHexString(':') +
                "] }";
    }
}
