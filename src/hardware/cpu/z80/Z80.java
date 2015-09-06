package hardware.cpu.z80;

import hardware.system.MemorySystemInterface;

/**
 * Created by joseluislaso on 05/09/15.
 */
public class Z80 {

    protected static long STD_CLK = 4000000;
    protected long clock;
    protected MemorySystemInterface memory = null;
    protected boolean halted = true;
    protected long mcycles = 0;
    protected long tstates = 0;

    // registers
    public int A, F, B, C, D, E, H, L, I, R;
    protected int PC;
    protected int SP;
    // interrupt
    public boolean IFF1 = false; // NMI interrupt flip-flop
    public boolean IFF0 = false; // IRQ interrupt flip-flop
    public int IM = 0;    // interrupt mode

    public Z80() {
        this(STD_CLK);
    }

    public Z80 (long clock){
        this.clock = clock;
    }

    public void attachSystemMemory(MemorySystemInterface memory){
        this.memory = memory;
    }

    public void run (int PC) {
        halted = false;

        while (!halted) {
            step();
        }
    }

    public void reset() {
        PC = 0;
        SP = 0x10000;
        H = L = 0;
        D = E = 0;
        B = C = 0;
        memory.reset();
    }

    // 16 bit registers
    protected int HL() {
        return H*256 + L;
    }
    protected int BC() {
        return B*256 + C;
    }
    protected int DE() {
        return D*256 + E;
    }
    protected void HL(int HL){
        H = HL >>> 8;
        L = HL & 0xFF;
    }
    protected void BC(int BC){
        B = BC >>> 8;
        C = BC & 0xFF;
    }
    protected void DE(int DE){
        D = DE >>> 8;
        E = DE & 0xFF;
    }

    protected int carryFlag(){
        return F & 1;
    }


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

    protected void step() {

        int opcode = memory.peek(PC++);

        switch (opcode) {

            case 0x00: // NOP
                t(1,4);
                break;

            case 0x76: // HALT
                t(1,4);
                halted = true;
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
