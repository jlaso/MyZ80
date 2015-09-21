package assembler.items;

import assembler.Program;
import assembler.Tools;
import assembler.parser.ExpressionParser;
import di.Container;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Item {

    protected int address;
    protected int[] opCode;
    protected ArrayList<Pending> pendingList = new ArrayList<Pending>();
    protected String src;

    public Item(String src) {
        this(src,0);
    }

    public Item(String src, int address) {
        this.src = src;
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public String getSrc() {
        return src;
    }

    public String prettyAddress() {
        return ""+address+"["+Tools.bytesToHex(new int[]{address>>>8, address&0xff}) + "]: ";
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getSize() {
        if (opCode != null) {
            return opCode.length;
        }else{
            return 0;
        }
    }

    public int[] getOpCode() {
        return opCode;
    }

    /**
     * add pending to the item
     *
     * @param cause
     * @param position
     * @param type
     */
    public void addPending(String cause, int position, int type) {
        cause = cause.trim();
        if (cause.isEmpty()) return;

        Pending pending = new Pending(cause, position, type);
        pendingList.add(pending);
    }

    /**
     * calculate the offset in c2 between two addresses
     *
     * @param from int
     * @param to int
     * @return int
     */
    protected int calcOffset(int from, int to)
    {
        int offset = Math.abs(from - to);
        if (from>to) {
            return 256 - offset;
        }else{
            return offset;
        }
    }

    /**
     * Try to solve pending cause, if any
     *
     * @param cause String
     * @param value int
     */
    public void solvePending(String cause, int value) {

        if ( !hasPending() ) return;

        Tools.println("cyan", "finding pending '"+cause+"' in "+toString());
        Iterator<Pending> iterator = pendingList.iterator();

        while(iterator.hasNext()) {

            Pending pending = iterator.next();

            Tools.println("", "\t '"+pending.getCause()+"' ... ["+pending.typeAsString()+"]");

            if (pending.match(cause)) {

                Tools.println("yellow", "pending '"+cause+"' found in  ~~~>  "+toString());

                String tmp = pending.replaceLabel(cause, ""+value);

                try {
                    tmp = Container.getContainer().expressionParser.preParse(tmp);
                    double d = Tools.eval(tmp);
                    //value = (int)d;
                    int pos = pending.getPosition();
                    Tools.println("purple", "pos=" + pos + ",cause=" + cause + "  ||  " + toString());
                    switch (pending.getType()) {
                        case Pending.OFFSET_8_BITS_C2:
                            opCode[pos] = calcOffset(address, value);
                            break;

                        case Pending.ADDRESS:
                            opCode[pos] = value & 0xff;
                            opCode[pos+1] = value >>> 8;
                            break;

                        case Pending.BYTE_HI:
                            opCode[pos] = value >>> 8;
                            break;

                        case Pending.BYTE_LO:
                            opCode[pos] = value & 0xff;
                            break;

                    }

                }catch(Exception e) {
                    // can't remove pending
                }

                iterator.remove();

            }
        }
    }

    public boolean hasPending() {
        return (pendingList.size() > 0);
    }

    public ArrayList<Pending> getPendingList() {
        return pendingList;
    }

    /**
     *
     * @param separator char
     * @return String
     */
    public String getOpCodeAsHexString(char separator)
    {
        String result = "";
        int size = getSize();
        for (int o=0; o<size; o++) {
            result += Tools.byteToHex(opCode[o]);
            if (o<size-1) result += separator;

        }

        return result;
    }
}
