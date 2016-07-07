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
class MainPanel extends JPanel {


    MainPanel(int width, int height) {
        super();
        setPreferredSize(new Dimension(width, height));

        setLayout(new BorderLayout());
        setBackground(new Color(0xcceecc));

        Font font = new Font("Courier New", Font.BOLD, 18);

    }


}
