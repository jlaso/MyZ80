; this a test program to evaluate the assembler

.org 0

label1:
ld a,10
add a,a

ld c,10
wait:
nop
nop
djnz wait
