package assembler.parser;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Directive extends Item {

    protected String name;
    protected String value;

    public Directive(String name) {
        this(name, "");
    }

    public Directive(String name, String value) {
        this.name = name;
        this.value = value;
        setSize(0);
    }

    @Override
    public String toString() {
        return "Directive{ name='" + name + "', value='" + value + "'}";
    }
}
