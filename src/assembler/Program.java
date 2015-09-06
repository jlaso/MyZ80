package assembler;

import assembler.parser.Constant;
import assembler.parser.Item;
import assembler.parser.Label;
import assembler.parser.Token;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Program {

    protected static int TOKEN = 1;
    protected static int CONSTANT = 2;

    protected Token[] tokens;
    protected Constant[] constants;
    protected Label[] labels;
    protected String fileName;
    protected String baseFileName;
    protected ArrayList<Item> program = new ArrayList<Item>();

    public Program(String file) {
        fileName = file;
        baseFileName = file.substring(0, file.lastIndexOf('.'));
    }

    protected Item tokenize(String line){

        String[] temp = new String[] {"","",""};
        int type = TOKEN;
        int current = 0;
        for (int i=0; i<line.length(); i++){
            char c = line.charAt(i);

            if (';'==c) break;  // comment ends tokenize process

            if ((0==current) && (':'==c)) { // label ends with :
                return new Label(temp[current]);
            }

            if ((' '==c) || (','==c)){
                if (temp[current]=="") {
                }else{
                    if ((1==current) && (temp[current].toLowerCase()=="equ")){
                        type = CONSTANT;
                    }
                    current++;
                }
            }else{
                temp[current] += c;
            }

        }

        if (CONSTANT==type){
            return new Constant(temp[0]);
        }

        return new Token(temp[0], temp[1], temp[2]);
    }

    public void assemble() throws FileNotFoundException {

        int address = 0;

        // pass 1
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {

                Item item = tokenize(line);

                item.setAddress(address);
                address += item.getSize();

                program.add(item);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // pass 2


        // pass 3

    }


    public void saveBin() {

        String content = "";
        for (int i=0; i<program.size(); i++){
            Item item = program.get(i);

            if (item instanceof Token) {
                int[] opCodes = ((Token)item).getOpCode();
                for (int o=0; o<opCodes.length; o++) {
                    content += (char)opCodes[o];
                }
            }
        }

        // save bin file
        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter( new FileWriter( baseFileName+".bin" ));
            writer.write(content);

        }
        catch ( IOException e)
        {
        }
        finally
        {
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

}
