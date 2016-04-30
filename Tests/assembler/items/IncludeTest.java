package assembler.items;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joseluislaso on 29/04/16.
 *
 */
public class IncludeTest {

    private Include include;

    @Before
    public void setUp() throws Exception {
        include = new Include("test.asm", "include");
    }

    @After
    public void tearDown() throws Exception {
        include = null;
    }

    @Test
    public void getFile() throws Exception {
        assertEquals("test.asm", include.getFile());
    }

    @Test
    public void toStringTest() throws Exception {
        assertEquals("Include{file='test.asm'}", include.toString());
    }

}