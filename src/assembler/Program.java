package assembler;

import assembler.parser.ProgramParser;
import common.$;
import di.Container;
import assembler.items.*;
import fileFormat.Z80FileFormat;
import samples.Samples;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Program {

    protected Container container;
    protected ArrayList<Item> program = new ArrayList<>();
    protected String fileName, baseFileName;
    protected int org = 0, address;
    protected boolean orgDefined = false, debug;
    protected ProgramParser programParser;

    /**
     * @param file String
     * @param debug boolean
     */
    public Program(String file, boolean debug) {
        container = Container.getContainer();
        fileName = file;
        baseFileName = file.substring(0, file.lastIndexOf('.'));
        programParser = new ProgramParser();
        this.debug = debug;
    }

    /**
     * prints the message if debug is active
     *
     * @param message String
     * @param color String
     */
    public void debug(String message, String color) {
        if (debug) {
            Tools.println(color == null ? "" : color, message);
        }
    }

    /**
     * processes the file passed
     *
     * @param fileName String
     * @throws Exception
     */
    protected void processFile(String fileName) throws Exception {

        int lineNumber = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();
                debug(line, "blue");

                if (!line.isEmpty()) {
                    Item item = programParser.itemizeLine(line, address, lineNumber);

                    if (null != item) {
                        if ((item instanceof Directive) && ((Directive) item).isORG()) {
                            if (orgDefined) {
                                throw new Exception(".org was defined already, you can't define more times");
                            }
                            org = address = Integer.parseInt(((Directive) item).getValue());
                            orgDefined = true;
                            debug($.TAB + $.TAB + $.TAB + "~~~~==** org address " + address + " **==~~~~", $.YELLOW);
                        }
                        address += item.getSize();
                        program.add(item);

                        if (item instanceof Constant) {
                            container.constants.add((Constant)item);
                        }else if (item instanceof Label) {
                            container.constants.add((Label)item);
                        }else if (item instanceof Include) {
                            processFile(Samples.getFile(((Include)item).getFile()));
                        }
                    }
                }

                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     *
     * @throws Exception
     */
    public void assemble() throws Exception {

        address = 0;

        // pass 1
        processFile(fileName);

        // pass 2  -  ??
        dumpPendings();

        // pass 3  -  solve labels
        debug($.CR + $.CR + "~~~~~~~~~~ propagating labels ~~~~~~~~~" + $.CR, $.RED);
        for (Item item : program) {
            if (item instanceof Label) propagateLabel((Label)item);
        }

        dumpPendings();

        debug($.CR + $.CR, null);

    }

    /**
     * propagates the value of the label passed
     *
     * @param label Label
     */
    protected void propagateLabel(Label label) {
        Tools.println_if(debug, "", "~^~ propagating label " + label.getLabel() + " " + label.getAddress() + " ~^~");
        for (Item item: program) {
            item.solvePending(label.getLabel(), label.getAddress());
        }
    }

    protected void dumpLabels() {
        if (!debug) return;
        Tools.println("red", $.CR + $.CR + "~~~~~~~~~~ labels ~~~~~~~~~~");
        for (Item item : program) {
            if (item instanceof Label) Tools.println("", item.toString());
        }
    }

    protected void dumpPendings() {
        if (!debug) return;
        Tools.println("red", $.CR + $.CR + "~~~~~~~~~~ pendings ~~~~~~~~~~");
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
        Tools.println($.RED, $.CR+ $.TAB+"~~~~~~  dumping assembled program ~~~~~~"+ $.CR);
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

        Tools.println_if(debug, "red", $.CR + $.CR + "~~~~~~~~~~~~ dump ~~~~~~~~~~~");

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

            Tools.println("red", $.CR + $.CR + $.TAB + "O~~=> generated " + outputFile + " with " + maxFileSize + " bytes. <=~~O");

        } catch (IOException e) {

            System.out.println ("Some error happened saving binary file." + $.CR + e.getMessage());
            System.exit(-1);
        }

    }

}
