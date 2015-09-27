#include <iostream>
#include <cstdio>
#include <string>
#include "Cpu.h"

using namespace std;

Cpu::Cpu()
{
    cycles=0;

    PC = 0;
    SP = 0xFFFF;
    *BC = *DE = *HL = *AF = 0;

    for (int i = 0; i < 255; ++i) {
        mfs[i] = &Cpu::unknown;
    }

    mfs[NOP]        = &Cpu::nop;
    mfs[LD_BC_NN]   = &Cpu::ld_bc_nn;
    mfs[LD_B_B]     = &Cpu::ld_b_b;
    mfs[HALT]       = &Cpu::halt;

    mfs[INC_B]      = &Cpu::inc_r;
    mfs[INC_C]      = &Cpu::inc_r;
    mfs[INC_D]      = &Cpu::inc_r;
    mfs[INC_E]      = &Cpu::inc_r;
    mfs[INC_H]      = &Cpu::inc_r;
    mfs[INC_H]      = &Cpu::inc_r;
    mfs[INC_HL_CONTENT]= &Cpu::unknown;
    mfs[INC_A]      = &Cpu::inc_r;

    mfs[JP_NN]      = &Cpu::jp_nn;
}

void Cpu::attachMemory(Memory memory)
{
    this->memory = memory;
}

long Cpu::getCycles()
{
    return cycles;
}

byte Cpu::readByte(word addr)
{
    byte read = memory.read(addr);
    sprintf(currentInstruction, "%s%02X:", currentInstruction, read);
    return read;
}

word Cpu::readWord(word addr)
{
    return readByte(addr) | (readByte(++addr) << 8);
}

void Cpu::run(word startAddress)
{
    PC = startAddress;

    do {
        sprintf(currentInstruction, "0x%04X: ", PC);

        opcode = readByte(PC++);

        cycles += (this->*mfs[opcode])();

        printf("%s\n", currentInstruction);

    } while (opcode != HALT);

}


int Cpu::unknown()
{
    printf("Unknow opcode 0x%02X\n", opcode);
    exit(-1);
    return 0;
}

void Cpu::setFlags(byte result, int sign, int zero, int half_carry, int par_over, int add_sub, int carry)
{
    if (result == 0 && zero == 1) *F |= ZERO_FLAG;
    if ((par_over == 0x7F) && (result == par_over)) *F |= PAR_OV_FLAG;
    if ((sign == 1) && (result & 0b10000000)) *F |= SIGN_FLAG;
    if (add_sub == 0) *F &= ~ADD_SUB_FLAG;
    if (half_carry == 1) *F |= H_CARRY_FLAG;
    if (half_carry == 0) *F &= ~H_CARRY_FLAG;
    if (carry == 1) *F |= CARRY_FLAG;
    if (carry == 0) *F &= ~CARRY_FLAG;
}

int Cpu::nop()
{
    cout << "NOP" << endl;
    return 4;
}

int Cpu::inc_r()
{
    int  rCode = (opcode >> 3 & 0x07);
    bool halfCarry = (*r[rCode] & 0b0001111) > 0;
    *r[rCode]++;
    setFlags(*r[rCode], 1, 1, (halfCarry ? 1 : 0), 0x7F, 0, -1);
    sprintf(currentInstruction, "%-*s INC %s", align, currentInstruction, rName[rCode]);
    return 10;
}

int Cpu::ld_bc_nn()
{
    *BC = readWord(PC);
    PC += 2;
    sprintf(currentInstruction, "%-*s LD BC, 0x%04X", align, currentInstruction, *BC);
    return 10;
}
int Cpu::jp_nn()
{
    PC = readWord(PC);
    sprintf(currentInstruction, "%-*s JP 0x%04X", align, currentInstruction, PC);
    return 10;
}
int Cpu::ld_b_b()
{
    sprintf(currentInstruction,"%-*s LD B,B", align, currentInstruction);
    return 4;
}
int Cpu::halt()
{
    sprintf(currentInstruction, "%-*s HALT", align, currentInstruction);
    return 4;
}

