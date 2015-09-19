package assembler.items;

import assembler.Tools;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Label extends Item implements Valuable{

    protected String label;

    public Label(String label, String src){
        super(src);
        this.label = label;
        this.opCode = null;
    }

    public Label(String label, int address, String src){
        this(label, src);
        this.address = address;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String getValue() {
        return ""+address;
    }

    @Override
    public boolean match(String name) {
        return label.equals(name);
    }

    @Override
    public String toString() {
        return src + " => Label{ " +
                prettyAddress() +
                "'" + label + "'}";
    }
}
