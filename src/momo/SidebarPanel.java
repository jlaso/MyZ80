/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyZ80.momo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author joseluislaso
 */
class SidebarPanel extends JScrollPane {

    private final JButton[] buttons;
    private final int numButtons;

    SidebarPanel(int width, int height, int numButtons, MainWindow win) {
        super(null, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
        Config config = new Config();
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        JPanel panel = new JPanel();
        panel.setSize(width, height);
        this.numButtons = numButtons;

        //panel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        //panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        panel.setBackground(new Color(0xddeeee));

        ActionListener al = new ButtonListener(win);

        buttons = new JButton[numButtons];
        for(int i=0; i<numButtons; i++){
            JButton button = new JButton("Button #"+i);
            button.setFont(config.font);
            button.setPreferredSize(new Dimension(175,20));
            button.setSize(new Dimension(175,20));
            button.addActionListener(al);
            buttons[i] = button;
            panel.add(button);
        }
        setViewportView(panel);
    }

    void setText(int index, String text){
        if(index < numButtons) {
            buttons[index].setText(text);
        }
    }


}
