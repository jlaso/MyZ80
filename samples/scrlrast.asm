;; Scrolling single-line split raster
;;
;; This code is released under the Gnu Public License v1.
;; 
;; Code by Kev Thacker

org &4000
nolist

;; turn off firmware interrupts
;; install a null interrupt handler

di
ld hl,&c9fb		;; EI:RET
ld (&38),hl		;; Interrupt mode 1 interrupt routine location

;; turn all colours black
ld bc,&7f10
ld a,&54

.turn_black
out (c),c
out (c),a
dec c
jp p,turn_black

ei

.loop 
;; wait for vsync start
ld b,&f5
.l1 in a,(c)
rra 
jr nc,l1

;; wait some time so we are in a visible part of the screen (top-border)

halt
halt
defs 20
call nice_raster
jp loop

.nice_raster
jr nr1
.nr1 defs 6               ;length of a raster section
.nr2 ld hl,raster
ld b,&7f
ld c,16
out (c),c                 ;border colour!!

;; this code sets the colours quickly
outi
inc b
outi
inc b
outi
inc b
outi
inc b
outi
inc b
outi
inc b
outi
inc b
outi
inc b
outi
inc b
outi
inc b
outi
inc b

;; set the colour to black for the rest
;; of the display
ld c,%01000000+20
out (c),c

;; scroll through the raster colours
ld a,(nice_raster+1)
inc a
ld (nice_raster+1),a
cp 6
ret nz
xor a
ld (nice_raster+1),a
.add ld de,0
inc de
ld a,e
and 31
ld e,a
ld (add+1),de
ld hl,raster
add hl,de
ld (nr2+1),hl
ret

;; table of colours
;; (nice blues)
.raster
defb %01000000+4
defb %01000000+21
defb %01000000+31
defb %01000000+27
defb %01000000+11
defb %01000000+27
defb %01000000+31
defb %01000000+21
defb %01000000+4
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+20
defb %01000000+4
defb %01000000+21
defb %01000000+31
defb %01000000+27
defb %01000000+11
defb %01000000+27
defb %01000000+31
defb %01000000+21
defb %01000000+4
defb %01000000+20
defb %01000000+20
end