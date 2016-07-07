/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package momo;

import javax.swing.*;
import java.awt.*;

/**
 * @author joseluislaso
 */
class SidebarPanel extends JScrollPane {

    private JButton[] buttons;
    private int numButtons;

    SidebarPanel(int width, int height, int numButtons) {
        super(null, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        JPanel panel = new JPanel();
        panel.setSize(width, height);
        this.numButtons = numButtons;

        //panel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        //panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        panel.setBackground(new Color(0xddeeee));

        Font font = new Font("Courier New", Font.BOLD, 18);

        buttons = new JButton[numButtons];
        for(int i=0; i<numButtons; i++){
            JButton button = new JButton("Button #"+i);
            button.setFont(font);
            button.setPreferredSize(new Dimension(175,20));
            button.setSize(new Dimension(175,20));
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
