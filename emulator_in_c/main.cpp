#include <iostream>
#include "Memory.h"
#include "general.h"
#include "Cpu.h"

using namespace std;

int main()
{

    struct rec16 {
        byte hi;
        byte lo;
    };

    union record16 {
        rec16 r;
        word rr;
    };

    record16 AF, BC, DE, HL;

    byte* A = &AF.r.hi;

    AF.rr = 0x1234;
    BC.rr = 0x5678;

    printf("0x%02X 0x%02X AF: 0x%04X BC: 0x%04X\n", *A, AF.r.lo, AF, BC);
    *A = 0;
    BC.r.hi = 0;
    printf("0x%02X 0x%02X AF: 0x%04X BC: 0x%04X\n", AF.r.hi, AF.r.lo, AF, BC);

    exit(0);

    Cpu cpu;

    Memory memory;
    memory.readFromFile("/Users/joseluislaso/projects/myZ80/src/samples/test.bin");

    cpu.attachMemory(memory);

//    printf("\n\n");
//
    for (int addr = 0; addr < 6; addr++) {
    //    printf("0x%02X > 0x%02X .\n", addr, memory.read(addr));
    }

    cpu.run(0);

    printf("Total cycles consumed are %ld", cpu.getCycles());

    return 0;
}

