package MyZ80.assembler.items;

import MyZ80.DI.Container;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joseluislaso on 29/04/16.
 *
 */
public class DirectiveTest {

    private Directive directive;
    private Container container;

    @Before
    public void setUp() throws Exception {
        container = Container.getContainer();
        container.constants.add(0, new Constant("myvar", "12", "myvar=12"));
        directive = new Directive(".db", "12", 0, ".db data 0");
    }

    @After
    public void tearDown() throws Exception {
        directive = null;
    }

    @Test
    public void is() throws Exception {
        assertTrue(Directive.is(".db"));
    }

    @Test
    public void getName() throws Exception {
        assertEquals(".db", directive.getName());
    }

    @Test
    public void getValue() throws Exception {
        assertEquals("12", directive.getValue());
    }

    @Test
    public void parse() throws Exception {

    }

    @Test
    public void isDB() throws Exception {
        assertTrue(directive.isDB());
    }

    @Test
    public void isDW() throws Exception {
        assertFalse(directive.isDW());
    }

    @Test
    public void isORG() throws Exception {
        assertFalse(directive.isORG());
    }

    @Test
    public void tokenizeOperands() throws Exception {
        int[] expected1 = {65, 12, 0};
        Directive directive1 = new Directive(".dw", "\"A\",12", 0, ".dw 'A");
        assertArrayEquals(expected1, directive1.process());
    }

    @Test
    public void addPending() throws Exception {
        int[] expected1 = {13, 0};
        Directive directive1 = new Directive(".dw", "myvar+1", 0, ".dw myvar+1");
        int[] expected2 = {11};
        Directive directive2 = new Directive(".db", "myvar-1", 0, ".dw myvar+1");
        assertArrayEquals(expected1, directive1.process());
        assertArrayEquals(expected2, directive2.process());
    }

    @Test
    public void process() throws Exception {
        int[] data = {12};
        assertArrayEquals(data,directive.process());

        assertNull(new Directive(".org", "0", ".org 0").process());
    }

    @Test
    public void toStringTest() throws Exception {
        assertEquals(".db data 0 => Directive{ 0[0000]: name='.db', value='12'   [0C] }",directive.toString());
    }

}