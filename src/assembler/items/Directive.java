package MyZ80.assembler.items;

import MyZ80.assembler.Tools;
import MyZ80.assembler.parser.exceptions.UnrecognizedLiteralException;
import MyZ80.DI.Container;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by joseluislaso on 06/09/15.
 *
 */
public class Directive extends Item {

    final public static int MAXDATA = 0x1000;
    final public static String _DB = ".db";
    final public static String _DW = ".dw";
    final public static String _ORG = ".org";

    protected String name;
    protected String value;
    protected Container container;

    private final static String directives = "|.db|.dw|.org|";

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
        this(name, value, 0, src);
    }

    public Directive(String name, String value, int address, String src) throws Exception {
        super(src, address);
        this.name = name;
        this.value = value;
        container = Container.getContainer();

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

    private String parse(String expression) throws UnrecognizedLiteralException {
        return container.expressionParser.preParse(expression);
    }

    boolean isDB() {
        return name.toLowerCase().equals(_DB);
    }

    boolean isDW() {
        return name.toLowerCase().equals(_DW);
    }

    public boolean isORG() {
        return name.toLowerCase().equals(_ORG);
    }

    private int[] tokenizeOperands(String operands) throws Exception {

        int[] tmp = new int[MAXDATA];
        int index = 0;
        String current = "";
        operands += ' ';  // in order to process the last operand
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
                            if (container.expressionParser.arePendingLiterals()){
                                if (isDB()) {
                                    addPending(container.expressionParser.getPendingList(), index, Pending.BYTE_LO);
                                    tmp[index++] = 0;
                                }else if (isDW()) {
                                    addPending(container.expressionParser.getPendingList(), index, Pending.BYTE_LO);
                                    tmp[index++] = 0;
                                    addPending(container.expressionParser.getPendingList(), index+1, Pending.BYTE_HI);
                                    tmp[index++] = 0;
                                }
                            }else{
                                double d = Tools.eval(current);
                                if (isDB()) {
                                    tmp[index++] = (int) d & 0xff;
                                } else if (isDW()) {
                                    tmp[index++] = (int) d & 0xff;
                                    tmp[index++] = (int) d >>> 8;
                                }
                            }
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

    private void addPending(ArrayList<String> pendings, int type, int pos) {
        for (String pending : pendings) {
            addPending(pending, type, pos);
        }
    }

    /**
     * process the directive with the rest of the data
     *
     * @return int[]
     * @throws Exception
     */
    int[] process() throws Exception {

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
        return src + " => Directive{ " +
                prettyAddress() +
                "name='" + name +
                "', value='" + value + "' " +
                (hasPending() ? "    pending " : "") +
                "  [" + getOpCodeAsHexString(':') +
                "] }";
    }
}
