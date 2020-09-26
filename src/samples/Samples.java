package MyZ80.samples;

/**
 * Created by joseluislaso on 12/09/15.
 */
public class Samples {

    public Samples() {
    }

    public static String getFile(String file) {
        return "./src/samples/"+file;
        //return Samples.class.getResource(file).getFile();
    }
}
