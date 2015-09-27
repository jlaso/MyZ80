#include <iostream>
#include "Memory.h"
#include "general.h"
#include "Cpu.h"

using namespace std;

int main()
{

    Cpu cpu;

    Memory memory;
    memory.readFromFile("/Users/joseluislaso/projects/myZ80/src/samples/test.bin");

    cpu.attachMemory(memory);

    printf("\n\n");

    for (int addr = 0; addr < 10; addr++) {
        printf("0x%02X > 0x%02X .\n", addr, memory.read(addr));
    }

    cpu.run(0);

    printf("Total cycles consumed are %ld", cpu.getCycles());

    return 0;
}

