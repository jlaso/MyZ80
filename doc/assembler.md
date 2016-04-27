Some of the definitions are taken from http://www.nongnu.org/z80asm/directives.html

NUMBERS
=======

All numeric terms can be expressed in this formats:

|example| base |
|-------|------|
| 1234 | decimal |
| %0101 | binary |
| $0F | hexadecimal |
|0Fh | hexadecimal |
|071o | octal |


COMMENTS
========
To put some comment inline you need only put a semicolon and write the comment

Examples:

; PROGRAM TEST-1
; @author dev1
;~~~~~~~~~~~~~~~~

ld a,10  ; load on accumulator the length of the text


DIRECTIVES
==========


|directive|explanation|examples|
|---------|-----------|--------|
| .db | define byte data, bytes separated by commas and string enclosed in double quotes ("), terms can be expressions |.db "Word",$1a,constant+2|
|.dw|define data by words (lo byte is stored first)|.dw $0123,65535|
|.org|define the start of the address for compilation|.org 0|
|#include|include the content of another source file, like if the text was in the current archive|#include "functions.asm"|
|#define|define a constant|#define Radius 10|


.dw and .db waits to have a list of operands separated by commas
.org waits to have a numeric expression as a operand


LABELS
======

The labels have to in a single line, and are resolved in one of the three passes of the assembler. 
Syntax is:

\s*\w+:\s*[;.*]?

Examples:

loop1:
call something
djnz loop1
    
    
    Title:   ;Here is declared the title for the report
.db "Report",0


