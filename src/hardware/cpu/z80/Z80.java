package hardware.cpu.z80;

import assembler.Tools;
import hardware.system.MemorySystemInterface;

/**
 * Created by joseluislaso on 05/09/15.
 */
public class Z80 {

    final public static long STD_CLK = 4000000;
    final public static boolean DEBUG = true;
    protected long clock;
    protected MemorySystemInterface memory = null;
    protected boolean halted = true;
    protected long mcycles = 0;
    protected long tstates = 0;
    protected String currentInstruction = "";

    // registers
    public int A, F, B, C, D, E, H, L, I, R;
    protected int PC;
    protected int SP;
    // interrupt
    public boolean IFF1 = false; // NMI interrupt flip-flop
    public boolean IFF0 = false; // IRQ interrupt flip-flop
    public int IM = 0;    // interrupt mode

    protected boolean debug = false;

    public Z80() {
        this(STD_CLK, false);
    }

    public Z80(boolean debug) {
        this(STD_CLK, debug);
    }

    public Z80 (long clock, boolean debug){
        this.debug = debug;
        this.clock = clock;
        reset();
    }

    public void attachSystemMemory(MemorySystemInterface memory){
        this.memory = memory;
    }

    public void run (int PC) {
        halted = false;
        this.PC = PC;

        while (!halted) {

            if (debug)
                dumpRegisters();

            step();

            if (debug) {
                System.out.println(" "+currentInstruction);
                currentInstruction = "";
            }
        }
    }

    public void reset() {
        PC = 0;
        SP = 0x1000;
        H = L = 0;
        D = E = 0;
        B = C = 0;

        if(memory != null) {
            memory.reset();
        }
    }

    // 16 bit registers
    protected int HL() {
        return (H << 8) | L;
    }
    protected void HL(int HL){
        H = HL >>> 8;
        L = HL & 0xFF;
    }
    protected void HL(int h, int l){
        H = h;
        L = l;
    }

    protected final int BC() { return (B<<8) | C; }
    protected void BC(int BC){
        B = BC >>> 8;
        C = BC & 0x00FF;
    }
    protected void BC(int b, int c){
        B = b;
        C = c;
    }
    protected int incByte(int b)
    {
        b = (b+1) & 0x00ff;
        if (b==0) F = F | 1;

        return b;
    }
    protected int decByte(int b)
    {
        b = (b-1) & 0x00ff;
        if (b==255) F = F | 1;

        return b;
    }

    protected int addByte(int a, int b)
    {
        int c = (a+b) & 0x00ff;
        if (a+b > 255) F = F | 1;

        return c;
    }

    protected int DE() {
        return (D << 8) | E;
    }
    protected void DE(int DE){
        D = DE >>> 8;
        E = DE & 0xFF;
    }
    protected void DE(int d, int e){
        D = d;
        E = e;
    }

    protected int carryFlag(){
        return F & 1;
    }
    protected void setCarryFlag(){
        F = F | 1;
    }


    /**
     * EXECUTE INSTRUCTIONS
     */

    protected void run_ED_opcode() {

        int opcode = memory.peek(PC++);

        switch (opcode) {

            case 0x46: // set IM to 0
                t(2,8);
                IM = 0;
                break;

            case 0x56: // set IM to 1
                t(2,8);
                IM = 1;
                break;

            case 0x5E: // set IM to 2
                t(2,8);
                IM = 2;
                break;

            // HL <- HL + ss + C
            case 0x4a:
            case 0x5a:
            case 0x6a:
            case 0x7a:
                t(4,15);
                adcHL(ssValue((opcode & 0b00110000) >>> 4));
                break;

        }
    }

    protected final int add16(int a, int b) {
        int result = a + b;
        F = (F & 0xc4) | (((a ^ result ^ b) >> 8) & 0x10) | ((result >> 16) & 1);
        return (result & 0xffff);
    }

    /**
     * HL <-  HL + nn + C
     */
    private final void adcHL(int value) {
        int tmp = HL();
        int result = tmp + value + carryFlag();
        F = (((tmp ^ result ^ value) >> 8) & 0x10) |
            ((result >> 16) & 1) |   // carry flag
            ((result >> 8) & 0x80) |
            (((result & 0xffff) != 0) ? 0 : 0x40) |
            (((value ^ tmp ^ 0x8000) & (value ^ result) & 0x8000) >> 13);

        H = (result >> 8) & 0xff;
        L = result & 0xff;
    }

    protected int ssValue(int ss) {
        switch (ss) {
            case 0x00: return BC();
            case 0x01: return DE();
            case 0x02: return HL();
            case 0x03: return SP;
        }
        return 0;
    }

    protected void t(int m, int t){
        mcycles+=m;
        tstates+=t;
    }

    protected void dumpRegisters()
    {
        System.out.println(
                "A:"+Tools.byteToHex(A)+ "|" +
                "F:"+Tools.byteToHex(F)+ "|" +
                "I:"+Tools.byteToHex(I)+ "|" +
                "R:"+Tools.byteToHex(R)+ "|" +
                "BC:"+Tools.addressToHex(BC())+ "|" +
                "DE:"+Tools.addressToHex(DE())+ "|" +
                "HL:"+Tools.addressToHex(HL())+ "|" +
                "SP:"+Tools.addressToHex(SP)+ "|" +
                "PC:"+Tools.addressToHex(PC)+ "|"
        );
    }

    /**
     * read a byte from memory
     *
     * @param address
     * @return
     */
    protected int readMem(int address)
    {
        int read = memory.peek(address) ;
        if (debug) System.out.print(Tools.byteToHex(read)+" ("+read+") ");
        return read;
    }

    protected String set16bRegister(int r, int hi, int lo)
    {
        switch (r){

            case 0x00: // BC
                BC(hi,lo);
                return "BC";

            case 0x01: // DE
                DE(hi,lo);
                return "DE";

            case 0x10: // HL
                HL(hi,lo);
                return "HL";

            case 0x11: // SP
                SP = (hi << 8) | lo;
                return "SP";

        }

        return "?";
    }

    protected String inc8bRegister(int r)
    {
        switch (r){
            case 0: B=incByte(B); return "B";
            case 1: C=incByte(C); return "C";
            case 2: D=incByte(D); return "D";
            case 3: E=incByte(E); return "E";
            case 4: H=incByte(H); return "H";
            case 5: L=incByte(L); return "L";
            //case 6:
            case 7: A=incByte(A); return "A";
        }

        return "?";
    }

    protected String set8bRegister(int r, int value)
    {
        switch (r){
            case 0: B=value; return "B";
            case 1: C=value; return "C";
            case 2: D=value; return "D";
            case 3: E=value; return "E";
            case 4: H=value; return "H";
            case 5: L=value; return "L";
            //case 6:
            case 7: A=value; return "A";
        }

        return "?";
    }

    protected String add8bRegisterToA(int r)
    {
        switch (r){
            case 0: A=addByte(A,B); return "B";
            case 1: A=addByte(A,C); return "C";
            case 2: A=addByte(A,D); return "D";
            case 3: A=addByte(A,E); return "E";
            case 4: A=addByte(A,H); return "H";
            case 5: A=addByte(A,L); return "L";
            case 6: A=addByte(A,readMem(HL())); return "(HL)";
            case 7: A=addByte(A,A); return "A";
        }

        return "?";
    }

    protected int c2byte(int value)
    {
        return 256 - (byte)value;
    }

    /**
     * executes one instruction
     */
    protected void step() {

        String reg;
        if (debug) System.out.print(Tools.addressToHex(PC)+": ");

        int opcode = readMem(PC++);

        switch (opcode) {

            case 0x00: // NOP
                t(1, 4);
                if (debug) currentInstruction = "NOP";
                break;

            case 0x10:  // djnz
                byte offset = (byte)readMem(PC++);
                System.out.println(offset);
                int address = PC + c2byte(offset);
                B=decByte(B);
                if (B==0){
                    t(2,8);
                }else{
                    t(3,13);
                    PC = address;
                }
                if (debug) currentInstruction = "DJNZ "+Tools.addressToHex(address);
                System.out.println(currentInstruction);
                System.exit(0);
                break;

            case 0x01:
            case 0x11:
            case 0x21:
            case 0x31:
                t(3,10);
                int lo = readMem(PC++);
                int hi = readMem(PC++);
                reg = set16bRegister((opcode >>> 4) & 0x03, hi, lo);
                if (debug) currentInstruction = "LD "+reg+","+Tools.byteToHex(hi)+Tools.byteToHex(lo)+"h";
                break;

            case 0x04:
            case 0x0C:
            case 0x14:
            case 0x1C:
            case 0x24:
            case 0x2C:
            case 0x34:
            case 0x3C:
                t(1, 4);
                reg = inc8bRegister((opcode >>> 3) & 0x07);
                if (debug) currentInstruction = "INC "+reg;
                break;

            case 0x06:
            case 0x0E:
            case 0x16:
            case 0x1E:
            case 0x26:
            case 0x2E:
            case 0x36:
            case 0x3E:
                t(2,7);
                int operand = readMem(PC++);
                reg = set8bRegister((opcode >>> 3) & 0x07, operand);
                if (debug) currentInstruction = "LD "+reg+","+Tools.byteToHex(operand)+"h";
                break;

            case 0x76: // HALT
                t(1,4);
                halted = true;
                break;

            case 0x80:
            case 0x81:
            case 0x82:
            case 0x83:
            case 0x84:
            case 0x85:
            case 0x86:
            case 0x87:
                t(1,4);
                reg = add8bRegisterToA(opcode & 0x07);
                if (debug) currentInstruction = "ADD A,"+reg;
                break;

            case 0xED:  // extended ED instructions
                run_ED_opcode();
                break;

            case 0xF3:  // DI
                t(1,4);
                IFF0 = IFF1 = false;
                break;

            case 0xFB:  // EI
                t(1,4);
                IFF0 = IFF1 = true;
                break;

            // arithmetic operations

            // HL <- HL + ss
            case 0x09:
            case 0x19:
            case 0x29:
            case 0x39:
                t(3,11);
                HL(add16(HL(),ssValue((opcode & 0b00110000) >>> 4)));
                break;



        }

    }

}
