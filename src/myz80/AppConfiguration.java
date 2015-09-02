/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myz80;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    
    protected String configFile = "./appconfig.properties";
    private Properties prop;
    
    public AppConfiguration() {
        
        prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();           
        InputStream stream = loader.getResourceAsStream(configFile);
        try {
            prop.load(stream);
            
            xWindowPos = Integer.parseInt(prop.getProperty("XWindowPos", "0"));
            
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
            prop.store(stream, null);           
            stream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AppConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AppConfiguration.class.getName()).log(Level.SEVERE, null, ex);
	}
        
    }
    
    
}
