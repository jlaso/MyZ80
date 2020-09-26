package MyZ80.fileFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joseluislaso on 29/04/16.
 *
 */
public class Z80FileFormatTest {

    private Z80FileFormat z80FileFormat;

    @Before
    public void setUp() throws Exception {
        z80FileFormat = new Z80FileFormat(10);
    }

    @After
    public void tearDown() throws Exception {
        z80FileFormat = null;
    }

    @Test
    public void allFlowTest() throws Exception {
        byte[] testData = new byte[]{1,2,3,4,5,6,7,8,9,0};
        z80FileFormat.addData(0, testData);
        z80FileFormat.saveToFile("test.z80");
        z80FileFormat.readFromFile("test.z80");
        assertArrayEquals(testData, z80FileFormat.getBytes());
    }


}