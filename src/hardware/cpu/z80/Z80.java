package hardware.cpu.z80;

import assembler.Tools;
import common.$;
import hardware.devices.peripheral.MapIO;
import hardware.system.MemorySystemInterface;
import myz80.StatusBarPanel;

/**
 * Created by joseluislaso on 05/09/15.
 */
public class Z80 {

    final public static long STD_CLK = 4000000;
    protected long clock;
    protected long factor;
    protected MemorySystemInterface memory = null;
    protected MapIO mapIO = null;
    protected boolean halted = true;
    protected long mcycles = 0;
    protected long tstates = 0;
    protected String currentInstruction = "";
    protected int opcode;

    protected String[] ssName = { /* 00 */ "BC", /* 01 */ "DE", /* 10 */ "HL", /* 11 */ "SP" };
    protected String[] rName = {
        "B",    // 000
        "C",    // 001
        "D",    // 010
        "E",    // 011
        "H",    // 100
        "L",    // 101
        "(HL)", // 110
        "A"     // 111
    };

    // registers
    public int A, F, B, C, D, E, H, L, I, R;
    protected int PC;
    protected int SP;
    protected int IX;
    protected int IY;
    // interrupt
    public boolean IFF1 = false; // NMI interrupt flip-flop
    public boolean IFF0 = false; // IRQ interrupt flip-flop
    public int IM = 0;    // interrupt mode

    protected boolean isED, isDD, isFD;

    protected boolean debug = false;
    protected StatusBarPanel statusPanel = null;

    public Z80() {
        this(STD_CLK, false);
    }

    public Z80(boolean debug) {
        this(STD_CLK, debug);
    }

    public Z80 (long clock, boolean debug){
        this.debug = debug;
        this.clock = clock;
        factor = 1000000000/clock; // to deal with nanoseconds
        reset();
    }

    public void attachSystemMemory(MemorySystemInterface memory){
        this.memory = memory;
        if (SP==0) SP = memory.lastRAMpos();
    }

    public void attachIOSpace(MapIO mapIO) {
        this.mapIO = mapIO;
    }

    public void attachStatusPanel(StatusBarPanel statusPanel) {
        this.statusPanel = statusPanel;
    }

    public void run (int PC) throws InterruptedException {
        halted = false;
        this.PC = PC;
        long startTime = System.nanoTime(), prevEndTime, realEndTime;

        while (!halted) {

            if (debug) dumpRegisters();

            step();

            if (statusPanel != null) statusPanel.setText("> "+currentInstruction);
            if (debug) System.out.println(" " + currentInstruction);

            currentInstruction = "";

            prevEndTime = startTime + factor*tstates;  // 7/4000000 = 0,00000175 seconds  1.75 microseconds
            realEndTime = System.nanoTime();

            int delay = (int)((prevEndTime - realEndTime)/1000000);
            if (delay  > 0){
                Thread.sleep(delay);
            }
            statusPanel.setText2(""+(prevEndTime - realEndTime)+" <");

            if ((opcode != 0xF3) && (opcode != 0xFB)) {   // DI & EI don't handle interrupts immediatelly
                handleInterrutps();
            }
        }
    }

    public void handleInterrutps() {

    }

    public void reset() {
        PC = 0;
        SP = 0;
        H = L = 0;
        D = E = 0;
        B = C = 0;
        A = F = 0;
        IX = IY = 0;

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

    protected int subByte(int a, int b)
    {
        int c = (a-b) & 0x00ff;
        F = F | 0b00000010;

        return c;
    }

    protected int addByte(int a, int b)
    {
        int c = (a+b) & 0x00ff;
        if (a+b > 255) F = F | 1;

        return c;
    }

    protected int adcByte(int a, int b)
    {
        int c = (a+b+(F|1)) & 0x00ff;
        if (a+b+(F|1) > 255) F = F | 1;

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

    protected int AF() {
        return (A << 8) | F;
    }
    protected void AF(int AF){
        A = AF >>> 8;
        F = AF & 0xFF;
    }
    protected void AF(int a, int f){
        A = a;
        F = f;
    }

    protected int carryFlag(){
        return F & 1;
    }
    protected void setCarryFlag(){
        F = F | 1;
    }


    protected final int add16(int a, int b) {
        int result = a + b;
        F = (F & 0xc4) | (((a ^ result ^ b) >> 8) & 0x10) | ((result >> 16) & 1);
        return (result & 0xffff);
    }

    /**
     * HL <- HL + ss
     *
     * @param ss
     * @return
     */
    private String addSSToHL(int ss)
    {
        String reg = "";
        int value = 0;

        switch (ss) {
            case 0x00: reg="BC"; value=BC(); break;
            case 0x01: reg="DE"; value=DE(); break;
            case 0x02: reg="HL"; value=HL(); break;
            case 0x03: reg="SP"; value=SP; break;
        }

        HL(add16(HL(), value));

        return reg;
    }

    /**
     * HL <-  HL + ss + C
     *
     * @param ss
     * @return
     */
    private String adcSSToHL(int ss) {
        String reg = "";
        int value = 0;

        switch (ss) {
            case 0x00: reg="BC"; value=BC(); break;
            case 0x01: reg="DE"; value=DE(); break;
            case 0x02: reg="HL"; value=HL(); break;
            case 0x03: reg="SP"; value=SP; break;
        }

        int tmp = HL();
        int result = tmp + value + carryFlag();
        F = (((tmp ^ result ^ value) >> 8) & 0x10) |
                ((result >> 16) & 1) |   // carry flag
                ((result >> 8) & 0x80) |
                (((result & 0xffff) != 0) ? 0 : 0x40) |
                (((value ^ tmp ^ 0x8000) & (value ^ result) & 0x8000) >> 13);

        H = (result >> 8) & 0xff;
        L = result & 0xff;

        return reg;
    }

    /**
     * returns the register of 16 bits indicated by "ss"
     *
     * @param ss
     * @return
     */
    protected int ssValue(int ss) {
        switch (ss) {
            case 0x00: return BC();
            case 0x01: return DE();
            case 0x02: return HL();
            case 0x03: return SP;
        }
        return 0;
    }


    // wrappers for flags

    protected boolean isSignPositive(){ return (F & 0b10000000) == 0;}
    protected boolean isSignNegative(){ return (F & 0b10000000) > 0;}
    protected boolean isZero(){ return (F & 0b01000000) > 0;}
    protected void setZero(){ F |= 0b01000000; }
    protected boolean isNonZero(){ return (F & 0b01000000) == 0;}
    protected boolean isCarry(){ return (F & 0b00000001) > 0;}
    protected boolean isNoCarry(){ return (F & 0b00000001) == 0;}
    protected boolean isParityOdd(){ return (F & 0b00000100) == 0;}
    protected boolean isParityEven(){ return (F & 0b00000100) > 0;}
    protected boolean isSubstract(){ return (F & 0b00000010) > 0;}
    protected boolean isHalfCarry(){ return (F & 0b00010000) > 0;}

    /**
     * clock and machine cycles control
     *
     * @param m
     * @param t
     */
    protected void t(int m, int t){
        mcycles+=m;
        tstates+=t;
    }

    protected void dumpStack() {
        String s = "*";
        for (int i = SP; i < SP+12; i++) {
            s += "["+Tools.addressToHex(i)+"] "+Tools.byteToHex(memory.peek(i))+" | ";
        }
        Tools.println("red", $.CR + s);
    }

    /**
     * prints the content of the registers
     */
    protected void dumpRegisters()
    {
        System.out.println(
                "~~~~~|"+
                "A:"+Tools.byteToHex(A)+ "("+A+")|" +
                "F:"+Tools.byteToHex(F)+
                        "[S:"+(isSignNegative()?"X":"-")+"]"+
                        "[Z:"+(isZero()?"X":"-")+"]"+
                        "[H"+(isHalfCarry()?"X":"-")+"]"+
                        "[P/V:"+(isParityEven()?"X":"-")+"]"+
                        "[N:"+(isSubstract()?"X":"-")+"]"+
                        "[C:"+(isCarry()?"X":"-")+"]"+
                     "|" +
                "I:"+Tools.byteToHex(I)+ "|" +
                "R:"+Tools.byteToHex(R)+ "|" +
                "BC:"+Tools.addressToHex(BC())+"("+BC()+")|" +
                "DE:"+Tools.addressToHex(DE())+"("+DE()+")|" +
                "HL:"+Tools.addressToHex(HL())+"("+HL()+")|" +
                "SP:"+Tools.addressToHex(SP)+ "|" +
                "PC:"+Tools.addressToHex(PC)+
                "|~~~~~"
        );
    }

    /**
     * read a byte from memory
     *
     * @param address int
     * @return int
     */
    protected int readMem(int address)
    {
        int read = memory.peek(address) ;
        if (debug) System.out.print(Tools.byteToHex(read)+" ("+read+") ");
        return read;
    }

    /**
     * Read a word from memory
     *
     * @param address int
     * @return int
     */
    protected int readWord(int address)
    {
        int lo = readMem(address);
        int hi = readMem(address+1);
        int value = (hi << 8) | lo;
        if (debug) System.out.print(Tools.addressToHex(value)+" ("+value+") ");
        return value;
    }

    /**
     * set a register of 16 bits identified by "ss" and returns its mnemonic
     *
     * @param ss int
     * @param hi int
     * @param lo int
     * @return String
     */
    protected String set16bRegister(int ss, int hi, int lo)
    {
        switch (ss){

            case 0b00: // BC
                BC(hi,lo);
                return "BC";

            case 0b01: // DE
                DE(hi,lo);
                return "DE";

            case 0b10: // HL
                HL(hi,lo);
                return "HL";

            case 0b11: // SP
                SP = (hi << 8) | lo;
                return "SP";

        }

        return "?";
    }

    /**
     * increments the register of 8 bits identified by "r" and returns its mnemonic
     *
     * @param r int
     * @return String
     */
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

    protected void decSSregister(int ss) {
        // @TODO: flags ?
        switch (ss) {
            case 0b00: BC(BC()-1); break;
            case 0b01: DE(DE() - 1); break;
            case 0b10: HL(HL() - 1); break;
            case 0b11: SP--; break;
        }
    }

    /**
     * sets the register of 8 bits identified by "r" and returns its mnemonic
     *
     * @param r int
     * @param value int
     * @return String
     */
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

    /**
     * sets A register from register of 8 bits identified by "r" and returns its mnemonic
     *
     * @param r int
     * @return String
     */
    protected String setAfrom8bRegister(int r)
    {
        // @TODO: flags ?
        switch (r){
            case 0: A=B; return "B";
            case 1: A=C; return "C";
            case 2: A=D; return "D";
            case 3: A=E; return "E";
            case 4: A=H; return "H";
            case 5: A=L; return "L";
            case 6: A=readMem(HL()); return "(HL)";
            case 7: A=A; return "A";
        }

        return "?";
    }

    /**
     * add the content of the register of 8 bits identified by "r" to Accumulator and returns its mnemonic
     *
     * @param r int
     * @return String
     */
    protected String sub8bRegisterToA(int r)
    {
        switch (r){
            case 0: A=subByte(A, B); return "B";
            case 1: A=subByte(A, C); return "C";
            case 2: A=subByte(A, D); return "D";
            case 3: A=subByte(A, E); return "E";
            case 4: A=subByte(A, H); return "H";
            case 5: A=subByte(A, L); return "L";
            case 6: A=subByte(A, readMem(HL())); return "(HL)";
            case 7: A=subByte(A, A); return "A";
        }

        return "?";
    }

    /**
     * add the content of the register of 8 bits identified by "r" to Accumulator and returns its mnemonic
     *
     * @param r int
     * @return String
     */
    protected String add8bRegisterToA(int r)
    {
        switch (r){
            case 0: A=addByte(A, B); return "B";
            case 1: A=addByte(A, C); return "C";
            case 2: A=addByte(A, D); return "D";
            case 3: A=addByte(A, E); return "E";
            case 4: A=addByte(A, H); return "H";
            case 5: A=addByte(A, L); return "L";
            case 6: A=addByte(A, readMem(HL())); return "(HL)";
            case 7: A=addByte(A, A); return "A";
        }

        return "?";
    }

    /**
     * add the carry and the content of the register of 8 bits identified by "r" to Accumulator and returns its mnemonic
     *
     * @param r int
     * @return String
     */
    protected String adc8bRegisterToA(int r)
    {
        switch (r){
            case 0: A=adcByte(A,B); return "B";
            case 1: A=adcByte(A,C); return "C";
            case 2: A=adcByte(A,D); return "D";
            case 3: A=adcByte(A,E); return "E";
            case 4: A=adcByte(A,H); return "H";
            case 5: A=adcByte(A,L); return "L";
            case 6: A=adcByte(A,readMem(HL())); return "(HL)";
            case 7: A=adcByte(A,A); return "A";
        }

        return "?";
    }

    // break flow routines

    /**
     *
     * @param cond int
     * @param address int
     * @return Result
     */
    protected Result jump(int cond, int address)
    {
        Result result = checkCondition(cond);
        if (result.success) PC=address;
        return result;
    }

    /**
     * checks the condition indicated by cond
     * @param cond int
     * @return Result
     */
    protected Result checkCondition(int cond) {
        String reg = "";
        boolean ret = false;
        switch (cond){
            case 0b000: reg="NZ"; ret=isNonZero(); break;
            case 0b001: reg="Z"; ret=isZero(); break;
            case 0b010: reg="NC"; ret=isNoCarry(); break;
            case 0b011: reg="C"; ret=isCarry(); break;
            case 0b100: reg="PO"; ret=isParityOdd(); break;
            case 0b101: reg="PE"; ret=isParityEven(); break;
            case 0b110: reg="P"; ret=isSignPositive(); break;
            case 0b111: reg="M"; ret=isSignNegative(); break;
        }

        return new Result(ret, reg);
    }

    /**
     *
     * @param cond int
     * @return Result
     */
    protected Result ret(int cond)
    {
        Result result = checkCondition(cond);
        if (result.success) PC=pop();
        return result;
    }

    /**
     * pushes the content of the value in the stack
     * @param value int
     */
    protected void push (int value) {
        memory.poke(--SP, (int)(value >>> 8));
        memory.poke(--SP, (int)(value & 0x00ff));
        if (debug) dumpStack();
    }

    /**
     * fetches the value of 16 bits from the stack
     * @return int
     */
    protected int pop() {
        if (debug) dumpStack();
        return (int) (memory.peek(SP++) | (memory.peek(SP++) << 8));
    }

    /**
     * pushes the content of the register indicated by ss into the stack
     * @param ss int
     * @return String
     */
    protected String pushSS(int ss) {
        switch (ss) {
            case 0x00: push(BC()); return "BC";
            case 0x01: push(DE()); return "DE";
            case 0x02:
                if (isDD) {
                    push(IX); return "IX";
                } else if (isFD) {
                    push(IY); return "IY";
                }
                push(HL()); return "HL";
            case 0x03: push(AF()); return "AF";
        }
        return "";
    }

    /**
     * fetches from the stack the content of the register indicated by ss
     * @param ss int
     * @return String
     */
    protected String popSS(int ss) {
        switch (ss) {
            case 0x00: BC(pop()); return "BC";
            case 0x01: DE(pop()); return "DE";
            case 0x02:
                if (isDD) {
                    IX=pop(); return "IX";
                } else if (isFD) {
                    IY=pop(); return "IY";
                }
                HL(pop()); return "HL";
            case 0x03: AF(pop()); return "AF";
        }
        return "";
    }

    protected int r(int r) {
        switch (r){
            case 0: return B;
            case 1: return C;
            case 2: return D;
            case 3: return E;
            case 4: return H;
            case 5: return L;
            case 6: return readMem(HL());
            case 7: return A;
        }
        return 0;
    }

    protected void A(int value) {
        A = value;
        if (A == 0) setZero();
    }

//    protected int c2byte(int value)
//    {
//        return 256 - (byte)value;
//    }

    /**
     * executes the instruction pointed by PC
     */
    protected void step() {

        isED = isDD = false;

        String reg;   // temp variable
        int address;  // temp variable
        if (debug) System.out.print(Tools.addressToHex(PC)+": ");    // prints the current address (PC)
        Result result;

        opcode = readMem(PC++);

        switch (opcode) {

            case 0x00: // NOP
                t(1, 4);
                currentInstruction = "NOP";
                break;

            case 0x10:  // DJNZ
                byte offset = (byte)readMem(PC++);
                address = PC + offset;
                B=decByte(B);
                if (B==0){
                    t(2,8);
                }else{
                    t(3,13);
                    PC = address;
                }
                currentInstruction = "DJNZ "+Tools.addressToHex(address)+" ("+offset+")";
                break;

            case 0x01:  // LD ss,nn
            case 0x11:
            case 0x21:    // 0010 0001
            case 0x31:
                t(3,10);
                int lo = readMem(PC++);
                int hi = readMem(PC++);
                reg = set16bRegister((opcode >>> 4) & 0x03, hi, lo);
                currentInstruction = "LD "+reg+","+Tools.byteToHex(hi)+Tools.byteToHex(lo)+"h";
                break;

            case 0x04:  // INC
            case 0x0C:
            case 0x14:
            case 0x1C:
            case 0x24:
            case 0x2C:
            case 0x34:
            case 0x3C:
                t(1, 4);
                reg = inc8bRegister((opcode >>> 3) & 0x07);
                currentInstruction = "INC "+reg;
                break;

            case 0x78:   // LD A,r
            case 0x79:
            case 0x7A:
            case 0x7B:
            case 0x7C:
            case 0x7D:
            case 0x7E:
            case 0x7F:
                t(1,4);
                reg = setAfrom8bRegister(opcode & 0x07);
                currentInstruction = "LD A,"+reg;
                break;

            case 0x06:  // LD r,r
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
                currentInstruction = "LD "+reg+","+Tools.byteToHex(operand)+"h";
                break;

            case 0x76: // HALT
                t(1,4);
                halted = true;
                currentInstruction = "HALT";
                break;

            case 0x80:  // ADD A,r
            case 0x81:
            case 0x82:
            case 0x83:
            case 0x84:
            case 0x85:
            case 0x86:
            case 0x87:
                t(1,4);
                reg = add8bRegisterToA(opcode & 0x07);
                currentInstruction = "ADD A,"+reg;
                break;

            case 0x88:  // ADC A,r
            case 0x89:
            case 0x8A:
            case 0x8B:
            case 0x8C:
            case 0x8D:
            case 0x8E:
            case 0x8F:
                t(1,4);
                reg = adc8bRegisterToA(opcode & 0x07);
                currentInstruction = "ADC A,"+reg;
                break;

            case 0x90:  // SUB r   A <- A - r
            case 0x91:
            case 0x92:
            case 0x93:
            case 0x94:
            case 0x95:
            case 0x96:
            case 0x97:
                t(1,4);
                reg = sub8bRegisterToA(opcode & 0x07);
                currentInstruction = "SUB "+reg;
                break;

            case 0x0B:   // DEC ss
            case 0x1B:
            case 0x2B:
            case 0x3B:
                t(1, 6);
                int ss = (opcode & 0b00110000) >>> 4;
                decSSregister(ss);
                currentInstruction = "DEC "+ssName[ss];
                break;

            case 0xB0:   // OR r
            case 0xB1:
            case 0xB2:
            case 0xB3:
            case 0xB4:
            case 0xB5:
            case 0xB6:
            case 0xB7:
                int r = opcode & 0x7;
                if (r == 0b110) t(2,7); else t(1,4);
                currentInstruction = "OR "+rName[r];
                A( A | r(r) );
                break;

            case 0xC1:  // POP ss
            case 0xD1:
            case 0xE1:
            case 0xF1:
                t(3,10);
                reg = popSS((opcode >>> 4) & 0x03);
                currentInstruction = "POP "+reg;
                return;

            case 0xC5:  // PUSH ss
            case 0xD5:
            case 0xE5:
            case 0xF5:
                t(3,11);
                reg = pushSS((opcode >>> 4) & 0x03);
                currentInstruction = "PUSH "+reg;
                return;

            case 0xCD:  // CALL nn
                t(5, 17);
                address = readWord(PC); PC+=2;
                push(PC);
                PC = address;
                currentInstruction = "CALL "+Tools.addressToHex(address);
                return;

            case 0xC4:   // CALL cc,nn
            case 0xD4:
            case 0xE4:
            case 0xF4:
            case 0xCC:
            case 0xDC:
            case 0xEC:
            case 0xFC:
                address = readWord(PC); PC+=2;
                result = jump((opcode & 0b00111000) >>> 3, address);
                if (result.success) {
                    t(5,17);
                }else{
                    t(3,10);
                }
                currentInstruction = "CALL "+result.data+", "+Tools.addressToHex(address);
                break;

            case 0xC9:   // RET
                PC = pop();
                t(3,10);
                currentInstruction = "RET";
                break;

            case 0xC0:   // RET cc
            case 0xD0:
            case 0xE0:
            case 0xF0:
            case 0xC8:
            case 0xD8:
            case 0xE8:
            case 0xF8:
                result = ret((opcode & 0b00111000) >>> 3);
                if (result.success) {
                    t(3,11);
                }else{
                    t(1,5);
                }
                currentInstruction="RET "+result.data;
                break;

            case 0xED:  // extended ED instructions
                run_ED_opcode();
                break;

            case 0xDD:  // extended DD instructions
                run_DD_opcode();
                break;

            case 0xFD:  // extended FD instructions
                run_FD_opcode();
                break;

            case 0xF3:  // DI
                t(1,4);
                IFF0 = IFF1 = false;
                currentInstruction = "di";
                break;

            case 0xFB:  // EI
                t(1,4);
                IFF0 = IFF1 = true;
                currentInstruction = "EI";
                break;

            // IO operations
            case 0xD3:
                t(3,11);
                int n = readMem(PC++);
                currentInstruction = "OUT ("+Tools.byteToHex(n)+"),A";
                mapIO.write(n, A);
                break;


            // arithmetic operations

            case 0x09:  // HL <- HL + ss
            case 0x19:
            case 0x29:
            case 0x39:
                t(3,11);
                reg = addSSToHL((opcode & 0b00110000) >>> 4);
                currentInstruction = "ADD HL,"+reg;
                break;


            // break flow

            case 0xC3:  // JP address
                address = readWord(PC); PC+=2;
                PC = address;
                currentInstruction = "JP "+Tools.addressToHex(address);
                break;

            case 0xC2:  // JP cond,address
            case 0xCA:
            case 0xD2:
            case 0xDA:
            case 0xE2:
            case 0xEA:
            case 0xF2:
            case 0xFA:
                address = readWord(PC); PC+=2;
                result = jump((opcode & 0b00111000) >>> 3, address);
                if (result.success) {  // @TODO: books says that is the same time in any case
                    t(5,17);   // in the book isn't this (page 165)
                }else{
                    t(3,10);
                }
                currentInstruction = "JP "+result.data+" "+Tools.addressToHex(address);
                break;
        }

    }



    /**
     * EXECUTE INSTRUCTIONS WITH ED PREFIX
     */

    protected void run_ED_opcode() {

        isED = true;
        String reg; // temp variable
        int opcode = readMem(PC++);

        switch (opcode) {

            case 0x46: // set IM to 0
                t(2,8);
                IM = 0;
                currentInstruction = "IM 0";
                break;

            case 0x56: // set IM to 1
                t(2,8);
                IM = 1;
                currentInstruction = "IM 1";
                break;

            case 0x5E: // set IM to 2
                t(2,8);
                IM = 2;
                currentInstruction = "IM 2";
                break;

            // HL <- HL + ss + C
            case 0x4a:
            case 0x5a:
            case 0x6a:
            case 0x7a:
                t(4,15);
                reg = adcSSToHL((opcode & 0b00110000) >>> 4);
                currentInstruction = "ADC HL,"+reg;
                break;

        }
    }

    /**
     * EXECUTE INSTRUCTIONS WITH DD PREFIX
     */

    protected void run_DD_opcode() {

        isDD = true;
        String reg; // temp variable
        int opcode = readMem(PC++);

        switch (opcode) {

        }

    }

    /**
     * EXECUTE INSTRUCTIONS WITH FD PREFIX
     */

    protected void run_FD_opcode() {

        isFD = true;
        String reg; // temp variable
        int opcode = readMem(PC++);

        switch (opcode) {

        }

    }

    class Result {
        public String data;
        public boolean success;

        public Result(boolean success, String data) {
            this.data = data;
            this.success = success;
        }
    }


}
