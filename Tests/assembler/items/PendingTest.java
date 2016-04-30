package assembler.items;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joseluislaso on 29/04/16.
 */
public class PendingTest {

    private Pending pending;

    @Before
    public void setUp() throws Exception {
        pending = new Pending("myvar", 123, 456);
    }

    @After
    public void tearDown() throws Exception {
        pending = null;
    }

    @Test
    public void getPosition() throws Exception {
        assertEquals(123, pending.getPosition());
    }

    @Test
    public void getType() throws Exception {
        assertEquals(456, pending.getType());
    }

    @Test
    public void getCause() throws Exception {
        assertEquals(" myvar ", pending.getCause());
    }

    @Test
    public void setCause() throws Exception {
        pending.setCause("myothervar");
        assertEquals(" myothervar ", pending.getCause());
        assertNotEquals(" myvar ", pending.getClass());
    }

    @Test
    public void match() throws Exception {
        assertTrue(pending.match("myvar"));
        assertFalse(pending.match("myothervar"));
    }

    @Test
    public void replaceLabel() throws Exception {
        pending.replaceLabel("myvar", "myothervar");
        assertFalse(pending.match("myvar"));
        assertTrue(pending.match("myothervar"));
    }

    @Test
    public void typeAsString() throws Exception {
        assertEquals("456", pending.typeAsString());
    }

    @Test
    public void toStringTest() throws Exception {
        assertEquals("Pending{  myvar  pos=123, [456] }", pending.toString());
    }

}