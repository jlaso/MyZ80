package MyZ80.assembler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Created by joseluislaso on 02/05/16.
 */
public class ProgramTest {

    Program program;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        program = new Program("test.asm", true);
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
        program = null;
    }

    @Test
    public void debug() throws Exception {
        program.debug("This is a test", "red");
        assertTrue(outContent.toString().contains("This is a test"));
        assertTrue(outContent.toString().contains(Tools.ANSI_RED));
    }

    @Test
    public void processFile() throws Exception {

    }

    @Test
    public void assemble() throws Exception {

    }

    @Test
    public void propagateLabel() throws Exception {

    }

    @Test
    public void dumpLabels() throws Exception {

    }

    @Test
    public void dumpPendings() throws Exception {

    }

    @Test
    public void dumpHexProgram() throws Exception {

    }

    @Test
    public void dumpProgram() throws Exception {

    }

    @Test
    public void saveBin() throws Exception {

    }

}