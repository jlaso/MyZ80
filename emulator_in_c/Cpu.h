#ifndef INSTRUCTION_H
#define INSTRUCTION_H

#include "general.h"
#include "Memory.h"
#include <string>

using namespace std;

class Cpu
{
private:
    record16 _AF;
    word* AF = &_AF.rr;
    byte* A  = &_AF.r.hi;
    byte* F  = &_AF.r.lo;
    record16 _BC;
    word* BC = &_BC.rr;
    byte* B  = &_BC.r.hi;
    byte* C  = &_BC.r.lo;
    record16 _DE;
    word* DE = &_DE.rr;
    byte* D  = &_DE.r.hi;
    byte* E  = &_DE.r.lo;
    record16 _HL;
    word* HL = &_HL.rr;
    byte* H  = &_HL.r.hi;
    byte* L  = &_HL.r.lo;
    record16 _IX;
    word* IX = &_IX.rr;
    byte* Ix = &_IX.r.hi;
    byte* X  = &_IX.r.lo;
    record16 _IY;
    word* IY = &_IY.rr;
    byte* Iy = &_IY.r.hi;
    byte* Y  = &_IY.r.lo;

    word PC, SP;

    long cycles;
    Memory memory;
    byte opcode;

    virtual int unknown();
    virtual int nop();
    virtual int inc_a();
    virtual int ld_bc_nn();
    virtual int halt();
    virtual int ld_b_b();
    virtual int jp_nn();

    typedef int(Cpu::*PtrFunction)(void);
    Cpu::PtrFunction mfs[0xFF];
    void pp(char* msg, word data);
    void pp(char* msg);
    void setFlags(byte result, int sign, int zero, int half_carry, int par_over, int add_sub, int carry);

    static const byte SIGN_FLAG     = 0b10000000;
    static const byte ZERO_FLAG     = 0b01000000;
    static const byte ADD_SUB_FLAG  = 0b00010000;
    static const byte PAR_OV_FLAG   = 0b00000100;
    static const byte CARRY_FLAG    = 0b00000001;
    static const byte H_CARRY_FLAG = 0b100000000;

public:

    Cpu();
    void attachMemory(Memory memory);
    void run(word startAddress);
    void execute(int opcode);
    long getCycles();

    static const byte NOP            = 0x00;
    static const byte LD_BC_NN       = 0x01;
    static const byte INC_A          = 0x3C;
    static const byte LD_B_B         = 0x40;
    static const byte HALT           = 0x76;
    static const byte JP_NN          = 0xC3;

};

#endif // INSTRUCTION_H
