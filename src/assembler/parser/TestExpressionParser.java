package assembler.parser;

import assembler.Program;
import assembler.items.Constant;
import machines.simpleZ80.Memory;
import samples.Samples;

import java.util.ArrayList;

/**
 * Created by joseluislaso on 14/09/15.
 */
public class TestExpressionParser {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        ArrayList<Constant> constants = new ArrayList<Constant>();

        constants.add(new Constant("pi", "314"));

        String exp1 = "$0a+label1+pi/10-%0101";

        ExpressionParser parser = new ExpressionParser(constants);

        System.out.println(exp1 + " ~~~> " + parser.preParse(exp1));

    }
}
