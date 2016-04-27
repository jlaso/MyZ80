package assembler.tests;

import assembler.parser.ExpressionParser;
import di.Container;
import assembler.items.Constant;
import assembler.items.Valuable;

import java.util.ArrayList;

/**
 * Created by joseluislaso on 14/09/15.
 */
public class TestExpressionParser {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        Container container = Container.getContainer();

        container.constants.add(new Constant("pi", "314", "#define pi 314"));

        String exp1 = "$0a+label1+pi/10-%0101";

        ExpressionParser parser = new ExpressionParser();

        System.out.println(exp1 + " ~~~> " + parser.preParse(exp1));

        if (parser.arePendingLiterals()) dumpStringArrayList(parser.getPendingList());

        String exp2 = "$0a,label1,pi,10,%0101";

        System.out.println(exp2 + " ~~~> " + parser.preParse(exp2));

        if (parser.arePendingLiterals()) dumpStringArrayList(parser.getPendingList());

        System.out.println("Substituting --> " + parser.solvePending(exp2, "label1", "$0aaa"));

        String exp3 = "#$0a+label1+pi/10-%0101";

        System.out.println(exp3 + " ~~~> " + parser.preParse(exp3));

        if (parser.arePendingLiterals()) dumpStringArrayList(parser.getPendingList());
    }

    public static void dumpStringArrayList(ArrayList<String> list) {
        System.out.println("There are these literals pending of solve ...");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }
}
