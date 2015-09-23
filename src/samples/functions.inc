; These are routines created in order to document the Z80 assembler
; @author Joseluis Laso
; 20.09.2015

; retard function
; isn't a millisecond,
; just for testing purposes
;=======================
delay1:

    push af
    ;push bc
    ;push de

    ld bc, $1000

delay1_outer:

    ld de, $1000

delay1_inner:

    dec de
    ld a,d
    or e
    jp nz delay1_inner

    dec bc
    ld a,b
    or c

    jp nz delay1_outer

    ;pop de
    ;pop bc
    pop af

    ret
;=======================
; end of retard function


; mult function                   see http://sgate.emt.bme.hu/patai/publications/z80guide/part4.html
; @param D
; @param E
; @return HL   D * E -> HL
;=======================
Multiply:                        ; this routine performs the operation HL=D*E
  ld hl,0                        ; HL is used to accumulate the result
  ld a,d                         ; checking one of the factors; returning if it is zero
  or a
  ret z
  ld b,d                         ; one factor is in B
  ld d,h                         ; clearing D (H is zero), so DE holds the other factor
MulLoop:                         ; adding DE to HL exactly B times
  add hl,de
  djnz MulLoop
  ret
;=======================
; end of mult function