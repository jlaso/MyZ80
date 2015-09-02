/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myz80;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author joseluislaso
 */
public class StatusBarPanel extends JPanel{
    
    JLabel label = new JLabel("This is the status bar panel");
    
    public StatusBarPanel(){
        super();

        //setSize(200, 400);
        setLayout(new BorderLayout());

        add(label, BorderLayout.CENTER);        
    }
    
    public void setText(String text) {        
        label.setText(text);        
    }
            
}
