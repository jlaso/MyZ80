package MyZ80.momo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonListener implements ActionListener {
    MainWindow win;

    public ButtonListener(MainWindow win) {
        this.win = win;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        JOptionPane.showMessageDialog(null, "button has been pressed");
        win.readData();
    }
}
