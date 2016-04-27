#include <iostream>
#include "Memory.h"
#include "general.h"
#include "Cpu.h"

using namespace std;

int main()
{
    clock_t begin = clock();

    Cpu cpu(2500000);

    Memory memory;
    memory.readFromFile("/Users/joseluislaso/projects/myZ80/src/samples/test.bin");

    cpu.attachMemory(memory);

    printf("\n\n");    for (int addr = 0; addr < 10; addr++) printf("0x%02X > 0x%02X .\n", addr, memory.read(addr));

    cpu.run(0);

    clock_t end = clock();
    double elapsed_secs = double(end - begin) / CLOCKS_PER_SEC;

    printf("Finished in %f  ->  %d  ->  %f", elapsed_secs, CLOCKS_PER_SEC, double(end - begin));
    return 0;
}

