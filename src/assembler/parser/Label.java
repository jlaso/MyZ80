package assembler.parser;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Label extends Item {

    protected String label;

    public Label(String label){
        this.label = label;
        setSize(0);
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "Label{" +
                "'" + label + "', address=" + address +
                '}';
    }
}
