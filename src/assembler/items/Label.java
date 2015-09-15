package assembler.items;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Label extends Item {

    protected String label;

    public Label(String label, String src){
        super(src);
        this.label = label;
        this.opCode = null;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return src + " => Label{" +
                "'" + label + "', address=" + address +
                '}';
    }
}
