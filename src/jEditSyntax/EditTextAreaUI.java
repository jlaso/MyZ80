/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyZ80.jEditSyntax;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author joseluislaso
 */
public class EditTextAreaUI extends ComponentUI {

    public static ComponentUI createUI(JComponent c) {
        return new EditTextAreaUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        g.setColor(c.getBackground());
        g.fillRect(10,10, c.getWidth()- 20, c.getHeight()-20);
    }
    
}