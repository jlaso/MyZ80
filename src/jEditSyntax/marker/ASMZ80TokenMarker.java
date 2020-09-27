package MyZ80.jEditSyntax.marker;

import MyZ80.jEditSyntax.KeywordMap;
import MyZ80.jEditSyntax.Token;

import javax.swing.text.Segment;

/**
 *
 * @author joseluislaso
 */
public class ASMZ80TokenMarker extends TokenMarker{

    public ASMZ80TokenMarker()
    {
        this.keywords = getKeywords();
    }

    public byte markTokensImpl(byte token, Segment line, int lineIndex)
    {
        char[] array = line.array;
        int offset = line.offset;
        lastOffset = offset;
        lastKeyword = offset;
        int length = line.count + offset;
        boolean backslash = false;

loop:		for(int i = offset; i < length; i++)
        {
            int i1 = (i+1);

            char c = array[i];
            if(c == '\\')
            {
                backslash = !backslash;
                continue;
            }

            switch(token)
            {
                case Token.NULL:
                    switch(c)
                    {
                        case '#':
                            if(backslash)
                                backslash = false;
                            break;
                        case '"':
                            doKeyword(line,i,c);
                            if(backslash)
                                backslash = false;
                            else
                            {
                                addToken(i - lastOffset,token);
                                token = Token.LITERAL1;
                                lastOffset = lastKeyword = i;
                            }
                            break;
                        case '\'':
                            doKeyword(line,i,c);
                            if(backslash)
                                backslash = false;
                            else
                            {
                                addToken(i - lastOffset,token);
                                token = Token.LITERAL2;
                                lastOffset = lastKeyword = i;
                            }
                            break;
                        case ':':
                            if(lastKeyword == offset)
                            {
                                if(doKeyword(line,i,c))
                                    break;
                                backslash = false;
                                addToken(i1 - lastOffset,Token.LABEL);
                                lastOffset = lastKeyword = i1;
                            }
                            else if(doKeyword(line,i,c))
                                break;
                            break;
                        case '/':
                            backslash = false;
                            doKeyword(line,i,c);
                            if(length - i > 1)
                            {
                                switch(array[i1])
                                {
                                    case '*':
                                        addToken(i - lastOffset,token);
                                        lastOffset = lastKeyword = i;
                                        if(length - i > 2 && array[i+2] == '*')
                                            token = Token.COMMENT2;
                                        else
                                            token = Token.COMMENT1;
                                        break;
                                    case '/':
                                        addToken(i - lastOffset,token);
                                        addToken(length - i,Token.COMMENT1);
                                        lastOffset = lastKeyword = length;
                                        break loop;
                                }
                            }
                            break;
                        default:
                            backslash = false;
                            if(!Character.isLetterOrDigit(c) && c != '_')
                                doKeyword(line,i,c);
                            break;
                    }
                    break;
                case Token.COMMENT1:
                case Token.COMMENT2:
                    backslash = false;
                    if(c == '*' && length - i > 1)
                    {
                        if(array[i1] == '/')
                        {
                            i++;
                            addToken((i+1) - lastOffset,token);
                            token = Token.NULL;
                            lastOffset = lastKeyword = i+1;
                        }
                    }
                    break;
                case Token.LITERAL1:
                    if(backslash)
                        backslash = false;
                    else if(c == '"')
                    {
                        addToken(i1 - lastOffset,token);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
                    break;
                case Token.LITERAL2:
                    if(backslash)
                        backslash = false;
                    else if(c == '\'')
                    {
                        addToken(i1 - lastOffset,Token.LITERAL1);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
                    break;
                default:
                    throw new InternalError("Invalid state: " + token);
            }
        }

        if(token == Token.NULL)
            doKeyword(line,length,'\0');

        switch(token)
        {
            case Token.LITERAL1:
            case Token.LITERAL2:
                addToken(length - lastOffset,Token.INVALID);
                token = Token.NULL;
                break;
            case Token.KEYWORD2:
                addToken(length - lastOffset,token);
                if(!backslash)
                    token = Token.NULL;
            default:
                addToken(length - lastOffset,token);
                break;
        }

        return token;
    }

    public static KeywordMap getKeywords()
    {
        if(asmZ80Keywords == null)
        {            
            // z80 instructions, take a look http://clrhome.org/table/ and http://clrhome.org/asm/
            asmZ80Keywords = new KeywordMap(false);
            asmZ80Keywords.add("adc",Token.KEYWORD2);
            asmZ80Keywords.add("add",Token.KEYWORD2);
            asmZ80Keywords.add("call",Token.KEYWORD2);
            asmZ80Keywords.add("cp",Token.KEYWORD2);
            asmZ80Keywords.add("dec",Token.KEYWORD2);
            asmZ80Keywords.add("ex",Token.KEYWORD2);
            asmZ80Keywords.add("jp",Token.KEYWORD2);
            asmZ80Keywords.add("jr",Token.KEYWORD2);
            asmZ80Keywords.add("halt",Token.KEYWORD2);
            asmZ80Keywords.add("inc",Token.KEYWORD2);
            asmZ80Keywords.add("in",Token.KEYWORD2);
            asmZ80Keywords.add("ld",Token.KEYWORD2);
            asmZ80Keywords.add("ldi",Token.KEYWORD2);
            asmZ80Keywords.add("nop",Token.KEYWORD2);
            asmZ80Keywords.add("ret",Token.KEYWORD2);
            asmZ80Keywords.add("rra",Token.KEYWORD2);
            asmZ80Keywords.add("sub",Token.KEYWORD2);
            asmZ80Keywords.add("xor",Token.KEYWORD2);

            // pseudo
            asmZ80Keywords.add("#include",Token.KEYWORD3);
            asmZ80Keywords.add("#define",Token.KEYWORD3);
           
            // pseudo
            asmZ80Keywords.add("defb",Token.KEYWORD1);
            asmZ80Keywords.add("equ",Token.KEYWORD1);
            asmZ80Keywords.add("defs",Token.KEYWORD1);
            asmZ80Keywords.add(".org",Token.KEYWORD1);
            asmZ80Keywords.add("org",Token.KEYWORD1);
            asmZ80Keywords.add(".db",Token.KEYWORD1);
            asmZ80Keywords.add("db",Token.KEYWORD1);
            asmZ80Keywords.add("nolist",Token.KEYWORD1);
            asmZ80Keywords.add("let",Token.KEYWORD1);

            // literals
            asmZ80Keywords.add("true",Token.LITERAL2);
            asmZ80Keywords.add("false",Token.LITERAL2);
        }
        return asmZ80Keywords;
    }

    // private members
    private static KeywordMap asmZ80Keywords;
    private KeywordMap keywords;
    private int lastOffset;
    private int lastKeyword;

    private boolean doKeyword(Segment line, int i, char c)
    {
        int i1 = i+1;

        int len = i - lastKeyword;
        byte id = keywords.lookup(line,lastKeyword,len);
        if(id != Token.NULL)
        {
            if(lastKeyword != lastOffset)
                addToken(lastKeyword - lastOffset,Token.NULL);
            addToken(len,id);
            lastOffset = i;
        }
        lastKeyword = i1;
        return false;
    }
}
