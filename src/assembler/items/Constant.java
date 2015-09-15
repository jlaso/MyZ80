package assembler.items;

import assembler.Tools;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Constant extends Item {

    protected String name;
    protected int value;

    public Constant(String name, String value, String src) {
        super(src);
        this.name = name;
        this.value = Tools.figureOut(value);
        this.opCode = null;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return src + " => Constant{" + name + " = " + value + "}";
    }
}
