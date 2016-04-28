package assembler.parser;

import assembler.items.Item;
import assembler.items.Token;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joseluislaso on 27/04/16.
 *
 */
public class ProgramParserTest {
    @Test
    public void itemizeLine() throws Exception {
        // MyClass is tested
        ProgramParser tester = new ProgramParser();
        Item item = tester.itemizeLine("NOP;",0, 0);
        Token expect = new Token("NOP", "", "", 0, "NOP");

        // assert statements
        //assertEquals("Expected another opcode", expect.getOpCode(), item.getOpCode());
        assertEquals("10x10 is 100", 10 * 10, 100);
    }

}