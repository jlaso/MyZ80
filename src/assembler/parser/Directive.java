package assembler.parser;

import assembler.Tools;

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
    protected String label;

    public Directive(String name) throws Exception {
        this(name, "");
    }

    public Directive(String name, String value) throws Exception {
        this(name, "", value);
    }

    public Directive(String name, String label, String value) throws Exception {
        this.name = name;
        this.value = isDB() || isDW() || isORG() ? label : value;
        this.label = isDB() || isDW() || isORG() ? "" : label;

        opCode = process();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
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
        operands += ',';  // in order to process the last operand
        boolean insideDoubleQuotes = false;
        boolean insideSingleQuotes = false;
        for (int i = 0; i < operands.length(); i++) {
            char c = operands.charAt(i);

            switch (c) {
                case '"':
                    if (!insideSingleQuotes) {
                        insideDoubleQuotes = !insideDoubleQuotes;
                        if (!insideDoubleQuotes) {
                            if (!current.equals("")) {
                                for (int j = 0; j < current.length(); j++) {
                                    tmp[index++] = (byte) current.charAt(j);
                                }
                                current = "";
                            }
                        }
                    } else {
                        current += c;
                    }
                    break;

                case '\'':
                    if (!insideDoubleQuotes) {
                        insideSingleQuotes = !insideSingleQuotes;
                        if (!insideSingleQuotes) {
                            if (!current.equals("")) {
                                for (int j = 0; j < current.length(); j++) {
                                    tmp[index++] = (byte) current.charAt(j);
                                }
                                current = "";
                            }
                        }
                    } else {
                        current += c;
                    }
                    break;

                case ' ':
                case ',':
                    if (!insideDoubleQuotes && !insideSingleQuotes) {
                        if (!current.equals("")) {
                            try {
                                int k = Tools.figureOut(current);
                                if (isDB()) {
                                    tmp[index++] = (byte) k;
                                } else if (isDW()) {
                                    tmp[index++] = (byte) (k & 0xff);
                                    tmp[index++] = (byte) (k >>> 8);
                                }
                            } catch (Exception e) {
                                if (isDB()) {
                                    addPending(current, index, Pending.BYTE_LO);
                                    tmp[index++] = 0;
                                } else if (isDW()) {
                                    addPending(current, index, Pending.ADDRESS);
                                    tmp[index++] = 0;
                                    tmp[index++] = 0;
                                } else {
                                    throw new Exception("Unknow " + current + " on this directive type");
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
        return "Directive{ name='" + name +
                "',label='" + label +
                "', value='" + value + "' " +
                (hasPending() ? "    pending " : "") +
                "  [" + getOpCodeAsHexString(':') +
                "] }";
    }
}
