package myz80;

/**
 * Created by joseluislaso on 02/09/15.
 */
public class Project {

    ProjectConfiguration config;

    public Project(String path) {

        config = new ProjectConfiguration(path);

    }

    public String getPath() {
        return config.getPath();
    }

    public ProjectConfiguration getConfig() {
        return config;
    }
}
