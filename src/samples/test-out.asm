; testing the multiplication
;------------------------------------

.org 1000
jp start

#define ledport $03

#include "functions.inc"

start:

   ld hl,$f000

loop:

   ld a,h
   ;out (ledport),A
   out ($03),A

   call delay1
   inc h

   jp loop

   halt


version:
.db "V1.0",0