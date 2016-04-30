package assembler.items;

import assembler.Tools;
import assembler.parser.ExpressionParser;
import assembler.parser.exceptions.UnrecognizedLiteralException;
import di.Container;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joseluislaso on 06/09/15.
 *
 *
 * TAKE IN ACCOUNT: http://sgate.emt.bme.hu/patai/publications/z80guide/part1.html#variables
 */
public class Token extends Item {

    private String instruction, op1, op2;
    private boolean isOffset = false;  // indicates that there is one operand in offset terms
    protected Container container;

    public Token(String instruction, String op1, String op2, int address, String src) throws Exception {
        super(src, address);
        this.instruction = instruction;
        this.op1 = op1;
        this.op2 = op2;
        container = Container.getContainer();

        opCode = process();
    }

    int[] process() throws Exception {

        String operation = instruction.toLowerCase().trim();
        String operand1 = op1.trim(), _operand1 = op1.toLowerCase();
        String operand2 = op2.trim(), _operand2 = op2.toLowerCase();

        switch (operation){

            case "nop":     return new int[] {0x00};
            case "halt":    return new int[] {0x76};

            case "di":      return new int[] {0xF3};
            case "ei":      return new int[] {0xFB};

            case "djnz":
                isOffset = true;
                return new int[] {0x10, getOffset(operand1, 1)};

            case "rrca":    return new int[] {0x0F};
            case "rra":     return new int[] {0x1F};
            case "rlca":    return new int[] {0x07};
            case "rla":     return new int[] {0x17};

            case "daa":     return new int[] {0x27};

            case "cpl":     return new int[] {0x2f};
            case "ccf":     return new int[] {0x3f};
            case "scf":     return new int[] {0x37};

            case "neg":     return new int[] {0xED, 0x44};
            case "retn":    return new int[] {0xED, 0x45};

            case "rst":
                switch (_operand1){
                    case "00h": return new int[] {0xC7};
                    case "08h": return new int[] {0xCF};
                    case "10h": return new int[] {0xD7};
                    case "18h": return new int[] {0xDF};
                    case "20h": return new int[] {0xE7};
                    case "28h": return new int[] {0xEF};
                    case "30h": return new int[] {0xF7};
                    case "38h": return new int[] {0xFF};
                }
                break;

            case "im":
                switch (_operand1){
                    case "0": return new int[] {0xED, 0x46};
                    case "1": return new int[] {0xED, 0x56};
                    case "2": return new int[] {0xD7, 0x5E};
                }
                break;

            case "push":
                switch (_operand1) {
                    case "bc":
                        return new int[]{0xC5};
                    case "de":
                        return new int[]{0xD5};
                    case "hl":
                        return new int[]{0xE5};
                    case "af":
                        return new int[]{0xF5};
                    case "ix":
                        return new int[]{0xDD, 0xE5};
                    case "iy":
                        return new int[]{0xFD, 0xE5};
                }
                break;

            case "exx" :
                return new int[] {0xD9};

            case "ex":
                switch (_operand2) {
                    case "hl":
                        switch (operand1) {
                            case "(sp)":
                                return new int[]{0xE3};
                            case "de":
                                return new int[]{0xEB};
                        }
                        break;

                    case "af'":
                        return new int[]{0x08};
                }
                break;

            case "out":
                if (_operand2.equals("a")){
                    Pattern p = Pattern.compile("\\(\\s*(?<r>[^\\)]+)\\s*\\)");
                    Matcher m = p.matcher(operand1);
                    if (m.find()) {
                        operand1 = m.group("r");
                    }
                    int n = getLiteralLo(operand1, 1);
                    return new int[] {0xD3, n};
                }
                break;

            case "pop":
                switch (_operand1) {
                    case "bc":
                        return new int[]{0xC1};
                    case "de":
                        return new int[]{0xD1};
                    case "hl":
                        return new int[]{0xE1};
                    case "af":
                        return new int[]{0xF1};
                    case "ix":
                        return new int[]{0xDD, 0xE1};
                    case "iy":
                        return new int[]{0xFD, 0xE1};
                }
                break;


            case "dec":  // 20 instructions with dec nemonic
                switch (_operand1) {
                    case "a": return new int[] {0x3d};
                    case "b": return new int[] {0x05};
                    case "c": return new int[] {0x0d};
                    case "d": return new int[] {0x15};
                    case "e": return new int[] {0x1d};
                    case "h": return new int[] {0x25};
                    case "l": return new int[] {0x2d};
                    case "(hl)": return new int[] {0x35};
                    case "bc": return new int[] {0x0b};
                    case "de": return new int[] {0x1b};
                    case "hl": return new int[] {0x2b};
                    case "sp": return new int[] {0x3b};
                    case "ixh": return new int[] {0xdd, 0x25};
                    case "iyh": return new int[] {0xfd, 0x25};
                    case "ix": return new int[] {0xdd, 0x2b};
                    case "iy": return new int[] {0xdd, 0x2b};
                    case "ixl": return new int[] {0xdd, 0x2d};
                    case "iyl": return new int[] {0xfd, 0x2d};

                    default:
                        Pattern p = Pattern.compile("\\(\\s*(?<r>ix|iy)\\s*\\+\\s*(?<n>\\d+)\\s*\\)");
                        Matcher m =p.matcher(_operand1);
                        if (m.find()){
                            int first = (m.group("r").equals("ix")) ? 0xdd : 0xfd;
                            return new int[] {first, 0x35, getDisplacement(m.group("m"), 2)};
                        }
                }
                break;

            case "inc":  // 20 instructions with inc nemonic
                switch (_operand1) {
                    case "a": return new int[] {0x3c};
                    case "b": return new int[] {0x04};
                    case "c": return new int[] {0x0c};
                    case "d": return new int[] {0x14};
                    case "e": return new int[] {0x1c};
                    case "h": return new int[] {0x24};
                    case "l": return new int[] {0x2c};
                    case "(hl)": return new int[] {0x34};
                    case "bc": return new int[] {0x03};
                    case "de": return new int[] {0x13};
                    case "hl": return new int[] {0x23};
                    case "sp": return new int[] {0x33};
                    case "ixh": return new int[] {0xdd, 0x24};
                    case "iyh": return new int[] {0xfd, 0x25};
                    case "ix": return new int[] {0xdd, 0x23};
                    case "iy": return new int[] {0xdd, 0x23};
                    case "ixl": return new int[] {0xdd, 0x2c};
                    case "iyl": return new int[] {0xfd, 0x2c};

                    default:
                        Pattern p = Pattern.compile("\\(\\s*(?<r>ix|iy)\\s*\\+\\s*(?<n>\\d+)\\s*\\)");
                        Matcher m =p.matcher(_operand1);
                        if (m.find()){
                            int first = (m.group("r").equals("ix")) ? 0xdd : 0xfd;
                            return new int[] {first, 0x34, getDisplacement(m.group("m"), 2)};
                        }
                }
                break;

            case "sub":
                if (operand2.isEmpty()){
                    try {
                        return new int[]{0x90 | code8bitsRegister(_operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xD6, Tools.figureOut(operand1)};
                    }
                }
                break;

            case "sbc":   // sbc a,?
                if (operand1.equals("a")){
                    try {
                        return new int[]{0x98 | code8bitsRegister(_operand2)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xDE, Tools.figureOut(operand2)};
                    }
                }
                break;

            case "add":   // add a,?
                switch (_operand1) {
                    case "a":
                        try {
                            return new int[]{0x80 | code8bitsRegister(_operand2)};
                        } catch (Unrecognized8bitsRegister e) {
                            return new int[]{0xC6, Tools.figureOut(operand2)};
                        }
                    case "hl":
                        return new int[]{0x09 | (ssRegister(_operand1) << 4)};
                }
                break;

            case "adc":   // adc a,?
                if (operand1.equals("a")){
                    try {
                        return new int[]{0x88 | code8bitsRegister(_operand2)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xCE, Tools.figureOut(operand2)};
                    }
                }
                break;

            case "and":
                if (operand2.isEmpty()){
                    try {
                        return new int[]{0xA0 | code8bitsRegister(_operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xE6, Tools.figureOut(operand1)};
                    }
                }
                break;

            case "cp":
                if (operand2.isEmpty()){
                    try {
                        return new int[]{0xB8 | code8bitsRegister(_operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xFE, Tools.figureOut(operand1)};
                    }
                }
                break;

            case "or":
                if (operand2.isEmpty()){
                    try {
                        return new int[]{0xB0 | code8bitsRegister(_operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xF6, Tools.figureOut(operand1)};
                    }
                }
                break;

            case "xor":
                if (operand2.isEmpty()){
                    try {
                        return new int[]{0xA8 | code8bitsRegister(_operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xEE, Tools.figureOut(operand1)};
                    }
                }
                break;

            case "ld":
                switch (_operand1) {

                    case "a":
                        switch (_operand2) {

                            case "(bc)": return new int[]{0x0A};
                            case "(de)": return new int[]{0x1A};
                            default:
                                try {
                                    return new int[]{0x78 | code8bitsRegister(_operand2)};
                                } catch (Unrecognized8bitsRegister e) {

                                    // detect between ld a,x and ld a,(xx)
                                    return new int[]{0x3E, 0xff & getLiteralLo(operand2, 2)};
                                }
                        }

                    case "b":
                        try {
                            return new int[] {0x40 | code8bitsRegister(_operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x06, getLiteralLo(operand2, 1)};
                        }
                    case "c":
                        try{
                            return new int[] {0x48 | code8bitsRegister(_operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x0E, getLiteralLo(operand2, 1)};
                        }
                    case "d":
                        try{
                            return new int[] {0x50 | code8bitsRegister(_operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x16, getLiteralLo(operand2, 1)};
                        }
                    case "e":
                        try{
                            return new int[] {0x58 | code8bitsRegister(_operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x1E, getLiteralLo(operand2, 1)};
                        }
                    case "h":
                        try{
                            return new int[] {0x60 | code8bitsRegister(_operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x26, getLiteralLo(operand2, 1)};
                        }
                    case "l":
                        try{
                            return new int[] {0x68 | code8bitsRegister(_operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x2E, getLiteralLo(operand2, 1)};
                        }
                    case "bc": return parse16bitsArgument(new int[] {0x01}, op2);
                    case "de": return parse16bitsArgument(new int[] {0x11}, op2);
                    case "hl": return parse16bitsArgument(new int[] {0x21}, op2);
                    case "sp":
                        switch (operand2) {
                            case "hl":
                                return new int[]{0xF9};
                            default:
                                return new int[]{0x31, getLiteralLo(op2, 1), getLiteralHi(op2, 2)};
                        }

                    case "(hl)":
                        if (!_operand2.equals("(hl)")){  // because the theoretical code for "ld (hl),(hl)" it's the same for "halt"
                            try {
                                return new int[] {0x70 | code8bitsRegister(_operand2)};
                            }catch (Unrecognized8bitsRegister e){
                                return new int[]{0x36, getLiteralLo(operand2, 1)};
                            }
                        }
                        break;
                    case "(bc)":
                        if (_operand2.equals("a")){
                            return new int[] {0x02};
                        }
                        break;
                    case "(de)":
                        if (_operand2.equals("a")){
                            return new int[] {0x12};
                        }
                        break;
                    default:
                        Pattern p = Pattern.compile("\\(\\s*(?<n>\\w+)\\s*\\)");
                        Matcher m = p.matcher(_operand1);
                        if(m.find()) {
                            int literalHi = getLiteralHi(m.group("n"), 2);
                            int literalLo = getLiteralLo(m.group("n"), 1);
                            switch (_operand2) {
                                case "hl":
                                    return new int[]{0x22, literalLo, literalHi};
                                case "a":
                                    return new int[]{0x23, literalLo, literalHi};
                            }
                        }

                }
                break;

            case "jr":
                isOffset = true;
                switch (_operand1){
                    case "nz":  return new int[] {0x20, getOffset(operand2, 1)};
                    case "z":   return new int[] {0x28, getOffset(operand2, 1)};
                    case "nc":  return new int[] {0x30, getOffset(operand2, 1)};
                    case "c":   return new int[] {0x38, getOffset(operand2, 1)};
                    default:
                        if (operand2.isEmpty()){
                            return new int[] {0x18, getOffset(operand1, 1)};
                        }
                }
                break;

            case "ret":
                if (!operand2.isEmpty()) break;
                switch (_operand1){
                    case "": return new int[] {0xC9};
                    case "nz":  return new int[] {0xC0};
                    case "z" :  return new int[] {0xC8};
                    case "nc":  return new int[] {0xD0};
                    case "c" :  return new int[] {0xD8};
                    case "po":  return new int[] {0xE0};
                    case "pe":  return new int[] {0xE8};
                    case "p":   return new int[] {0xF0};
                    case "m":   return new int[] {0xF8};
                }
                break;

            case "jp":
                switch (_operand1){
                    case "nz":  return getJumpCode(0xC2, operand2);
                    case "z":   return getJumpCode(0xCA, operand2);
                    case "nc":  return getJumpCode(0xD2, operand2);
                    case "c":   return getJumpCode(0xDA, operand2);
                    case "po":  return getJumpCode(0xE2, operand2);
                    case "pe":  return getJumpCode(0xEA, operand2);
                    case "p":   return getJumpCode(0xF2, operand2);
                    case "m":   return getJumpCode(0xFA, operand2);
                    case "(hl)": return new int[] {0xE9};
                    case "(ix)": return new int[] {0xDD, 0xE9};
                    case "(iy)": return new int[] {0xFD, 0xE9};
                    default:
                        if (operand2.isEmpty()){
                            return getJumpCode(0xC3, operand1);
                        }
                }
                break;

            case "call":
                switch (_operand1){
                    case "nz":  return getJumpCode(0xC4, operand2);
                    case "z":   return getJumpCode(0xCC, operand2);
                    case "nc":  return getJumpCode(0xD4, operand2);
                    case "c":   return getJumpCode(0xDC, operand2);
                    case "po":  return getJumpCode(0xE4, operand2);
                    case "pe":  return getJumpCode(0xEC, operand2);
                    case "p":   return getJumpCode(0xF4, operand2);
                    case "m":   return getJumpCode(0xFC, operand2);
                    case "(hl)": return new int[] {0xE9};
                    case "(ix)": return new int[] {0xDD, 0xE9};
                    case "(iy)": return new int[] {0xFD, 0xE9};
                    default:
                        if (operand2.isEmpty()){
                            return getJumpCode(0xCD, operand1);
                        }
                }
                break;

        }
        throw new Exception("not recognized "+operation+" "+operand1+", "+operand2);

    }

    /**
     * @param opCode int
     * @param operand String
     * @return int[]
     */
    private int[] getJumpCode(int opCode, String operand) {
        int addressHi = getLiteralHi(operand, 2);
        int addressLo = getLiteralLo(operand, 1);

        return new int[] {opCode, addressLo, addressHi};
    }

    /**
     * code a 8'bits register into its code for ALU
     * @param reg
     * @return
     * @throws Exception
     */
    protected int code8bitsRegister(String reg) throws Exception {
        switch (reg.toLowerCase()){
            case "a": return 0x07;
            case "b": return 0x00;
            case "c": return 0x01;
            case "d": return 0x02;
            case "e": return 0x03;
            case "h": return 0x04;
            case "l": return 0x05;
            case "(hl)": return 0x06;
        }
        throw new Unrecognized8bitsRegister(reg);
    }

    protected int ssRegister(String reg) throws Exception {
        switch(reg) {
            case "bc": return 0;
            case "de": return 1;
            case "hl": return 2;
            case "sp": return 3;
        }
        throw new Exception("Unknow register '"+reg+"'");
    }

    /**
     * get a literal address (16-bit) (can be a label also)
     * @param literal String
     * @param pos int
     * @return int
     */
    protected int getLiteralHi(String literal, int pos)
    {
        try {
            return Tools.figureOut(literal) >>> 8;
        }catch (Exception e){
            addPending(literal, pos, Pending.BYTE_HI);
            return 0;
        }
    }
    /**
     * get a literal address (16-bit) (can be a label also)
     * @param literal String
     * @param pos int
     * @return int
     */
    protected int getLiteralLo(String literal, int pos)
    {
        try {
            return Tools.figureOut(literal) & 0xff;
        }catch (Exception e){
            addPending(literal, pos, Pending.BYTE_LO);
            return 0;
        }
    }

    /**
     * get a literal displacement (8-bits) (can be a label too)
     *
     * @param literal String
     * @param pos int
     * @return int
     */
    protected int getDisplacement(String literal, int pos)
    {
        try {
            return Tools.figureOut(literal);
        }catch (Exception e){
            addPending(literal, pos, Pending.OFFSET_8_BITS_C2);
            return 0;
        }
    }

    /**
     * return the address computed for a short jump
     *
     * @param offset String
     * @param pos int
     * @return int
     */
    protected int getOffset(String offset, int pos)
    {
        try{
            return Integer.parseInt(offset);
        }catch (Exception e) {
            addPending(offset, pos, Pending.OFFSET_8_BITS_C2);
            return 0;
        }
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
            return (256 - offset);
        }else{
            return offset;
        }
    }

//    /**
//     *
//     * @param pendingCause
//     * @param toAddress
//     * @throws Exception
//     */
//    public void resolvePending(String pendingCause, int toAddress) throws Exception {
//        boolean solved = false;
//        for(int o=0; o<opCode.length; o++){
//            if (opCode[o] == pendingMarkAbsoluteLo){
//                opCode[o] = toAddress & 0xff;
//                solved = true;
//            }
//            if (opCode[o] == pendingMarkAbsoluteHi){
//                opCode[o] = toAddress >>> 8;
//                solved = true;
//            }
//            if (opCode[o] == pendingMarkOffset) {
//                opCode[o] = 0xff & calcOffset(address+opCode.length, toAddress);
//                solved = true;
//            }
//        }
//        if (!solved) {
//            throw new Exception("trying to resolve when is no pending, pendingCause='" + pendingCause + "'");
//        }
//    }

//    public boolean isPending() {
//        return pending;
//    }

//    /**
//     * @param pending
//     */
//    public void setPending(boolean pending) {
//        this.pending = pending;
//        if (!pending) {
//            pendingCause = "";
//        }
//    }
//
//    public String getPendingCause() {
//        return pendingCause;
//    }

    @Override
    public String toString() {
        int address = getAddress();
        return src + " => Token{ " +
                prettyAddress() +
                instruction + " " + op1 + (op2.isEmpty() ? "" : ","+ op2)  +
                (isOffset ? " ~"+Tools.addressToHex(realAddress(opCode[opCode.length-1]))+"~ " : "") +
                (hasPending() ? "    pending " : "") +
                " [" + getOpCodeAsHexString(':') + "] " +
                " }";
    }

    protected int realAddress(int offset) {
        if ((byte)offset<0) {
            return (address - 256 + offset);
        }else{
            return address + offset;
        }
    }

    /**
     * exception to capture errors decoding 8-bit register, and try to interpret as a literal
     */
    private class Unrecognized8bitsRegister extends Exception{
        public Unrecognized8bitsRegister(String register) {
            super("unrecognized "+register+" trying to figure out a 8 bit register");
        }
    }

    /**
     * parse a literal argument of 16 bits
     *
     * @param prefix int[]  opcode prefix to add at the beginning of the result
     * @param literal String
     * @return int[]
     * @throws UnrecognizedLiteralException
     */
    protected int[] parse16bitsArgument(int[] prefix, String literal) throws UnrecognizedLiteralException {
        int pos = prefix.length;
        int[] result = new int[pos+2];
        for (int i = 0; i < pos; i++) {
            result[i] = prefix[i];
        }
        literal = container.expressionParser.preParse(literal);
        if(container.expressionParser.arePendingLiterals()) {
            addPending(literal, pos + 1, Pending.BYTE_HI);
            addPending(literal, pos, Pending.BYTE_LO);
        }else{
            double d = Tools.eval(literal);
            result[pos] = (int)d & 0xff;
            result[pos+1] = (int)d >> 8;
        }
        return result;
    }

}
