/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myz80;

import jEditSyntax.JEditTextArea;
import jEditSyntax.SyntaxStyle;
import jEditSyntax.SyntaxUtilities;
import jEditSyntax.Token;
import jEditSyntax.marker.ASMZ80TokenMarker;
import java.awt.Color;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.UIManager;

/**
 *
 * @author joseluislaso
 */
public class MyZ80 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //new TextEditor();   
        
        UIManager.put("EditTextAreaUI", "jEditSyntax.EditTextAreaUI");

        JFrame fm = new JFrame();
        
        JEditTextArea jta = new JEditTextArea(20,100, fm); 
        jta.setTokenMarker(new ASMZ80TokenMarker());
        jta.readInFile("samples/normals.asm");
        /*
        jta.setText(";; A byte-by-byte software scroll.\n" +
";;\n" +
";; Rate dependant on mode:\n" +
";; Mode 0: 2 pixels at a time\n" +
";; Mode 1: 4 pixels at a time\n" +
";; Mode 2: 8 pixels at a time\n" +
";;\n" +
";; Coded in 1992 by Kevin Thacker\n" +
";;\n" +
";; Note uses \"I\" register so will not work with Interrupt mode 2.\n" +
";; Font is mode 1, 2 bytes wide and 16 lines tall\n" +
"\n" +
"org &2000\n" +
"nolist\n" +
"\n" +
".scr_set_ink equ &bc32\n" +
".scr_set_border equ &bc38\n" +
".scr_set_mode equ &bc0e\n" +
".kl_u_rom_disable equ &b903\n" +
"\n" +
".no_of_characters_in_font equ 60\n" +
".width_of_scroll          equ 80  ;in bytes\n" +
".character_height         equ 16  ;in bytes\n" +
".character_width          equ 2   ;in bytes\n" +
".screen_address           equ &c000 ; top-left screen address of scroll\n" +
"\n" +
"let next_line_add=&800-width_of_scroll+1\n" +
"\n" +
";; mode 1\n" +
"ld a,1\n" +
"call scr_set_mode\n" +
"\n" +
";; set border\n" +
"ld bc,0\n" +
"call scr_set_border\n" +
"\n" +
";; set pen 0\n" +
"xor a\n" +
"ld bc,0\n" +
"call scr_set_ink\n" +
"\n" +
";; set pen 1\n" +
"ld a,1\n" +
"ld bc,&1a1a\n" +
"call scr_set_ink\n" +
"\n" +
";; set pen 2\n" +
"ld a,2\n" +
"ld bc,&1414\n" +
"call scr_set_ink\n" +
"\n" +
";; set pen 3\n" +
"ld a,3\n" +
"ld bc,&0202\n" +
"call scr_set_ink\n" +
"\n" +
";; **** INITIALISE SCROLLER ****\n" +
"call make_font_table\n" +
"\n" +
"call scroll_init\n" +
"\n" +
";; turn off upper rom because we will be reading from screen memory\n" +
";; to perform the scroll\n" +
"\n" +
"call kl_u_rom_disable\n" +
"\n" +
".loop\n" +
"ld b,&f5\n" +
"in a,(c)\n" +
"rra\n" +
"jr nc,loop\n" +
"halt\n" +
"halt\n" +
"halt\n" +
"call normal_scroll\n" +
"jp loop\n" +
"\n" +
".normal_scroll\n" +
";; copy screen data left by one byte\n" +
"\n" +
"ld hl,screen_address+1\n" +
"ld a,character_height\n" +
"\n" +
";; loop uses I register, so will not work with IM 2 without modification\n" +
";;\n" +
".scroll_loop \n" +
"ld i,a\n" +
"ld e,l\n" +
"ld d,h\n" +
"dec de\n" +
";; lots of ldi to move the scroll 1 byte at a time\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"ldi\n" +
"                       ;no of bytes to scroll-1\n" +
"call scr_next_line\n" +
"ld a,i\n" +
"dec a\n" +
"jp nz,scroll_loop\n" +
"\n" +
"ld hl,screen_address+width_of_scroll-1\n" +
"ld a,character_height\n" +
"ld de,(current_character_data)\n" +
"\n" +
";; this copies one column of a character from the font\n" +
";; to the screen\n" +
";;\n" +
";; the font character is stored left-to-right, top-to-bottom\n" +
".column_loop\n" +
"ld i,a\n" +
"ld a,(de)\n" +
"ld (hl),a\n" +
"ld bc,character_width\n" +
"ex de,hl\n" +
"add hl,bc\n" +
"ex de,hl\n" +
"call scr_next_line2\n" +
"ld a,i\n" +
"dec a\n" +
"jr nz,column_loop\n" +
"\n" +
";; this effectively increments to the next column of the current char\n" +
"ld hl,(current_character_data)\n" +
"inc hl\n" +
"ld (current_character_data),hl\n" +
"\n" +
";; this checks if we have processed all columns from the current char\n" +
"ld hl,current_character_column\n" +
"inc (hl)\n" +
"ld a,(hl)\n" +
"cp character_width\n" +
"ret nz\n" +
"\n" +
"xor a\n" +
"ld (hl),a\n" +
"ld hl,(scroll_pointer)\n" +
"inc hl\n" +
"ld (scroll_pointer),hl\n" +
"ld a,(hl)\n" +
"or a\n" +
"jr nz,continue_scroller\n" +
"\n" +
".scroll_init\n" +
"ld hl,scroll_text\n" +
"ld (scroll_pointer),hl\n" +
"\n" +
".continue_scroller ld a,(hl)\n" +
"call get_next_character\n" +
"ld (current_character_data),de\n" +
"ret\n" +
"\n" +
".scr_next_line\n" +
"ld bc,next_line_add\n" +
"add hl,bc\n" +
"ret nc\n" +
"ld bc,&c050\n" +
"add hl,bc\n" +
"ret\n" +
"\n" +
".scr_next_line2\n" +
"ld bc,&800\n" +
"add hl,bc\n" +
"ret nc\n" +
"ld bc,&c050\n" +
"add hl,bc\n" +
"ret\n" +
"\n" +
".get_next_character\n" +
"sub \" \"\n" +
"add a,a\n" +
"ld e,a\n" +
"ld d,0\n" +
"ld hl,font_table\n" +
"add hl,de\n" +
"ld e,(hl)\n" +
"inc hl\n" +
"ld d,(hl)\n" +
"ret\n" +
"\n" +
";; generate a table, giving the start address of each char's data in the font\n" +
";; making it a bit quicker to find chars in the font\n" +
".make_font_table\n" +
"ld a,no_of_characters_in_font\n" +
"ld ix,font_table\n" +
"ld hl,font_address\n" +
"\n" +
".make_loop\n" +
"ld (ix+0),l\n" +
"ld (ix+1),h\n" +
"inc ix\n" +
"inc ix\n" +
"ld bc,character_height*character_width\n" +
"add hl,bc\n" +
"dec a\n" +
"jr nz,make_loop\n" +
"ret\n" +
"\n" +
"\n" +
".scroll_text\n" +
"defb \"WOW ISN'T THIS A COOL SCROLLER ...................   CODED BY KEV\"\n" +
"defb \" ON THE 1ST OF MARCH IN 1992                    ONE OF THE BEST\"\n" +
"defb \" CODINGS YET?????!!!              SCROLLER WRAPS NOW             \"\n" +
"defb 0\n" +
"\n" +
".scroll_pointer defw 0\n" +
".current_character_column defb 0\n" +
".current_character_data defw 0\n" +
"\n" +
".font_table defs no_of_characters_in_font*2\n" +
"\n" +
";; font is ripped from lemmings on atari st\n" +
".font_address\n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &23,&00,&12,&00,&12,&00,&12,&00,&12,&00,&12,&00,&32,&00,&23,&00 \n" +
"defb &33,&00,&00,&00,&03,&00,&12,&00,&12,&00,&32,&00,&33,&00,&00,&00 \n" +
"defb &02,&04,&64,&c8,&24,&0c,&fb,&c4,&9d,&08,&44,&88,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&46,&0c,&46,&8c,&8f,&0e,&3c,&c2,&fd,&ae,&46,&8c,&46,&c8 \n" +
"defb &46,&8c,&78,&6a,&2d,&6a,&df,&ae,&46,&8c,&46,&cc,&00,&00,&00,&00 \n" +
"defb &10,&00,&10,&00,&12,&0c,&34,&c2,&db,&cc,&ac,&00,&07,&00,&23,&08 \n" +
"defb &11,&c4,&00,&6a,&bf,&6a,&78,&0e,&ef,&cc,&10,&00,&01,&00,&00,&00 \n" +
"defb &cc,&02,&48,&24,&48,&06,&8c,&6a,&cc,&0c,&11,&84,&01,&08,&23,&88 \n" +
"defb &03,&00,&75,&00,&06,&06,&ae,&60,&8c,&24,&cc,&46,&88,&66,&00,&00 \n" +
"defb &07,&00,&5a,&00,&1e,&08,&1c,&08,&1c,&88,&d8,&00,&af,&00,&64,&00 \n" +
"defb &df,&00,&0b,&00,&2b,&88,&1d,&c6,&bf,&4a,&8e,&8e,&67,&66,&00,&00 \n" +
"defb &01,&00,&10,&08,&32,&88,&03,&00,&33,&00,&22,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&08,&01,&84,&10,&08,&10,&08,&32,&88,&32,&00,&23,&00,&23,&00 \n" +
"defb &32,&00,&32,&00,&23,&88,&01,&88,&01,&08,&11,&cc,&00,&88,&00,&00 \n" +
"defb &02,&00,&47,&00,&30,&00,&12,&00,&32,&88,&01,&08,&01,&08,&01,&08 \n" +
"defb &01,&88,&10,&08,&23,&88,&23,&00,&03,&00,&77,&00,&22,&00,&00,&00 \n" +
"defb &00,&00,&23,&00,&ab,&04,&af,&8c,&73,&80,&47,&88,&3c,&0c,&69,&e2 \n" +
"defb &cf,&4c,&17,&88,&63,&08,&bf,&cc,&ab,&44,&33,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&11,&00,&10,&00,&10,&00,&10,&00,&27,&0c,&70,&48 \n" +
"defb &67,&cc,&01,&00,&10,&00,&01,&00,&11,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&01,&00,&12,&00,&03,&00,&75,&00,&46,&00,&22,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&47,&4c,&07,&c0 \n" +
"defb &77,&cc,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&02,&00,&07,&00,&65,&00,&57,&00,&22,&00,&00,&00 \n" +
"defb &00,&02,&00,&24,&00,&06,&00,&6a,&00,&84,&11,&84,&01,&08,&32,&88 \n" +
"defb &03,&00,&75,&00,&06,&00,&ae,&00,&4c,&00,&cc,&00,&88,&00,&00,&00 \n" +
"defb &03,&08,&34,&84,&16,&c0,&6b,&4a,&0c,&06,&0c,&64,&0c,&64,&4c,&46 \n" +
"defb &4c,&46,&0c,&46,&c8,&46,&db,&ae,&25,&4c,&47,&cc,&33,&88,&00,&00 \n" +
"defb &03,&88,&56,&80,&56,&80,&32,&80,&01,&08,&01,&80,&01,&80,&11,&08 \n" +
"defb &01,&80,&01,&80,&01,&80,&01,&80,&11,&08,&11,&08,&11,&88,&00,&00 \n" +
"defb &07,&08,&78,&84,&f8,&c2,&67,&4a,&00,&6a,&11,&48,&23,&0c,&47,&80 \n" +
"defb &17,&00,&ca,&00,&ae,&00,&6b,&ae,&e9,&4a,&8f,&2e,&ff,&ee,&00,&00 \n" +
"defb &47,&0c,&f8,&c2,&e1,&4a,&4b,&4a,&0c,&42,&00,&06,&00,&4a,&11,&0c \n" +
"defb &00,&ea,&00,&26,&8c,&06,&6b,&8e,&8f,&2e,&cf,&ee,&77,&cc,&00,&00 \n" +
"defb &00,&0c,&00,&c0,&01,&c0,&10,&48,&12,&c8,&31,&c8,&46,&c8,&26,&8c \n" +
"defb &8c,&ea,&5f,&ca,&0f,&6a,&ff,&ea,&00,&c8,&00,&8c,&00,&44,&00,&00 \n" +
"defb &07,&0c,&5a,&c2,&db,&8c,&48,&00,&48,&00,&cb,&08,&9e,&48,&77,&4a \n" +
"defb &00,&ea,&00,&26,&cc,&06,&2f,&8e,&cb,&2e,&ff,&ee,&77,&cc,&00,&00 \n" +
"defb &03,&0c,&34,&c0,&cb,&cc,&0c,&00,&84,&00,&2f,&08,&3c,&c0,&ff,&ca \n" +
"defb &0c,&06,&4c,&64,&0c,&64,&0c,&06,&cb,&0e,&8f,&2e,&77,&cc,&00,&00 \n" +
"defb &0f,&0e,&f0,&e0,&0f,&4a,&ff,&4a,&00,&48,&00,&84,&01,&80,&11,&08 \n" +
"defb &01,&08,&23,&08,&23,&08,&33,&88,&23,&08,&23,&88,&11,&00,&00,&00 \n" +
"defb &07,&0c,&52,&c0,&fb,&ca,&c8,&42,&c8,&06,&c8,&06,&07,&48,&9e,&6a \n" +
"defb &3f,&e8,&4c,&24,&0c,&64,&4c,&06,&8f,&0e,&8f,&2e,&77,&cc,&00,&00 \n" +
"defb &07,&0c,&da,&e2,&cb,&ca,&48,&24,&84,&24,&84,&46,&0c,&64,&eb,&2c \n" +
"defb &07,&0e,&77,&8e,&00,&26,&11,&ae,&23,&4c,&57,&cc,&33,&88,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&01,&00,&03,&08,&32,&08,&23,&88,&11,&00 \n" +
"defb &00,&00,&01,&00,&32,&00,&03,&00,&75,&00,&46,&00,&02,&00,&00,&00 \n" +
"defb &00,&88,&00,&84,&01,&84,&10,&80,&12,&08,&21,&00,&65,&00,&46,&00 \n" +
"defb &57,&00,&12,&00,&23,&88,&01,&08,&11,&84,&00,&4c,&00,&88,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&07,&0c,&16,&c0,&77,&cc,&00,&00,&00,&00 \n" +
"defb &07,&0c,&65,&c0,&77,&cc,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &04,&00,&42,&00,&60,&00,&65,&00,&21,&00,&32,&08,&01,&08,&11,&4c \n" +
"defb &01,&88,&32,&88,&03,&00,&57,&00,&06,&00,&66,&00,&44,&00,&00,&00 \n" +
"defb &03,&0c,&16,&c2,&78,&4a,&fb,&68,&44,&42,&00,&2e,&11,&0c,&01,&08 \n" +
"defb &01,&88,&11,&00,&00,&00,&01,&00,&10,&08,&11,&08,&11,&00,&00,&00 \n" +
"defb &30,&08,&07,&84,&cc,&40,&09,&6a,&b1,&8e,&0a,&66,&4e,&46,&c6,&66 \n" +
"defb &4e,&66,&0a,&46,&2b,&6a,&19,&6e,&c8,&00,&26,&22,&03,&0c,&00,&00 \n" +
"defb &03,&08,&74,&84,&34,&48,&cb,&4a,&0c,&24,&8c,&64,&c8,&46,&db,&2c \n" +
"defb &f8,&e0,&af,&2c,&bf,&ce,&cc,&46,&cc,&46,&cc,&46,&88,&22,&00,&00 \n" +
"defb &8f,&4c,&78,&48,&1f,&6a,&48,&8e,&0c,&06,&2f,&2e,&1e,&84,&8f,&48 \n" +
"defb &bf,&8e,&cc,&06,&4c,&06,&df,&0e,&bc,&4e,&8f,&2e,&ff,&cc,&00,&00 \n" +
"defb &13,&4c,&34,&e2,&f8,&e0,&db,&ac,&ae,&46,&0c,&00,&48,&00,&8c,&00 \n" +
"defb &0c,&00,&0c,&00,&2e,&46,&9f,&2c,&8f,&c2,&67,&2e,&33,&cc,&00,&00 \n" +
"defb &8f,&08,&78,&84,&f0,&6a,&f3,&ca,&84,&24,&48,&64,&48,&64,&8c,&46 \n" +
"defb &0c,&46,&0c,&64,&0c,&46,&af,&ae,&8f,&2e,&9f,&cc,&ff,&88,&00,&00 \n" +
"defb &9f,&0e,&5a,&e0,&2d,&0e,&7b,&ee,&0c,&00,&3f,&00,&3c,&08,&df,&08 \n" +
"defb &7f,&00,&4c,&00,&4c,&00,&6f,&8e,&8f,&6a,&bf,&ae,&ff,&ee,&00,&00 \n" +
"defb &af,&0e,&5a,&e0,&69,&4a,&7b,&ee,&0c,&00,&48,&00,&4b,&08,&8f,&80 \n" +
"defb &8f,&88,&bf,&88,&0c,&00,&8c,&00,&8c,&00,&8c,&00,&cc,&00,&00,&00 \n" +
"defb &03,&2e,&34,&c2,&f8,&2e,&fb,&ee,&2e,&00,&8c,&00,&c8,&ee,&8c,&e8 \n" +
"defb &0c,&ac,&0c,&ec,&2e,&46,&9f,&ce,&8f,&0e,&47,&6e,&33,&ee,&00,&00 \n" +
"defb &8c,&46,&48,&24,&48,&24,&48,&24,&48,&06,&48,&24,&4b,&ac,&8f,&2c \n" +
"defb &0f,&2c,&3f,&8e,&0c,&24,&0c,&06,&8c,&06,&8c,&46,&cc,&66,&00,&00 \n" +
"defb &57,&0c,&25,&c0,&16,&0c,&47,&4c,&23,&88,&23,&08,&23,&88,&23,&88 \n" +
"defb &23,&88,&23,&88,&23,&88,&67,&4c,&07,&0c,&67,&0c,&77,&cc,&00,&00 \n" +
"defb &00,&04,&00,&ea,&00,&ca,&00,&ae,&00,&ae,&00,&ae,&00,&ae,&88,&ae \n" +
"defb &84,&ae,&0c,&ae,&48,&ae,&db,&8c,&8f,&4c,&47,&cc,&33,&88,&00,&00 \n" +
"defb &cc,&04,&8c,&c8,&48,&84,&59,&c4,&7a,&08,&4b,&88,&69,&00,&cb,&00 \n" +
"defb &eb,&88,&9d,&08,&59,&4c,&8c,&8c,&8c,&ae,&cc,&66,&cc,&44,&00,&00 \n" +
"defb &04,&00,&8e,&00,&ca,&00,&ea,&00,&ea,&00,&8e,&00,&ae,&00,&ae,&00 \n" +
"defb &ae,&00,&ae,&00,&ae,&00,&8f,&8c,&8f,&6a,&cf,&2e,&77,&cc,&00,&00 \n" +
"defb &8c,&46,&c0,&60,&c2,&e8,&79,&e0,&87,&2c,&87,&6c,&2f,&ec,&0d,&64 \n" +
"defb &0c,&64,&4c,&64,&4c,&46,&4c,&46,&cc,&46,&cc,&46,&88,&22,&00,&00 \n" +
"defb &ff,&88,&5a,&84,&2d,&c0,&7b,&4a,&0c,&ac,&48,&24,&48,&24,&0c,&64 \n" +
"defb &0c,&64,&0c,&46,&0c,&46,&0c,&46,&0c,&46,&8c,&46,&cc,&66,&00,&00 \n" +
"defb &23,&08,&16,&84,&f8,&4a,&fb,&e8,&8c,&24,&0c,&64,&0c,&46,&8c,&46 \n" +
"defb &8c,&46,&8c,&64,&0c,&46,&bf,&ae,&8f,&2e,&47,&4c,&33,&88,&00,&00 \n" +
"defb &ff,&88,&5a,&84,&a5,&6a,&7b,&ca,&84,&24,&48,&64,&48,&46,&c8,&46 \n" +
"defb &7b,&8e,&2d,&2e,&0f,&0c,&3f,&88,&8c,&00,&8c,&00,&cc,&00,&00,&00 \n" +
"defb &23,&08,&74,&84,&f8,&6a,&fb,&ca,&0c,&24,&4c,&64,&4c,&64,&4c,&64 \n" +
"defb &4c,&46,&0c,&66,&bf,&ee,&8f,&2c,&47,&2c,&33,&ce,&00,&46,&00,&00 \n" +
"defb &cf,&08,&5a,&84,&69,&6a,&7b,&ca,&0c,&06,&48,&24,&48,&24,&9f,&4a \n" +
"defb &8f,&6a,&0f,&84,&7b,&0c,&48,&48,&c8,&8e,&8c,&46,&cc,&66,&00,&00 \n" +
"defb &07,&4c,&5a,&c2,&2d,&0e,&7b,&cc,&0c,&00,&cb,&88,&34,&84,&47,&48 \n" +
"defb &33,&e8,&00,&24,&00,&24,&ff,&ac,&8f,&0e,&8f,&2e,&ff,&cc,&00,&00 \n" +
"defb &bf,&ae,&5a,&e0,&0f,&4a,&fe,&6e,&23,&88,&23,&08,&33,&88,&23,&88 \n" +
"defb &23,&88,&23,&88,&23,&88,&33,&88,&23,&88,&23,&88,&11,&00,&00,&00 \n" +
"defb &88,&44,&0c,&06,&0c,&06,&0c,&06,&4c,&26,&0c,&06,&0c,&06,&cc,&66 \n" +
"defb &cc,&26,&cc,&26,&8c,&ae,&bf,&8c,&ed,&4c,&47,&cc,&33,&88,&00,&00 \n" +
"defb &88,&44,&0c,&42,&84,&06,&0c,&06,&c4,&62,&84,&06,&84,&06,&4c,&26 \n" +
"defb &0c,&26,&0c,&ae,&ea,&0c,&9f,&6e,&47,&4c,&67,&88,&33,&00,&00,&00 \n" +
"defb &88,&02,&84,&64,&84,&64,&0c,&64,&c4,&64,&0c,&64,&1d,&46,&dd,&46 \n" +
"defb &7e,&ce,&4f,&c6,&1f,&86,&1f,&0e,&2e,&0e,&ae,&ae,&cc,&66,&00,&00 \n" +
"defb &08,&02,&0c,&24,&48,&06,&ca,&6a,&60,&84,&65,&c4,&12,&80,&23,&08 \n" +
"defb &03,&08,&57,&4c,&06,&48,&ae,&ea,&8c,&06,&cc,&66,&88,&22,&00,&00 \n" +
"defb &88,&02,&0c,&64,&0c,&64,&0c,&06,&ae,&ea,&06,&0c,&47,&4c,&23,&08 \n" +
"defb &23,&88,&23,&88,&23,&88,&23,&88,&23,&88,&23,&88,&11,&00,&00,&00 \n" +
"defb &ff,&4c,&d2,&e2,&bf,&6a,&ff,&ea,&00,&8c,&11,&cc,&01,&08,&23,&88 \n" +
"defb &03,&00,&57,&00,&06,&00,&bf,&ee,&8f,&e2,&df,&2e,&ff,&ee,&00,&00 \n" +
"defb &01,&08,&03,&08,&12,&00,&32,&00,&12,&00,&23,&00,&23,&00,&23,&00 \n" +
"defb &23,&00,&23,&00,&33,&00,&23,&00,&23,&00,&33,&88,&11,&88,&00,&00 \n" +
"defb &08,&00,&84,&00,&0c,&00,&6a,&00,&06,&00,&65,&00,&03,&00,&23,&88 \n" +
"defb &11,&08,&11,&c4,&00,&0c,&00,&ae,&00,&46,&00,&46,&00,&22,&00,&00 \n" +
"defb &03,&00,&23,&08,&01,&08,&01,&08,&01,&08,&01,&08,&01,&88,&01,&08 \n" +
"defb &01,&88,&01,&08,&01,&88,&01,&88,&01,&08,&23,&88,&23,&00,&00,&00 \n" +
"defb &01,&00,&32,&00,&12,&08,&25,&80,&37,&c4,&26,&4c,&44,&88,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&0f,&0e,&2d,&e0,&ff,&ce,&00,&00 \n" +
"defb &01,&00,&01,&08,&32,&88,&03,&00,&13,&00,&22,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&03,&0e,&16,&e0,&cb,&ec,&48,&64 \n" +
"defb &4c,&64,&4c,&64,&4c,&46,&cc,&46,&af,&2c,&07,&86,&33,&ee,&00,&00 \n" +
"defb &08,&00,&84,&00,&84,&00,&0c,&00,&c7,&08,&4b,&c0,&0f,&4a,&cc,&06 \n" +
"defb &0c,&06,&4c,&46,&0c,&46,&4c,&26,&5f,&ae,&0f,&4c,&ff,&88,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&03,&0c,&16,&c2,&db,&ce,&48,&46 \n" +
"defb &0c,&00,&0c,&00,&0c,&00,&48,&66,&cb,&0e,&07,&2e,&33,&cc,&00,&00 \n" +
"defb &00,&02,&00,&24,&00,&24,&00,&24,&13,&2c,&34,&68,&af,&ec,&8c,&46 \n" +
"defb &0c,&46,&0c,&64,&0c,&46,&c8,&46,&8f,&4e,&65,&2e,&33,&ee,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&23,&0e,&25,&e0,&07,&ee,&06,&00 \n" +
"defb &cb,&08,&e9,&08,&9f,&88,&06,&00,&57,&4e,&47,&2e,&33,&ee,&00,&00 \n" +
"defb &01,&0e,&12,&68,&03,&ce,&13,&00,&24,&00,&65,&0c,&56,&84,&57,&cc \n" +
"defb &64,&00,&64,&00,&64,&00,&46,&00,&64,&00,&64,&00,&66,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&03,&0e,&16,&c2,&cb,&ee,&48,&00 \n" +
"defb &0c,&06,&0c,&ac,&0c,&64,&8c,&20,&af,&0e,&47,&0e,&33,&ee,&00,&00 \n" +
"defb &08,&00,&0c,&00,&0c,&00,&0c,&00,&4f,&08,&4b,&84,&3f,&48,&4c,&c8 \n" +
"defb &0c,&26,&4c,&26,&0c,&06,&4c,&26,&4c,&66,&0c,&06,&88,&44,&00,&00 \n" +
"defb &12,&00,&32,&08,&23,&00,&00,&00,&03,&00,&56,&08,&23,&08,&01,&08 \n" +
"defb &01,&88,&01,&08,&01,&08,&11,&88,&23,&4c,&47,&4c,&33,&cc,&00,&00 \n" +
"defb &03,&00,&12,&08,&33,&00,&00,&00,&03,&00,&74,&08,&32,&08,&01,&08 \n" +
"defb &01,&88,&01,&08,&01,&88,&23,&00,&47,&00,&17,&00,&66,&00,&00,&00 \n" +
"defb &04,&00,&8c,&00,&c8,&00,&c8,&04,&c8,&48,&c8,&0c,&d9,&c4,&cb,&08 \n" +
"defb &bc,&88,&8f,&88,&bf,&08,&9d,&4c,&8c,&48,&8c,&8c,&cc,&44,&00,&00 \n" +
"defb &02,&00,&03,&00,&03,&00,&03,&00,&03,&00,&03,&00,&03,&00,&33,&00 \n" +
"defb &13,&00,&03,&00,&03,&00,&23,&4c,&23,&4c,&11,&0c,&00,&cc,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&8c,&06,&48,&42,&0e,&e8,&3d,&a4 \n" +
"defb &5e,&6c,&0f,&6c,&1d,&46,&cc,&46,&cc,&46,&cc,&46,&88,&02,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&07,&08,&9e,&84,&fb,&48,&c8,&c8 \n" +
"defb &c8,&26,&c8,&06,&c8,&06,&c8,&66,&8c,&66,&8c,&66,&04,&44,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&03,&08,&07,&84,&fb,&6a,&48,&06 \n" +
"defb &0c,&06,&0c,&46,&0c,&66,&8c,&26,&8f,&2e,&07,&4c,&33,&88,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&8f,&08,&5a,&c0,&1f,&ea,&0c,&06 \n" +
"defb &0c,&46,&0c,&06,&df,&2e,&8f,&4c,&ff,&88,&4c,&00,&88,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&03,&0e,&16,&e0,&cb,&ac,&8c,&24 \n" +
"defb &0c,&46,&8c,&46,&eb,&8e,&65,&4e,&33,&ce,&00,&46,&00,&22,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&05,&08,&da,&c0,&e9,&4a,&ca,&06 \n" +
"defb &ea,&00,&8c,&00,&8c,&00,&8c,&00,&8c,&00,&8c,&00,&44,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&03,&0c,&34,&e2,&e9,&0c,&8e,&00 \n" +
"defb &07,&00,&23,&08,&11,&c0,&00,&4a,&ff,&4a,&0f,&2e,&ff,&cc,&00,&00 \n" +
"defb &00,&00,&01,&00,&12,&00,&32,&00,&1e,&4c,&5a,&c2,&fe,&cc,&23,&00 \n" +
"defb &23,&00,&23,&00,&32,&00,&23,&00,&23,&00,&23,&00,&11,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&88,&02,&0c,&64,&0c,&64,&0c,&64 \n" +
"defb &4c,&64,&0c,&64,&0c,&64,&8c,&06,&fb,&2e,&07,&0c,&23,&88,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&08,&02,&0c,&64,&0c,&64,&0c,&64 \n" +
"defb &0c,&64,&0c,&06,&ea,&ae,&17,&0c,&65,&4c,&23,&08,&11,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&08,&02,&0c,&64,&0c,&64,&0c,&64 \n" +
"defb &4d,&46,&0f,&4e,&0f,&c6,&1f,&0e,&ae,&8e,&8c,&06,&cc,&66,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&04,&04,&0c,&48,&ca,&0c,&35,&84 \n" +
"defb &65,&08,&12,&08,&47,&08,&17,&4c,&ae,&0c,&8c,&8c,&44,&44,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&44,&04,&8c,&06,&c8,&06,&c8,&26 \n" +
"defb &8f,&2e,&25,&0e,&77,&ae,&00,&8c,&23,&0c,&03,&4c,&33,&88,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&07,&0c,&da,&c0,&77,&48,&00,&84 \n" +
"defb &01,&4c,&23,&88,&57,&00,&8e,&00,&bf,&8c,&0f,&a6,&ff,&cc,&00,&00 \n" +
"defb &01,&08,&32,&08,&32,&00,&32,&00,&12,&00,&03,&00,&13,&00,&46,&00 \n" +
"defb &13,&00,&03,&00,&23,&00,&32,&00,&32,&00,&23,&88,&01,&88,&00,&00 \n" +
"defb &10,&00,&10,&00,&10,&00,&10,&00,&10,&00,&10,&00,&10,&00,&01,&00 \n" +
"defb &10,&00,&10,&00,&01,&00,&01,&00,&01,&00,&01,&00,&01,&00,&00,&00 \n" +
"defb &23,&00,&23,&88,&10,&08,&01,&08,&01,&08,&11,&80,&11,&80,&00,&0c \n" +
"defb &11,&08,&11,&08,&01,&08,&01,&88,&01,&08,&23,&88,&33,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&02,&00,&64,&00,&ac,&23,&4a,&67,&86 \n" +
"defb &8f,&4c,&1f,&88,&2e,&00,&0c,&00,&88,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00 \n" +
"defb &00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00,&00\n" +
";; font" 
                    );*/
        jta.recalculateVisibleLines();
        jta.setFirstLine(0);
        jta.setElectricScroll(0);
        jta.getPainter().setSelectionColor(UIManager.getColor("TextArea.selectionBackground"));
        SyntaxStyle[] styles = SyntaxUtilities.getDefaultSyntaxStyles();
        styles[Token.COMMENT1] = new SyntaxStyle(Color.GRAY,true,false);
        //styles[Token.KEYWORD1] = new SyntaxStyle(new Color(0x000080),false,true);
        //styles[Token.KEYWORD2] = new SyntaxStyle(new Color(0x000080),false,true);
        //styles[Token.KEYWORD3] = new SyntaxStyle(new Color(0x000080),false,true);
        styles[Token.LITERAL1] = new SyntaxStyle(new Color(0x008000),false,true);
        styles[Token.LITERAL2] = new SyntaxStyle(new Color(0x000080),false,true);

        jta.getPainter().setStyles(styles);
        
        fm.add(jta);
        fm.setDefaultCloseOperation(EXIT_ON_CLOSE);
        fm.pack();
        fm.setVisible(true);

    }
    
}
