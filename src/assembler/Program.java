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

    public void debug(String message, String color) {
        if (debug) {
            Tools.println(color == null ? "" : color, message);
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

        debug(_.CR + _.CR, null);

    }

    protected void propagateLabel(Label label) {
        Tools.println_if(debug, "", "~^~ propagating label " + label.getLabel() + " " + label.getAddress() + " ~^~");
        for (Item item: program) {
            item.solvePending(label.getLabel(), label.getAddress());
        }
    }

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

    public void dumpProgram() {
        Tools.println(_.RED, _.CR+_.TAB+"~~~~~~  dumping assembled program ~~~~~~"+_.CR);
        String hex = "";
        for (Item item : program) {
            System.out.println(item.toString());
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
