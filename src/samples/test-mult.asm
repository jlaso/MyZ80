; testing the multiplication
;------------------------------------

.org 1000
jp start

#include "functions.inc"

start:

   ld d,10
   ld e,10
   call multiply
   ; checks that the result is D * E and is stored in HL
   halt


version:
.db "V1.0",0