/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyZ80.myz80;

import javax.swing.UIManager;

/**
 *
 * @author joseluislaso
 */
public class MyZ80 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // inject my own UI
        UIManager.put("EditTextAreaUI", "MyZ80.jEditSyntax.EditTextAreaUI");

        // launch main window
        new TextEditor();

    }
    
}
