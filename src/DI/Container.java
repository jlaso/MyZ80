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
    public ArrayList<Valuable> constants = new ArrayList<Valuable>();

    public Container() {
        expressionParser = new ExpressionParser(this);
    }

    public static Container getContainer() {
        return container == null ? (container = new Container()) : container;
    }
}
