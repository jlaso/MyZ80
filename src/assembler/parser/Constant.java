package assembler.parser;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Constant extends Item {

    protected String name;
    protected String value;

    public Constant(String name) {
        this(name, "");
    }

    public Constant(String name, String value) {
        this.name = name;
        this.value = value;
        setSize(0);
    }
}
