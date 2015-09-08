package assembler;

import assembler.parser.*;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Program {

    final protected static int TOKEN = 1;
    final protected static int CONSTANT = 2;
    final protected static int DIRECTIVE = 3;

    protected Token[] tokens;
    protected Constant[] constants;
    protected ArrayList<Label> labels = new ArrayList<Label>();
    protected String fileName;
    protected String baseFileName;
    protected ArrayList<Item> program = new ArrayList<Item>();

    public Program(String file) {
        fileName = file;
        baseFileName = file.substring(0, file.lastIndexOf('.'));
    }

    protected Item tokenize(String line) throws Exception {

        String[] temp = new String[] {"","",""};
        int type = TOKEN;
        int current = 0;
        for (int i=0; i<line.length(); i++) {
            char c = line.charAt(i);

            if (';' == c) break;  // comment ends tokenize process

            if ((0 == current) && (':' == c)) { // label ends with :
                Label label = new Label(temp[0]);
                labels.add(label);
                return label;
            }

            if ((' ' == c) || (',' == c)) {
                if ("" != temp[current]) {
                    switch (current) {
                        case 0:
                            if (temp[current].charAt(0) == '.') {
                                type = DIRECTIVE;
                            }
                            break;
                        case 1:
                            if (temp[current].toLowerCase() == "equ") {
                                type = CONSTANT;
                            }
                            break;
                    }
                    current++;
                }
            }else {
                temp[current] += c;
            }
        }

        //System.out.println("--> temp[0]='"+temp[0]+"', temp[1]='"+temp[1]+"', temp[2]='"+temp[2]+"'");

        if (""!=temp[0]) {
            switch (type) {
                case CONSTANT:
                    return new Constant(temp[0]);

                case DIRECTIVE:
                    return new Directive(temp[0], temp[1]);

                default:
                    return new Token(temp[0], temp[1], temp[2]);
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

                if (""!=line) {
                    Item item = tokenize(line);

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

        // pass 2


        // pass 3
        for (int i=0; i<program.size(); i++){
            Item item = program.get(i);

            if (item instanceof Token) {
                Token token = (Token) item;

                if (token.isPending()){
                    token.resolvePending(token.getPendingCause(), getValueOfLabel(token.getPendingCause()));
                    token.setPending(false);
                }

            }
        }

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

        String hex = "";
        for (int i=0; i<program.size(); i++){
            Item item = program.get(i);

            if (item instanceof Token) {
                hex += ((Token) item).getOpCodeAsHexString(' ');
            }
        }
        System.out.println(hex);

        // save bin file
        DataOutputStream writer = null;
        try
        {
            writer = new DataOutputStream(new FileOutputStream(baseFileName+".bin" ));
            for (int i=0; i<program.size(); i++){
                Item item = program.get(i);

                System.out.println(item.toString());

                if (item instanceof Token) {
                    int[] opCodes = ((Token)item).getOpCode();
                    for (int b=0; b<opCodes.length; b++) {
                        writer.writeByte((byte)(opCodes[b] & 0xff));
                    }
                }
            }
        }
        catch ( IOException e)
        {
        }

        try
        {
            if ( writer != null)
                writer.close( );
        }
        catch ( IOException e)
        {
        }

    }

}
