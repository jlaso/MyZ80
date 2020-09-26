package MyZ80.DI;

import MyZ80.assembler.items.Constant;
import junit.framework.TestCase;

/**
 * Created by joseluislaso on 28/04/16.
 *
 */
public class ContainerTest extends TestCase {

    private Container container;

    public void setUp() throws Exception {
        super.setUp();
        container = Container.getContainer();
        container.constants.add(new Constant("pi", "314", "#define pi 314"));
    }

    public void tearDown() throws Exception {
        container = null;
    }

    public void testGetContainer() throws Exception {
        assertTrue(container != null);
    }

    public void testGetValueOf1() throws Exception {
        assertEquals("314", container.getValueOf("pi"));
    }

    public void testGetValueOf2() throws Exception {
        String value;
        try {
            value = container.getValueOf("ka");
        }catch(Exception e){
            assertEquals("Constant ka not declared yet!", e.getMessage());
            return;
        }
        throw new Exception("error, the constant 'ka' is resolved as a "+value);
    }

}