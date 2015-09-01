/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myz80;

import java.awt.BorderLayout;
import java.awt.Button;
import javax.swing.JPanel;

/**
 *
 * @author joseluislaso
 */
public class StatusBarPanel extends JPanel{
    
    public StatusBarPanel(){
        super();

        //setSize(200, 400);
        setLayout(new BorderLayout());

        add(new Button("North"), BorderLayout.NORTH);        
    }
}
