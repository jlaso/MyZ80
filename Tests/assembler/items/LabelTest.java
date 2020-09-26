package MyZ80.assembler.items;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joseluislaso on 29/04/16.
 *
 */
public class LabelTest {

    private Label label;

    @Before
    public void setUp() throws Exception {
        label = new Label(":start", 12 ,":start");
    }

    @After
    public void tearDown() throws Exception {
        label = null;
    }

    @Test
    public void getLabel() throws Exception {
        assertEquals(":start", label.getLabel());
    }

    @Test
    public void getValue() throws Exception {
        assertEquals("12", label.getValue());
    }

    @Test
    public void match() throws Exception {
        assertTrue(label.match(":start"));
    }

    @Test
    public void toStringTest() throws Exception {
        assertEquals(":start => Label{ 12[000C]: ':start'}", label.toString());
    }

}