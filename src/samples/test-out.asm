; testing the multiplication
;------------------------------------

.org 1000
jp start

#define ledport $03

#include "functions.inc"

start:

   ld a,10
   ;out (ledport),A
   out ($03),A
   halt


version:
.db "V1.0",0