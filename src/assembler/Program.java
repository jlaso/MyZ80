package assembler;

import assembler.parser.*;
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

    protected ArrayList<Item> program = new ArrayList<Item>();
    protected ArrayList<Label> labels = new ArrayList<Label>();
    protected ArrayList<Constant> constants = new ArrayList<Constant>();
    protected String fileName;
    protected int maxFileSize;
    protected String baseFileName;
    protected int org = 0;

    public Program(String file, int maxFileSize) {
        fileName = file;
        this.maxFileSize = maxFileSize;
        baseFileName = file.substring(0, file.lastIndexOf('.'));
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
        boolean insideSingleQuotes = false;

        char_loop:
        for (int i=0; i<line.length(); i++) {
            char c = line.charAt(i);

            switch (c) {
                case '"':
                    if (!insideSingleQuotes) {
                        insideDoubleQuotes = !insideDoubleQuotes;
                        if (!insideDoubleQuotes && !temp[current].equals("") && (type == DIRECTIVE)) {
                            temp[current] += c;
                            break char_loop;
                        }
                    }
                    temp[current] += c;
                    break;

                case '\'':
                    if (!insideDoubleQuotes) {
                        insideSingleQuotes = !insideSingleQuotes;
                        if (!insideSingleQuotes && !temp[current].equals("") && (type == DIRECTIVE)) {
                            temp[current] += c;
                            break char_loop;
                        }
                    }
                    temp[current] += c;
                    break;

                case ';':
                    if (!insideDoubleQuotes && !insideSingleQuotes) {
                        break char_loop;  // comment ends tokenize process
                    }
                    temp[current] += c;
                    break;

                case ':':
                    if (!insideDoubleQuotes && !insideSingleQuotes) {
                        if (0 == current) { // label ends with :   label only accepted in the first part (0)
                            type = LABEL;
                            break char_loop;
                        }
                    }
                    temp[current] += c;
                    break;

                case ',':
                case ' ':
                    if (!insideDoubleQuotes && !insideSingleQuotes) {
                        if (((',' == c) && (DIRECTIVE != type)) || (' ' == c)) {
                            if (!temp[current].equals("")) {
                                if (UNKNOWN == type) {
                                    if ((0 == current) && (temp[current].charAt(0) == '.')) {
                                        type = DIRECTIVE;
                                    } else if ((0 == current) && (temp[current].toLowerCase().equals("#define"))) {
                                        type = CONSTANT;
                                    } else if ((0 == current) && (temp[current].toLowerCase().equals("#include"))) {
                                        type = INCLUDE;
                                    } else if ((1 == current) && (temp[current].toLowerCase().equals("equ"))) {
                                        type = CONSTANT;
                                    } else {
                                        type = TOKEN;
                                    }
                                }
                                current++;
                            }
                        } else {
                            temp[current] += c;
                        }
                    }else{
                        temp[current] += c;
                    }
                    break;

                default:
                    temp[current] += c;
                    break;
            }

            if (current > temp.length) {
                break;
            }
        }

        if (!temp[0].equals("")) {

            switch (type) {
                case CONSTANT:
                    Constant constant = new Constant(temp[1], temp[2]);
                    constants.add(constant);
                    Tools.println("green", "\t"+constant.toString());
                    return constant;

                case DIRECTIVE:
                    Tools.println("yellow", "~~~~~~~ temp[0]='" + temp[0] + "', temp[1]='" + temp[1] + "', temp[2]='" + temp[2] + "' ~~~~~~");
                    temp[1] = evaluate(temp[1]);
                    temp[2] = evaluate(temp[2]);
                    Tools.println("green","~~~~~~~ temp[0]='"+temp[0]+"', temp[1]='"+temp[1]+"', temp[2]='"+temp[2]+"' ~~~~~~");
                    Directive directive = new Directive(temp[0], temp[1], temp[2]);
                    //Tools.println("green", "\t"+temp[0]+"|\t"+temp[1]+"|\t"+temp[2]+"|");
                    Tools.println("red", "\t"+directive.toString());
                    return directive;

                case INCLUDE:
                    // have to include the file in the
                    System.out.println("\thave to include the file "+temp[1]);
                    break;

                case TOKEN:
                    Token token = new Token(temp[0], temp[1], temp[2]);
                    Tools.println("purple", "\t"+token.toString());
                    return token;

                case LABEL:
                    Label label = new Label(temp[0]);
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

        // pass 3  -  solve labels
        for (int i=0; i<labels.size(); i++){
            propagateLabel(labels.get(i));
        }

    }

    protected void propagateLabel(Label label) {
        for (int i = 0; i < program.size(); i++) {
            program.get(i).solvePending(label.getLabel(), label.getAddress());
        }
    }

    protected String evaluate(String formula) {

        if (formula.equals("")) return "";

        for (int i = 0; i < constants.size(); i++) {
            Constant c = constants.get(i);
            if (formula.contains(c.getName())) {
                formula = formula.replace(c.getName(), ""+c.getValue());
            }
        }

        String[] terms = formula.split("[+|-]");
        int term;

        for (int i = 0; i < terms.length; i++) {
            try{
                term = Tools.figureOut(terms[i]);
                formula = formula.replace(terms[i], "" + term);
            }catch (NumberFormatException e){
                return formula;
            }
        }

        return "" + (int) Tools.eval(formula);
    }

//    protected int getConstantValue(String term) {
//        for (int i = 0; i < constants.size(); i++) {
//            Constant c = constants.get(i);
//            if (term.equals(c.getName())) {
//                return c.getValue();
//            }
//        }
//
//        return 0;
//    }

    protected int getValueOfConstant(String constant) throws Exception {
        for (int i = 0; i < constants.size(); i++) {
            Constant c = constants.get(i);
            if (c.getName().equals(constant)) {
                return c.getValue();
            }
        }

        throw new Exception("Constant "+constant+" not declared yet!");
    }

    /**
     *
     * @param labelSrch
     * @return
     * @throws Exception
     */
    protected int getValueOfLabel(String labelSrch) throws Exception {
        for (int l=0; l<labels.size(); l++){
            Label label = labels.get(l);
            if (label.getLabel().equals(labelSrch)){
                return label.getAddress();
            }
        }

        throw new Exception("label '"+labelSrch+"' was not found");
    }

    public void saveBin() {

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
            z80file.saveToFile(baseFileName+".bin");
        }catch ( IOException e) {

        }

    }

}
