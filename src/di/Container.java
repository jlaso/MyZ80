package di;

import assembler.items.Valuable;
import assembler.parser.ExpressionParser;

import java.util.ArrayList;

/**
 * Created by joseluislaso on 18/09/15.
 */
final public class Container {

    protected static Container container = null;

    public ExpressionParser expressionParser;
    public ArrayList<Valuable> constants = new ArrayList<>();

    public Container() {
        expressionParser = new ExpressionParser(this);
    }

    /**
     * singleton
     *
     * @return Container
     */
    public static Container getContainer() {

        return container == null ? (container = new Container()) : container;

    }

    /**
     * returns the value of the constant passed or throws an exception if doesn't exist
     *
     * @param constantName String
     * @return String
     * @throws Exception
     */
    protected String getValueOf(String constantName) throws Exception {
        for (Valuable constant : constants) {
            if (constant.match(constantName)) {
                return constant.getValue();
            }
        }

        throw new Exception("Constant " + constantName + " not declared yet!");
    }
}
