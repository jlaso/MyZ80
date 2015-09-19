; retard function
;=======================
retard1ms:

ld b, $ff

_retard1ms:
    nop
    nop
    nop
    djnz _retard1ms
    ret
;=======================
; end of retard function