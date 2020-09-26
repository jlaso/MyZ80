/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyZ80.momo;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author joseluislaso
 */
class StatusBarPanel extends JPanel{
    
    private JLabel label1 = new JLabel("(1)");
    private JLabel label2 = new JLabel("(2)", SwingConstants.RIGHT);

    StatusBarPanel(){
        super();

        setLayout(new BorderLayout());
        setBackground(new Color(0xcccccc));

        Font font = new Font("Courier New", Font.BOLD, 18);

        label1.setFont(font);
        label1.setForeground(Color.BLACK);
        add(label1, BorderLayout.WEST);

        label2.setFont(font);
        label2.setPreferredSize(new Dimension(200, 40));
        label2.setForeground(Color.BLACK);
        add(label2, BorderLayout.EAST);
    }

    public void setText(String text)
    {
        label1.setText(text);
    }

    public void setText(String text1, String text2) {
        if (text1 != null) this.setText(text1);
        label2.setText(text2);
    }
            
}
