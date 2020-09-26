package MyZ80.assembler.parser;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by joseluislaso on 29/04/16.
 *
 */
public class ReservedWordsTest {

    @Test
    public void is() throws Exception {
        assertTrue(ReservedWords.is(ReservedWords.WITHOUT_OPERANDS, "NOP"));
        assertFalse(ReservedWords.is(ReservedWords.WITH_ONE_OPERAND, "NOP"));
        assertFalse(ReservedWords.is(ReservedWords.WITH_ONE_OR_NONE_OPERANDS, "NOP"));
        assertFalse(ReservedWords.is(ReservedWords.WITH_TWO_OPERANDS, "NOP"));
        assertFalse(ReservedWords.is(ReservedWords.WITHOUT_OPERANDS, "NOOP"));
    }

}