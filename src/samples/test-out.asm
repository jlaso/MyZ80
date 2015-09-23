; testing the multiplication
;------------------------------------

.org 1000
jp start

#define ledport $03

#include "functions.inc"

start:

   ld sp,$ff00
   ld a,0

loop:

   ;out (ledport),A
   out ($03),A
   call delay1
   inc a

   jp loop

   halt


version:
.db "V1.0",0