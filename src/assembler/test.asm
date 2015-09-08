; this a test program to evaluate the assembler

.org 0

label1:
ld bc,ffffh
inc a
ld a,10
add a,a

ld c,10
ld b,10
wait:
nop
nop
djnz wait

halt
