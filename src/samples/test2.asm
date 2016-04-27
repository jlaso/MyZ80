#include    "ti83plus.inc"
#include    "dcs.inc"
#define     progStart   $1095
.org        progStart-2
.db         $BB,$6D
Init:
    xor D
	ret
	jr  Start
.dw         Description
.db         $07,$00
.dw         Icon
.dw         $0000
Start:
    call    OpenGUIStack
    ld  HL, Window
    ld  DE, Button-Window
    ld  A,  GUIRSmallWin
    call    PushGUIStack
    ld  HL, Button
    ld  DE, Description-Button
    ld  A,  GUIRWinButtons
    call    PushGUIStack
    ld  HL, 0
    call    GUIMouse
    ret
End:
    call    ResetAppPage
    ret
Window:
.db         $00,$00 ; Window position
.db         $F8,$88,$88,$88,$F8 ; Window icon
.db         "Hello world!",0 ; Window title
Button:
.db         %00100000
.dw         0
.dw         0
.dw         End
Description:
.db         "HELLOWORLD",0
Icon:
.db         %11111111,%11111111
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %10000000,%00000001
.db         %11111111,%11111111