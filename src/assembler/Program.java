package assembler;

import assembler.parser.ProgramParser;
import common._;
import di.Container;
import assembler.items.*;
import fileFormat.Z80FileFormat;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Program {

    final protected static int UNKNOWN = 0;
    final protected static int TOKEN = 1;
    final protected static int CONSTANT = 2;
    final protected static int DIRECTIVE = 3;
    final protected static int INCLUDE = 4;
    final protected static int LABEL = 5;

    protected Container container;
    protected ArrayList<Item> program = new ArrayList<Item>();
    protected String fileName;
    protected String baseFileName;
    protected int org = 0;
    protected ProgramParser programParser;
    protected boolean debug;

    public Program(String file, boolean debug) {
        container = Container.getContainer();
        fileName = file;
        baseFileName = file.substring(0, file.lastIndexOf('.'));
        programParser = new ProgramParser();
        this.debug = debug;
    }

//    /**
//     * tokenize the line passed and return the corresponding Item
//     *
//     * @param line String
//     * @return Item
//     * @throws Exception
//     */
//    protected Item tokenize(String line) throws Exception {
//
//        String[] temp = new String[]{"", "", ""};
//        int type = UNKNOWN;
//        int current = 0;
//        boolean insideDoubleQuotes = false;
//
//        char_loop:
//        for (int i = 0; i < line.length(); i++) {
//            char c = line.charAt(i);
//
//            switch (c) {
//                case '"':
//                    insideDoubleQuotes = !insideDoubleQuotes;
//                    if (!insideDoubleQuotes && !temp[current].equals("") && (type == DIRECTIVE)) {
//                        temp[current] += c;
//                        break char_loop;     // ?????
//                    }
//                    temp[current] += c;
//                    break;
//
//                case ';':
//                    if (!insideDoubleQuotes) {
//                        break char_loop;  // comment ends tokenize process
//                    }
//                    temp[current] += c;
//                    break;
//
//                case ':':
//                    if (!insideDoubleQuotes) {
//                        if (0 == current) { // label ends with :   label only accepted in the first part (0)
//                            type = LABEL;
//                            break char_loop;
//                        }
//                    }
//                    temp[current] += c;
//                    break;
//
//                case ',':
//                    if (insideDoubleQuotes || (current == temp.length) || DIRECTIVE == type) {
//                        temp[current] += c;
//                    } else {
//                        current++;
//                    }
//                    break;
//
//                case ' ':
//                    if (insideDoubleQuotes || (DIRECTIVE == type)) {
//                        temp[current] += c;
//                    } else {
//
//                        if (!temp[current].equals("")) {
//                            if (UNKNOWN == type) {
//                                if (temp[0].charAt(0) == '.') {
//                                    type = DIRECTIVE;
//                                } else if (temp[0].toLowerCase().equals("#define")) {
//                                    type = CONSTANT;
//                                } else if (temp[0].toLowerCase().equals("#include")) {
//                                    type = INCLUDE;
//                                } else if ((current > 0) && (temp[1].toLowerCase().equals("equ"))) {
//                                    type = CONSTANT;
//                                } else if (current > 0) {
//                                    type = TOKEN;
//                                }
//                            }
//                            current++;
//                        } else {
//                            temp[current] += c;
//                        }
//
//                    }
//                    break;
//
//                default:
//                    temp[current] += c;
//                    break;
//            }
//
//            if (current >= temp.length) {
//                break;
//            }
//        }
//
//        if (!temp[0].equals("")) {
//
//            switch (type) {
//                case CONSTANT:
//                    Constant constant = new Constant(temp[1], temp[2], line);
//                    container.constants.add(constant);
//                    Tools.println_if(debug, "green", _.TAB + constant.toString());
//                    return constant;
//
//                case DIRECTIVE:
//                    Tools.println_if(debug, "yellow", "~~~~~~~ temp[0]='" + temp[0] + "', temp[1]='" + temp[1] + "', temp[2]='" + temp[2] + "' ~~~~~~");
//                    temp[1] = evaluate(temp[1]);
//                    temp[2] = evaluate(temp[2]);
//                    Tools.println_if(debug, "green", "~~~~~~~ temp[0]='" + temp[0] + "', temp[1]='" + temp[1] + "', temp[2]='" + temp[2] + "' ~~~~~~");
//                    Directive directive = new Directive(temp[0], temp[1] + " " + temp[2], line);
//                    //Tools.println("green", "\t"+temp[0]+"|\t"+temp[1]+"|\t"+temp[2]+"|");
//                    Tools.println_if(debug, "red", _.TAB + directive.toString());
//                    return directive;
//
//                case INCLUDE:
//                    // have to include the file in the
//                    System.out.println(_.TAB + "have to include the file " + temp[1]);
//                    break;
//
//                case TOKEN:
//                    Token token = new Token(temp[0], temp[1], temp[2], line);
//                    Tools.println_if(debug, "purple", _.TAB + token.toString());
//                    return token;
//
//                case LABEL:
//                    Label label = new Label(temp[0], line);
//                    labels.add(label);
//                    return label;
//
//            }
//        }
//
//        return null;
//    }

    public void debug(String message, String color) {
        if (debug) {
            Tools.println(color, message);
        }
    }

    public void assemble() throws Exception {

        int address = 0;
        int lineNumber = 1;

        // pass 1
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                Tools.println("blue", line);

                if (!line.isEmpty()) {
                    //Item item = tokenize(line);
                    Item item = programParser.itemizeLine(line, address, lineNumber);

                    if (null != item) {
                        if ((item instanceof Directive) && ((Directive) item).isORG()) {
                            org = address = Integer.parseInt(((Directive) item).getValue());
                            debug(_.TAB + _.TAB + _.TAB + "~~~~==** org address " + address + " **==~~~~", _.YELLOW);
                        }
                        //item.setAddress(address);
                        address += item.getSize();
                        program.add(item);

                        if (item instanceof Constant) {
                            container.constants.add((Constant)item);
                        }else if (item instanceof Label) {
                            container.constants.add((Label)item);
                            if (((Label)item).match("Description")){
                              //  System.exit(0);
                            }
                        }
                    }
                }

                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // pass 2  -  ??
        dumpPendings();

        // pass 3  -  solve labels
        debug(_.CR + _.CR + "~~~~~~~~~~ propagating labels ~~~~~~~~~" + _.CR, _.RED);
        for (Item item : program) {
            if (item instanceof Label) propagateLabel((Label)item);
        }

    }

    protected void propagateLabel(Label label) {
        Tools.println_if(debug, "", "~^~ propagating label " + label.getLabel() + " " + label.getAddress() + " ~^~");
        for (Item item: program) {
            item.solvePending(label.getLabel(), label.getAddress());
        }
    }
//
//    protected String evaluate(String formula) throws Exception {
//
//        if (formula.isEmpty()) return "";
//
//        formula = container.expressionParser.preParse(formula);
//
//        if (container.expressionParser.arePendingLiterals()) {
//            return formula;
//        }
//
//        if (formula.contains(",") || formula.contains("\"")) return formula; //resolveConstants(formula);
//
//        String acum = formula; /*
//
//        formula += " ";  // in order to process last term
//        String current = "";
//        String acum = "";
//
//        for (int i=0; i<formula.length(); i++) {
//            char c = formula.charAt(i);
//
//            switch (c) {
//
//                case ')':
//                case ' ':
//                case '-':
//                case '+':
//                case '/':
//                case '*':
//                    // process
//                    if (!current.equals("")) {
//                        try {
//                            current = "" + Tools.figureOut(current);
//                        } catch (NumberFormatException e) {
//                            try {
//                                current = getValueOf(current);
//                            } catch (Exception exp) {
//                                throw new Exception("Unknown term " + current + " in field");
//                            }
//                        }
//                    }
//                    acum += current + c;
//                    current = "";
//                    break;
//
//                default:
//                    current += c;
//                    break;
//            }
//        }
//        */
//        debug("(O) formula='" + formula + "' ~~~> '" + acum + "'", _.RED);
//
//        return "" + (int) Tools.eval(acum);
//    }

//
//    protected String resolveConstants(String formula) {
//
//        if (formula.equals("")) return "";
//
//        formula += " ";  // in order to process last term
//        String current = "";
//        String acum = "";
//
//        for (int i=0; i<formula.length(); i++) {
//            char c = formula.charAt(i);
//
//            switch (c) {
//
//                case '(':
//                case ',':
//                case ')':
//                case ' ':
//                case '-':
//                case '+':
//                case '/':
//                case '*':
//                    if (!current.equals("")) {
//                        try {
//                            int a = Tools.figureOut(current);
//                        }catch (NumberFormatException e) {
//                            try {
//                                current = getValueOf(current);
//                            } catch (Exception exp) {
//
//                            }
//                        }
//                    }
//                    acum += current + c;
//                    current = "";
//                    break;
//
//                default:
//                    current += c;
//                    break;
//            }
//        }
//
//        Tools.println("red", "(C) formula='"+formula+"' ~~~> '"+acum+"'");
//
//        return acum;
//    }
//
//    protected String evaluateNOOOOO(String formula) {
//
//        if (formula.equals("")) return "";
//
//        for (int i = 0; i < constants.size(); i++) {
//            Constant c = constants.get(i);
//            if (formula.contains(c.getName())) {
//                formula = formula.replace(c.getName(), ""+c.getValue());
//            }
//        }
//
//        String[] terms = formula.split("[+|-]");
//        int term;
//
//        for (int i = 0; i < terms.length; i++) {
//            try{
//                term = Tools.figureOut(terms[i]);
//                formula = formula.replace(terms[i], "" + term);
//            }catch (NumberFormatException e){
//                return formula;
//            }
//        }
//
//        return "" + (int) Tools.eval(formula);
//    }

    protected String getValueOf(String constantName) throws Exception {
        for (Valuable constant : container.constants) {
            if (constant.match(constantName)) {
                return constant.getValue();
            }
        }

        throw new Exception("Constant " + constantName + " not declared yet!");
    }

    protected void dumpLabels() {
        if (!debug) return;
        Tools.println("red", _.CR + _.CR + "~~~~~~~~~~ labels ~~~~~~~~~~");
        for (Item item : program) {
            if (item instanceof Label) Tools.println("", item.toString());
        }
    }

    protected void dumpPendings() {
        if (!debug) return;
        Tools.println("red", _.CR + _.CR + "~~~~~~~~~~ pendings ~~~~~~~~~~");
        for (Item item : program) {
            for (Pending pending : item.getPendingList()) {
                Tools.println("", pending.toString() + " " + item.toString());
            }
        }
    }

    public void dumpHexProgram() {
        String hex = "";
        for (int i = 0; i < program.size(); i++) {
            Item item = program.get(i);

            if (item.getSize() > 0) {
                hex += item.getOpCodeAsHexString(' ');
            }
        }
        System.out.println(hex);
    }

    /**
     * @param outputFile  String
     * @param maxFileSize int
     */
    public void saveBin(String outputFile, int maxFileSize) {

        Tools.println_if(debug, "red", _.CR + _.CR + "~~~~~~~~~~~~ dump ~~~~~~~~~~~");

        Z80FileFormat z80file = new Z80FileFormat(maxFileSize);
        z80file.setPC(org);

        for (Item item : program) {

            System.out.println(item.toString());

            if (item.getSize() > 0) {
                z80file.addData(item.getAddress(), item.getOpCode());
            }
        }

        // save bin file
        try {

            z80file.saveToFile(outputFile);

            dumpLabels();
            dumpPendings();

            Tools.println("red", _.CR + _.CR + _.TAB + "O~~=> generated " + outputFile + " with " + maxFileSize + " bytes. <=~~O");

        } catch (IOException e) {

            System.out.println ("Some error happened saving binary file." + _.CR + e.getMessage());
            System.exit(-1);
        }

    }

}
