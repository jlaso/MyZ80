package assembler;

import assembler.items.*;
import assembler.parser.ExpressionParser;
import fileFormat.Z80FileFormat;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Program {

    protected static Program instance = null;

    final protected static int UNKNOWN = 0;
    final protected static int TOKEN = 1;
    final protected static int CONSTANT = 2;
    final protected static int DIRECTIVE = 3;
    final protected static int INCLUDE = 4;
    final protected static int LABEL = 5;

    protected ArrayList<Item> program = new ArrayList<Item>();
    protected ArrayList<Label> labels = new ArrayList<Label>();
    protected ArrayList<Valuable> constants = new ArrayList<Valuable>();
    protected String fileName;
    protected String baseFileName;
    protected int org = 0;

    protected ExpressionParser parser;

    public static Program getInstance() {
        return instance;
    }

    public Program(String file) {
        fileName = file;
        baseFileName = file.substring(0, file.lastIndexOf('.'));
        parser = new ExpressionParser(constants);
        instance = this;
    }

    public ExpressionParser getParser() {
        return parser;
    }

    /**
     * tokenize the line passed and return the corresponding Item
     *
     * @param line String
     * @return Item
     * @throws Exception
     */
    protected Item tokenize(String line) throws Exception {

        String[] temp = new String[] {"","",""};
        int type = UNKNOWN;
        int current = 0;
        boolean insideDoubleQuotes = false;

        char_loop:
        for (int i=0; i<line.length(); i++) {
            char c = line.charAt(i);

            switch (c) {
                case '"':
                    insideDoubleQuotes = !insideDoubleQuotes;
                    if (!insideDoubleQuotes && !temp[current].equals("") && (type == DIRECTIVE)) {
                        temp[current] += c;
                        break char_loop;     // ?????
                    }
                    temp[current] += c;
                    break;

                case ';':
                    if (!insideDoubleQuotes) {
                        break char_loop;  // comment ends tokenize process
                    }
                    temp[current] += c;
                    break;

                case ':':
                    if (!insideDoubleQuotes) {
                        if (0 == current) { // label ends with :   label only accepted in the first part (0)
                            type = LABEL;
                            break char_loop;
                        }
                    }
                    temp[current] += c;
                    break;

                case ',':
                    if (insideDoubleQuotes || (current==temp.length) || DIRECTIVE == type) {
                        temp[current] += c;
                    } else {
                        current++;
                    }
                    break;

                case ' ':
                    if (insideDoubleQuotes || (DIRECTIVE == type)) {
                        temp[current] += c;
                    }else{

                        if (!temp[current].equals("")) {
                            if (UNKNOWN == type) {
                                if (temp[0].charAt(0) == '.') {
                                    type = DIRECTIVE;
                                } else if (temp[0].toLowerCase().equals("#define")) {
                                    type = CONSTANT;
                                } else if (temp[0].toLowerCase().equals("#include")) {
                                    type = INCLUDE;
                                } else if ((current > 0) && (temp[1].toLowerCase().equals("equ"))) {
                                    type = CONSTANT;
                                } else if (current > 0) {
                                    type = TOKEN;
                                }
                            }
                            current++;
                        } else {
                            temp[current] += c;
                        }

                    }
                    break;

                default:
                    temp[current] += c;
                    break;
            }

            if (current >= temp.length) {
                break;
            }
        }

        if (!temp[0].equals("")) {

            switch (type) {
                case CONSTANT:
                    Constant constant = new Constant(temp[1], temp[2], line);
                    constants.add(constant);
                    Tools.println("green", "\t"+constant.toString());
                    return constant;

                case DIRECTIVE:
                    Tools.println("yellow", "~~~~~~~ temp[0]='" + temp[0] + "', temp[1]='" + temp[1] + "', temp[2]='" + temp[2] + "' ~~~~~~");
                    temp[1] = evaluate(temp[1]);
                    temp[2] = evaluate(temp[2]);
                    Tools.println("green","~~~~~~~ temp[0]='"+temp[0]+"', temp[1]='"+temp[1]+"', temp[2]='"+temp[2]+"' ~~~~~~");
                    Directive directive = new Directive(temp[0], temp[1]+" "+temp[2], line, parser);
                    //Tools.println("green", "\t"+temp[0]+"|\t"+temp[1]+"|\t"+temp[2]+"|");
                    Tools.println("red", "\t"+directive.toString());
                    return directive;

                case INCLUDE:
                    // have to include the file in the
                    System.out.println("\thave to include the file "+temp[1]);
                    break;

                case TOKEN:
                    Token token = new Token(temp[0], temp[1], temp[2], line, parser);
                    Tools.println("purple", "\t"+token.toString());
                    return token;

                case LABEL:
                    Label label = new Label(temp[0], line);
                    labels.add(label);
                    return label;

            }
        }

        return null;
    }

    public void assemble() throws Exception {

        int address = 0;

        // pass 1
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                Tools.println("blue", line);

                if (""!=line) {
                    Item item = tokenize(line);

                    if (item instanceof Directive) {
                        Directive directive = ((Directive) item);
                        if (directive.getName().equals(Directive._ORG)) {
                            org = address = Integer.parseInt(directive.getValue());
                            Tools.println("yellow","\t\t\t~~~~==** org address "+address+" **==~~~~");
                        }
                    }
                    if (null!=item) {
                        item.setAddress(address);
                        address += item.getSize();

                        program.add(item);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // pass 2  -  ??

        dumpPendings();

        // pass 3  -  solve labels
        Tools.println("red", "\n\n~~~~~~~~~~ propagating labels ~~~~~~~~~\n");
        for (int i=0; i<labels.size(); i++){
            Tools.println("", "~^~ propagating label "+labels.get(i).getLabel()+" "+labels.get(i).getAddress()+" ~^~");
            propagateLabel(labels.get(i));
        }

    }

    protected void propagateLabel(Label label) {
        for (int i = 0; i < program.size(); i++) {
            program.get(i).solvePending(label.getLabel(), label.getAddress());
        }
    }

    protected String evaluate(String formula) throws Exception {

        //  $dd+constant/$a0

        if (formula.equals("")) return "";

        formula = parser.preParse(formula);

        if (parser.arePendingLiterals()) {
            return formula;
        }

        if (formula.contains(",") || formula.contains("\"")) return formula; //resolveConstants(formula);

        String acum = formula; /*

        formula += " ";  // in order to process last term
        String current = "";
        String acum = "";

        for (int i=0; i<formula.length(); i++) {
            char c = formula.charAt(i);

            switch (c) {

                case ')':
                case ' ':
                case '-':
                case '+':
                case '/':
                case '*':
                    // process
                    if (!current.equals("")) {
                        try {
                            current = "" + Tools.figureOut(current);
                        } catch (NumberFormatException e) {
                            try {
                                current = getValueOf(current);
                            } catch (Exception exp) {
                                throw new Exception("Unknown term " + current + " in field");
                            }
                        }
                    }
                    acum += current + c;
                    current = "";
                    break;

                default:
                    current += c;
                    break;
            }
        }
        */
        Tools.println("red", "(O) formula='"+formula+"' ~~~> '"+acum+"'");

        return "" + (int) Tools.eval(acum);
    }

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

    protected String getValueOf(String constant) throws Exception {
        for (int i = 0; i < constants.size(); i++) {
            Valuable c = constants.get(i);
            if (c.match(constant)) {
                return c.getValue();
            }
        }

        throw new Exception("Constant "+constant+" not declared yet!");
    }
//
//    /**
//     *
//     * @param labelSrch
//     * @return
//     * @throws Exception
//     */
//    protected int getValueOfLabel(String labelSrch) throws Exception {
//        for (int l=0; l<labels.size(); l++){
//            Label label = labels.get(l);
//            if (label.getLabel().equals(labelSrch)){
//                return label.getAddress();
//            }
//        }
//
//        throw new Exception("label '"+labelSrch+"' was not found");
//    }

    protected void dumpLabels() {
        Tools.println("red", "\n\n~~~~~~~~~~ labels ~~~~~~~~~~");
        for (int i = 0; i < labels.size(); i++) {
            Tools.println("", labels.get(i).toString());
        }
    }

    protected void dumpPendings() {
        Tools.println("red", "\n\n~~~~~~~~~~ pendings ~~~~~~~~~~");
        for (int i = 0; i < program.size(); i++) {
            ArrayList<Pending> p = program.get(i).getPendingList();
            for (int j = 0; j < p.size(); j++) {
                Tools.println("", p.get(j).toString()+ " " + program.get(i).toString());
            }
        }
    }

    /**
     *
     * @param outputFile
     * @param maxFileSize
     */
    public void saveBin(String outputFile, int maxFileSize) {

        /*
        String hex = "";
        for (int i=0; i<program.size(); i++){
            Item item = program.get(i);

            if (item.getSize() > 0){
                hex += item.getOpCodeAsHexString(' ');
            }
        }
        System.out.println(hex);
        */

        Tools.println("red", "\n\n~~~~~~~~~~~~ dump ~~~~~~~~~~~");

        Z80FileFormat z80file = new Z80FileFormat(maxFileSize);
        z80file.setPC(org);

        for (int i=0; i<program.size(); i++){
            Item item = program.get(i);

            System.out.println(item.toString());

            if (item.getSize() > 0) {
                z80file.addData(item.getAddress(), item.getOpCode());
            }
        }

        // save bin file
        try {

            z80file.saveToFile(outputFile);

        }catch ( IOException e) {

        }

        dumpLabels();

        dumpPendings();

        Tools.println("red", "\n\n\tO~~=> generated "+outputFile+" with "+maxFileSize+" bytes. <=~~O");

    }

}
