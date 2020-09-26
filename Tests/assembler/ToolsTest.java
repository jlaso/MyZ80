package MyZ80.assembler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Created by joseluislaso on 30/04/16.
 *
 */
public class ToolsTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void byteToHex() throws Exception {
        int[] test = new int[]{0, 0x1A, 0xFF, 0xA3, 0x1B};
        assertEquals("001AFFA31B", Tools.bytesToHex(test));
    }

    @Test
    public void addressToHex() throws Exception {
        assertEquals("1234", Tools.addressToHex(0x1234));
    }

    @Test
    public void bytesToHex() throws Exception {
        assertEquals("23", Tools.byteToHex(0x23));
    }

    @Test
    public void color() throws Exception {
        assertEquals(Tools.ANSI_BLACK, Tools.color("black"));
    }

    @Test
    public void println() throws Exception {
        Tools.println("black", "hello");
        assertEquals(Tools.ANSI_BLACK+"hello"+Tools.ANSI_RESET+"\n", outContent.toString());
    }

    @Test
    public void println_if() throws Exception {
        Tools.println_if(false, "red", "Hi");
        assertEquals("", outContent.toString());
    }

    @Test
    public void figureOut() throws Exception {
        assertEquals(10, Tools.figureOut("10"));
        assertEquals(16, Tools.figureOut("10h"));
        assertEquals(8, Tools.figureOut("10o"));
        assertEquals(16, Tools.figureOut("&10"));
        assertEquals(16, Tools.figureOut("$10"));
    }

    @Test
    public void eval() throws Exception {
        assertEquals(3.0, Tools.eval("1+2"), 0);
        assertEquals(3.5, Tools.eval("1.2+2.3"), 0);
    }

}