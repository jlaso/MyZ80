package myz80;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by joseluislaso on 02/09/15.
 */
public class ProjectConfiguration {

    protected String configFile = ".project.myz80";
    private Properties prop;
    protected String path;

    protected String name;

    public ProjectConfiguration(String path) {

        this.path = path;
        configFile = path + "/" + configFile;
        prop = new Properties();
        try {
            FileInputStream stream = new FileInputStream(configFile);
            prop.load(stream);

            name = prop.getProperty("name", path);

        } catch (NullPointerException ex) {

            //save();

        } catch (IOException ex) {

            //save();

        }

        save();

    }

    public void save() {

        FileOutputStream stream;
        try {
            stream = new FileOutputStream(configFile);

            prop.setProperty("name", name);

            prop.store(stream, null);
            stream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AppConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AppConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
