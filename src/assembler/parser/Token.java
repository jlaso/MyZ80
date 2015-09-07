package assembler.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joseluislaso on 06/09/15.
 */
public class Token extends Item {

    protected int[] opCode;
    protected String instruction, op1, op2;
    protected boolean pending;
    protected String pendingCause;

    public Token(String instruction, String op1, String op2) throws Exception {
        this.instruction = instruction;
        this.op1 = op1;
        this.op2 = op2;

        opCode = process();
    }

    protected int[] process() throws Exception {

        String operation = instruction.toLowerCase();
        String operand1 = op1.toLowerCase();
        String operand2 = op2.toLowerCase();

        switch (operation ){

            case "nop":     return new int[] {0x00};
            case "halt":     return new int[] {0x76};
            case "di":     return new int[] {0xF3};
            case "ei":     return new int[] {0xFB};

            case "djnz":    return new int[] {0x10, getOffset(operand1)};

            case "rrca":    return new int[] {0x0F};

            case "rra":    return new int[] {0x1F};

            case "rlca": return new int[] {0x07};

            case "rla": return new int[] {0x17};

            case "daa": return new int[] {0x27};

            case "cpl": return new int[] {0x2f};

            case "ccf": return new int[] {0x3f};

            case "scf": return new int[] {0x37};

            case "rst":
                switch (operand1){
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

            case "push":
                switch (operand1) {
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


            case "dec":  // 20 instructions with dec nemonic
                switch (operand1) {
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
                        Matcher m =p.matcher(operand1);
                        if (m.find()){
                            int first = (m.group("r") == "ix") ? 0xdd : 0xfd;
                            return new int[] {first, 0x35, getLiteral(m.group("m"))};
                        }
                }
                break;

            case "inc":  // 20 instructions with inc nemonic
                switch (operand1) {
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
                        Matcher m =p.matcher(operand1);
                        if (m.find()){
                            int first = (m.group("r") == "ix") ? 0xdd : 0xfd;
                            return new int[] {first, 0x34, getLiteral(m.group("m"))};
                        }
                }
                break;

            case "sub":
                if (""==operand2){
                    try {
                        return new int[]{0x90 | code8bitsRegister(operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xD6, Integer.parseInt(operand1)};
                    }
                }
                break;

            case "sbc":   // sbc a,?
                if ("a"==operand1){
                    try {
                        return new int[]{0x98 | code8bitsRegister(operand2)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xDE, Integer.parseInt(operand2)};
                    }
                }
                break;

            case "add":   // add a,?
                if ("a"==operand1){
                    try {
                        return new int[]{0x80 | code8bitsRegister(operand2)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xC6, Integer.parseInt(operand2)};
                    }
                }
                break;

            case "adc":   // adc a,?
                if ("a"==operand1){
                    try {
                        return new int[]{0x88 | code8bitsRegister(operand2)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xCE, Integer.parseInt(operand2)};
                    }
                }
                break;

            case "and":
                if (""==operand2){
                    try {
                        return new int[]{0xA0 | code8bitsRegister(operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xE6, Integer.parseInt(operand1)};
                    }
                }
                break;

            case "cp":
                if (""==operand2){
                    try {
                        return new int[]{0xB8 | code8bitsRegister(operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xFE, Integer.parseInt(operand1)};
                    }
                }
                break;

            case "or":
                if (""==operand2){
                    try {
                        return new int[]{0xB0 | code8bitsRegister(operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xF6, Integer.parseInt(operand1)};
                    }
                }
                break;

            case "xor":
                if (""==operand2){
                    try {
                        return new int[]{0xA8 | code8bitsRegister(operand1)};
                    }catch (Unrecognized8bitsRegister e){
                        return new int[]{0xEE, Integer.parseInt(operand1)};
                    }
                }
                break;

            case "ld":
                switch (operand1) {

                    case "a":
                        try {
                            return new int[] {0x78 | code8bitsRegister(operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x3E, Integer.parseInt(operand1)};
                        }
                    case "b":
                        try {
                            return new int[] {0x40 | code8bitsRegister(operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x06, Integer.parseInt(operand1)};
                        }
                    case "c":
                        try{
                            return new int[] {0x48 | code8bitsRegister(operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x0E, Integer.parseInt(operand1)};
                        }
                    case "d":
                        try{
                            return new int[] {0x50 | code8bitsRegister(operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x16, Integer.parseInt(operand1)};
                        }
                    case "e":
                        try{
                            return new int[] {0x58 | code8bitsRegister(operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x1E, Integer.parseInt(operand1)};
                        }
                    case "h":
                        try{
                            return new int[] {0x60 | code8bitsRegister(operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x26, Integer.parseInt(operand1)};
                        }
                    case "l":
                        try{
                            return new int[] {0x68 | code8bitsRegister(operand2)};
                        }catch (Unrecognized8bitsRegister e){
                            return new int[]{0x2E, Integer.parseInt(operand1)};
                        }
                    case "bc": return new int[] {0x01, getLiteral(op2)};
                    case "de": return new int[] {0x11, getLiteral(op2)};
                    case "hl": return new int[] {0x21, getLiteral(op2)};
                    case "sp": return new int[] {0x31, getLiteral(op2)};
                    case "(hl)":
                        if ("(hl)"!=operand2){  // because the theoretical code for "ld (hl),(hl)" it's the same for "halt"
                            try {
                                return new int[] {0x70 | code8bitsRegister(operand2)};
                            }catch (Unrecognized8bitsRegister e){
                                return new int[]{0x36, Integer.parseInt(operand1)};
                            }
                        }
                        break;
                    case "(bc)":
                        if ("a"==operand2){
                            return new int[] {0x02};
                        }
                        break;
                    case "(de)":
                        if ("a"==operand2){
                            return new int[] {0x12};
                        }
                        break;
                    default:
                        Pattern p = Pattern.compile("\\(\\s*(?<n>\\w+)\\s*\\)");
                        Matcher m =p.matcher(operand1);
                        int literal = getLiteral(m.group("n"));
                        switch (operand2) {
                            case "hl":
                                return new int[] {0x22, literal};
                            case "a":
                                return new int[] {0x23, literal};
                        }

                }
                break;

            case "jr":
                switch (operand1){
                    case "nz":  return new int[] {0x20, getOffset(operand2)};
                    case "z":   return new int[] {0x28, getOffset(operand2)};
                    case "nc":  return new int[] {0x30, getOffset(operand2)};
                    case "c":   return new int[] {0x38, getOffset(operand2)};
                    default:
                        if (""==operand2){
                            return new int[] {0x18, getOffset(operand1)};
                        }
                }
                break;

            case "ret":
                if (""!=operand2) break;
                switch (operand1){
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
                switch (operand1){
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
                        if (""==operand2){
                            return getJumpCode(0xC3, operand1);
                        }
                }
                break;

        }
        throw new Exception("not recognized "+operation+" "+operand1+" "+operand2);

    }

    private int[] getJumpCode(int opCode, String operand) {
        int address = getLiteral(operand);

        return new int[] {opCode, address >>> 8, address & 0xff};
    }

    /**
     * code a 8'bits register into its code for ALU
     * @param reg
     * @return
     * @throws Exception
     */
    protected int code8bitsRegister(String reg) throws Exception {
        switch (reg){
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

    /**
     * @TODO: interpret hexadecimal, octal and  binary literals, and CONSTANTS
     * @param literal
     * @return
     */
    protected int getLiteral(String literal)
    {
        return Integer.parseInt(literal);
    }

    protected int getOffset(String offset)
    {
        int off = Integer.parseInt(offset);
        if (off!=0){
            return off;
        }

        pending = true;
        pendingCause = offset;
        return 0x00;
    }

    public int getSize() {
        return opCode.length;
    }

    public int[] getOpCode() {
        return opCode;
    }

    public boolean isPending() {
        return pending;
    }

    private class Unrecognized8bitsRegister extends Exception{
        public Unrecognized8bitsRegister(String register) {
            super("unrecognized "+register+" trying to figure out a 8 bit register");
        }
    }
}
