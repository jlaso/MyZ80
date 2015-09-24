#include "Cpu.h"
#include <iostream>
#include <string>

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
    mfs[INC_A]      = &Cpu::inc_a;
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

void Cpu::run(word startAddress)
{
    PC = startAddress;

    do {
        printf("0x%02X: ", PC);

        opcode = memory.read(PC++);

        printf("[0x%02X] ", opcode);

        cycles += (this->*mfs[opcode])();

    } while (opcode != HALT);

}

void Cpu::pp(char* msg, word data)
{
    printf("%s 0x%04X\n", msg, data);
}
void Cpu::pp(char* msg)
{
    printf("%s\n", msg);
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

int Cpu::inc_a()
{
    bool hc = (*A & 0b0001111) > 0;
    *A++;
    setFlags(*A, 1, 1, (hc ? 1 : 0), 0x7F, 0, -1);
    pp("INC A");
    return 10;
}

int Cpu::ld_bc_nn()
{
    *BC = memory.readWord(PC);
    PC += 2;
    pp("LD BC,",*BC);
    return 10;
}
int Cpu::jp_nn()
{
    PC = memory.readWord(PC);
    pp("JP ",PC);
    return 10;
}
int Cpu::ld_b_b()
{
    cout << "LD B,B" << endl;
    return 4;
}
int Cpu::halt()
{
    cout << "HALT" << endl;
    return 4;
}

