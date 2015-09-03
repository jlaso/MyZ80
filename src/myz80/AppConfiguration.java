/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myz80;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joseluislaso
 */
public class AppConfiguration {
    
    protected int xWindowPos = 0;
    protected int yWindowPos = 0;
    protected int xWindowSize = -1;
    protected int yWindowSize = -1;
    protected String lastProject = "";
    
    protected String configFile = "resources/appconfig.properties";
    private Properties prop;
    
    public AppConfiguration() {
        
        prop = new Properties();
        //ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //InputStream stream = getClass().getClassLoader().getResourceAsStream(configFile);
        try {
            FileInputStream stream = new FileInputStream(configFile);
            prop.load(stream);
            
            xWindowPos = Integer.parseInt(prop.getProperty("xWindowPos", "0"));
            yWindowPos = Integer.parseInt(prop.getProperty("yWindowPos", "0"));
            xWindowSize = Integer.parseInt(prop.getProperty("xWindowSize", "-1"));
            yWindowSize = Integer.parseInt(prop.getProperty("yWindowSize", "-1"));
            lastProject = prop.getProperty("lastProject", "");

        } catch (NullPointerException ex) {

            save();
            
        } catch (IOException ex) {
            
            save();
            
        }
        
    }
    
    public void save() {
        
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(configFile);

            prop.setProperty("xWindowPos", Integer.toString(xWindowPos));
            prop.setProperty("yWindowPos", Integer.toString(yWindowPos));
            prop.setProperty("xWindowSize", Integer.toString(xWindowSize));
            prop.setProperty("yWindowSize", Integer.toString(yWindowSize));
            prop.setProperty("lastProject", lastProject);

            prop.store(stream, null);
            stream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AppConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AppConfiguration.class.getName()).log(Level.SEVERE, null, ex);
	    }
        
    }

    public int getxWindowPos() {
        return xWindowPos;
    }

    public void setxWindowPos(int xWindowPos) {
        this.xWindowPos = xWindowPos;
    }

    public int getyWindowPos() {
        return yWindowPos;
    }

    public void setyWindowPos(int yWindowPos) {
        this.yWindowPos = yWindowPos;
    }

    public int getxWindowSize() {
        return xWindowSize;
    }

    public void setxWindowSize(int xWindowSize) {
        this.xWindowSize = xWindowSize;
    }

    public int getyWindowSize() {
        return yWindowSize;
    }

    public void setyWindowSize(int yWindowSize) {
        this.yWindowSize = yWindowSize;
    }

    public String getLastProject() {
        return lastProject;
    }

    public void setLastProject(String lastProject) {
        this.lastProject = lastProject;
    }
}
