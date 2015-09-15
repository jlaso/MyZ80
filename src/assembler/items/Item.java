package assembler.items;

import assembler.Tools;

import java.util.ArrayList;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Item {

    protected int address;
    protected int[] opCode;
    protected ArrayList<Pending> pendingList = new ArrayList<Pending>();
    protected String src;

    public Item(String src) {
        this.src = src;
    }

    public int getAddress() {
        return address;
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
        Pending pending = new Pending(cause, position, type);

        pendingList.add(pending);
    }

    /**
     * Try to solve pending cause, if any
     *
     * @param cause
     * @param value
     */
    public void solvePending(String cause, int value) {

        if ( !hasPending() ) return;

        for (int i = 0; i < pendingList.size(); i++) {
            Pending pending  = pendingList.get(i);

            if (pending.match(cause)) {
                int pos = pending.getPosition();
                Tools.println("cyan", "pos="+pos+",cause="+cause+"  ||  "+toString());
                switch (pending.getType()) {
                    case Pending.OFFSET_8_BITS_C2:
                        opCode[pos] = (byte) value;
                        break;

                    case Pending.ADDRESS:
                        opCode[pos] = value & 0xff;
                        opCode[pos+1] = value >>> 8;
                        break;

                    case Pending.BYTE_HI:
                        opCode[pos+1] = value >>> 8;
                        break;

                    case Pending.BYTE_LO:
                        opCode[pos+1] = value & 0xff;
                        break;

                }

                pendingList.remove(i);
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
     * @param separator
     * @return
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
