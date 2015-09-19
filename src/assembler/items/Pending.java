package assembler.items;

/**
 * Created by joseluislaso on 11/09/15.
 */
public class Pending {

    /**
     * types
     **/
    final static int OFFSET_8_BITS_C2 = 1;
    final static int BYTE_LO = 2;
    final static int BYTE_HI = 3;
    final static int ADDRESS = 4;

    protected String cause;
    protected int position;
    protected int type;

    public Pending(String cause, int position, int type) {
        this.cause = " " + cause.trim() + " ";
        this.position = position;
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public int getType() {
        return type;
    }

    public String getCause() { return cause; }

    public void setCause(String cause) { this.cause = cause; }

    public boolean match(String cause) {
        return this.cause.contains(" " + cause + " ");
    }

    public String replaceLabel(String label, String value) {
        return cause = cause.replace(" "+label+" ", value);
    }

    protected String typeAsString() {
        switch (type) {
            case OFFSET_8_BITS_C2: return "OFFSET_8_BITS_C2";
            case BYTE_LO: return "BYTE_LO";
            case BYTE_HI: return "BYTE_HI";
            case ADDRESS: return "ADDRESS";
            default: return ""+type;
        }
    }

    @Override
    public String toString() {
        return "Pending{ " + cause + " pos=" + position + ", [" + typeAsString() + "] }";
    }
}
