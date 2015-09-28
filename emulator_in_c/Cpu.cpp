#include <iostream>
#include <cstdio>
#include <string>
#include "Cpu.h"

#define DEBUG

using namespace std;

Cpu::Cpu()
{
    reset();
}

/**
 * executes the reset of the chip
 */
void Cpu::reset()
{
    cycles=0;
    PC = 0;
    *AF_ = *AF = SP = 0xFFFF;
    *BC  = *DE  = *HL  = 0xFFFF;
    *BC_ = *DE_ = *HL_ = 0xFFFF;
    IFF1 = IFF2 = false;
    IM = I = R = 0;
    registersBank = 0;
}

/**
 * connects the memory to the chip
 */
void Cpu::attachMemory(Memory memory)
{
    this->memory = memory;
}

/**
 * reads a byte from the memory attached
 */
byte Cpu::readByte(word addr)
{
    byte read = memory.read(addr);
#ifdef DEBUG
    sprintf(currentInstruction, "%s%02X:", currentInstruction, read);
#endif
    return read;
}

/**
 * reads a word from the memory attached
 */
word Cpu::readWord(word addr)
{
    return readByte(addr++) | (readByte(addr) << 8);
}

/**
 * starts the execution of instructions from the address especified
 */
void Cpu::run(word startAddress)
{
    PC = startAddress;
    loop:
#ifdef DEBUG
    sprintf(currentInstruction, "0x%04X: ", PC);
#endif
    opcode = readByte(PC++);
    cycles += step();
#ifdef DEBUG
    printf("%s\n", currentInstruction);
#endif
    if (opcode == HALT) exit(0);

    goto loop;
}

/**
 * decodes the current opcode
 */
int Cpu::step()
{
    switch (opcode) {

        case NOP:
#ifdef DEBUG
            printf(currentInstruction, "%-*s NP", align, currentInstruction);
#endif
            return 4;

        case HALT:
#ifdef DEBUG
            sprintf(currentInstruction, "%-*s HALT", align, currentInstruction);
#endif
            return 4;

        case JP_NN:
            return jp_nn();

        case LD_BC_NN: wPrev = *BC; wAfter = *BC = readWord(PC); goto ld_rr_nn;
        case LD_DE_NN: wPrev = *DE; wAfter = *DE = readWord(PC); goto ld_rr_nn;
        case LD_HL_NN: wPrev = *HL; wAfter = *HL = readWord(PC); goto ld_rr_nn;
        case LD_SP_NN: wPrev = SP;  wAfter =  SP = readWord(PC);  goto ld_rr_nn;
        ld_rr_nn:
            PC += 2;
#ifdef DEBUG
            sprintf(currentInstruction, "%-*s LD %s, 0x%04X", align, currentInstruction, rrName(opcode >> 4), wAfter);
#endif
            return 10;

        case INC_A: bPrev = *A++; bAfter = *A; goto inc_r;
        case INC_B: bPrev = *B++; bAfter = *B; goto inc_r;
        case INC_C: bPrev = *C++; bAfter = *C; goto inc_r;
        case INC_D: bPrev = *D++; bAfter = *D; goto inc_r;
        case INC_E: bPrev = *E++; bAfter = *E; goto inc_r;
        case INC_H: bPrev = *H++; bAfter = *H; goto inc_r;
        case INC_L: bPrev = *L++; bAfter = *L; goto inc_r;
        inc_r:
            setFlags(bAfter, 1, 1, ((bPrev & 0b0001111) > 0 ? 1 : 0), 0x7F, 0, -1);
            #ifdef DEBUG
            sprintf(currentInstruction, "%-*s INC %s", align, currentInstruction, rName((opcode >> 3) & 0x07));
            #endif
            return 10;

        case LD_B_N: bPrev = *B; bAfter = *B = readByte(PC++); goto ld_r_n;
        case LD_C_N: bPrev = *C; bAfter = *C = readByte(PC++); goto ld_r_n;
        case LD_D_N: bPrev = *D; bAfter = *D = readByte(PC++); goto ld_r_n;
        case LD_E_N: bPrev = *E; bAfter = *E = readByte(PC++); goto ld_r_n;
        case LD_H_N: bPrev = *H; bAfter = *H = readByte(PC++); goto ld_r_n;
        case LD_L_N: bPrev = *L; bAfter = *L = readByte(PC++); goto ld_r_n;
        case LD_A_N: bPrev = *A; bAfter = *A = readByte(PC++); goto ld_r_n;
        ld_r_n:
#ifdef DEBUG
            sprintf(currentInstruction, "%-*s LD %s,0x%02X", align, currentInstruction, rName((opcode >> 3) & 0x07), bAfter);
#endif
            return 7;

        case LD_HL_CONTENT:
            bPrev = readByte(*HL); bAfter = readByte(PC++); memory.write(*HL, bAfter);
#ifdef DEBUG
            sprintf(currentInstruction, "%-*s LD (HL),0x%02X", align, currentInstruction, bAfter);
#endif
            return 10;

        case ADD_A_B: add_a_r(*B); goto add_a_r;
        case ADD_A_C: add_a_r(*C); goto add_a_r;
        case ADD_A_D: add_a_r(*D); goto add_a_r;
        case ADD_A_E: add_a_r(*E); goto add_a_r;
        case ADD_A_H: add_a_r(*H); goto add_a_r;
        case ADD_A_L: add_a_r(*L); goto add_a_r;
        case ADD_A_A: add_a_r(*A); goto add_a_r;
        add_a_r:
#ifdef DEBUG
            sprintf(currentInstruction, "%-*s ADD A,%s", align, currentInstruction, rName(opcode & 0x07));
#endif
            return 4;


        default:
            printf("Unknow opcode 0x%02X\n", opcode);
            exit(1);
    }
}

/**
 * adds the register r to A
 */
void Cpu::add_a_r(byte r)
{
    bPrev = *A;
    // @TODO: flags
    bAfter = ((*A + r) & 0xFF);
}

/**
 * gets the content of the register r
 */
byte Cpu::r(byte rCode)
{
    switch (rCode) {
        case 0b000: return *B;
        case 0b001: return *C;
        case 0b010: return *D;
        case 0b011: return *E;
        case 0b100: return *H;
        case 0b101: return *L;
        case 0b110: return readByte(*HL);
        case 0b111: return *A;
        default: return 0;
    }
}

/**
 * gets the name of the register of 8 bits r
 */
char* Cpu::rName(byte rCode) {
    switch (rCode) {
        case 0b000: return "B";
        case 0b001: return "C";
        case 0b010: return "D";
        case 0b011: return "E";
        case 0b100: return "H";
        case 0b101: return "L";
        case 0b110: return "(HL)";
        case 0b111: return "A";
        default: return "?";
    }
}

/**
 * gets the name of the register of 16 bits rr
 */
char* Cpu::rrName(byte rCode) {
    switch (rCode) {
        case 0b000: return "BC";
        case 0b001: return "DE";
        case 0b010: return "HL";
        case 0b011: return "SP";
        default: return "?";
    }
}

/**
 * set flags F according to the status of some flags passed
 */
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


int Cpu::jp_nn()
{
    PC = readWord(PC);
#ifdef DEBUG
    sprintf(currentInstruction, "%-*s JP 0x%04X", align, currentInstruction, PC);
#endif
    return 10;
}


