package jEditSyntax.marker;

import jEditSyntax.KeywordMap;
import jEditSyntax.Token;

/**
 *
 * @author joseluislaso
 */
public class ASMZ80TokenMarker extends CTokenMarker{

    public ASMZ80TokenMarker()
    {
        super(false,getKeywords());
    }

    public static KeywordMap getKeywords()
    {
        if(asmZ80Keywords == null)
        {
            // z80 instructions
            asmZ80Keywords = new KeywordMap(false);
            asmZ80Keywords.add("adc",Token.KEYWORD2);
            asmZ80Keywords.add("add",Token.KEYWORD2);
            asmZ80Keywords.add("call",Token.KEYWORD2);
            asmZ80Keywords.add("jp",Token.KEYWORD2);
            asmZ80Keywords.add("ld",Token.KEYWORD2);
            asmZ80Keywords.add("nop",Token.KEYWORD2);
           
            // pseudo
            asmZ80Keywords.add("defb",Token.KEYWORD1);
            asmZ80Keywords.add("equ",Token.KEYWORD1);
            asmZ80Keywords.add("org",Token.KEYWORD1);

            // literals
            asmZ80Keywords.add("true",Token.LITERAL2);
            asmZ80Keywords.add("false",Token.LITERAL2);
        }
        return asmZ80Keywords;
    }

    // private members
    private static KeywordMap asmZ80Keywords;
}
