package MyZ80.resources;

import java.io.File;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class Resources {

    public static File getFile(String file) {
        return new File("./src/resources/"+file);
    }

    public static String getFileName(String file) {
        return "./src/resources/"+file;
    }

}
