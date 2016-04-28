package assembler.items;

import junit.framework.TestCase;

/**
 * Created by joseluislaso on 28/04/16.
 *
 */
public class ConstantTest extends TestCase {

    Constant constant;

    public void setUp() throws Exception {
        super.setUp();
        constant = new Constant("PI", "314", "#Declare PI value");
    }

    public void tearDown() throws Exception {
        constant = null;
    }

    public void testGetName() throws Exception {
        assertEquals("PI", constant.getName());
    }

    public void testGetValue() throws Exception {
        assertEquals("314", constant.getValue());
    }

    public void testMatch() throws Exception {
        assertTrue(constant.match("PI"));
        assertFalse(constant.match("PA"));
    }

    public void testToString() throws Exception {
        assertEquals(constant.toString(), "#Declare PI value => Constant{PI = 314}");
    }

}