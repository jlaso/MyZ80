package di;

import assembler.items.Valuable;
import assembler.parser.ExpressionParser;

import java.util.ArrayList;

/**
 * Created by joseluislaso on 18/09/15.
 */
public class DI {

    protected static DI instance = null;
    protected ExpressionParser parser;
    protected ArrayList<Valuable> constants = new ArrayList<Valuable>();

    public DI() {

        parser = new ExpressionParser(constants);

    }

    public ExpressionParser getParser() {
        return parser;
    }

    public static DI getInstance() {
        if (instance == null) {
            instance = new DI();
        }

        return instance;
    }
}
