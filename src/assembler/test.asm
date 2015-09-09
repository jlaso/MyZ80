; this a test program to evaluate the assembler

.org 0

nop

label1:
ld bc,ffffh

inc a
inc b
inc c
inc d
inc e
inc h
inc l


ld a,10h
ld b,1eh
ld c,feh
ld e,ffh
ld d,a0h
ld h,0fh
ld l,f0h

add a,a
add a,b
add a,c
add a,d
add a,e
add a,h
add a,l
add a,(hl)

ld c,10
ld b,10


wait:
nop
nop
djnz wait

halt
