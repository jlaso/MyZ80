; simple z80 assembler demonstration
;------------------------------------

.org 1000
jp start

#include "functions.inc"

start:

   ld hl,version
   call retard1ms
   halt


version:
.db "V1.0",0