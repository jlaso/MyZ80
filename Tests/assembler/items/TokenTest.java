package assembler.items;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joseluislaso on 29/04/16.
 *
 */
public class TokenTest {

    private Token token;

    @Before
    public void setUp() throws Exception {
        token = new Token("HALT", "", "", 123, "HALT");
    }

    @After
    public void tearDown() throws Exception {
        token = null;
    }

    @Test
    public void process() throws Exception {
        assertArrayEquals(new int[]{0x76}, token.process());
    }

}