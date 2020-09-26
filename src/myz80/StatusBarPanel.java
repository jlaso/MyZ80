/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyZ80.myz80;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author joseluislaso
 */
public class StatusBarPanel extends JPanel{
    
    JLabel label = new JLabel("This is the status bar panel");
    JLabel label2 = new JLabel("", SwingConstants.RIGHT);

    public StatusBarPanel(){
        super();

        //setSize(200, 400);
        setLayout(new BorderLayout());
        setBackground(new Color(0xcccccc));

        label.setFont(new Font("Courier New", Font.BOLD, 18));
        label.setForeground(Color.BLACK);
        add(label, BorderLayout.WEST);
        label2.setFont(new Font("Courier New", Font.BOLD, 18));
        label2.setPreferredSize(new Dimension(200, 40));
        label2.setForeground(Color.BLACK);
        add(label2, BorderLayout.EAST);
    }
    
    public void setText(String text) {        
        label.setText(text);        
    }

    public void setText2(String text) {
        label2.setText(text);
    }
            
}
