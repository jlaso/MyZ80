package MyZ80.hardware.board;

import MyZ80.resources.Resources;

import javax.swing.*;
import java.awt.*;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class LedIcon extends JLabel {

    protected String color;

    LedIcon(int bit, String color) {
        super("" + bit);
        this.color = color;
        setPreferredSize(new Dimension(40, 32));
        setIcon(new ImageIcon(Resources.getFileName("devices/led/led-" + color + "-off.png")));
    }

    public void change(boolean status) {
        setIcon(new ImageIcon(Resources.getFileName("devices/led/led-" + color + "-" + (status ? "on" : "off") + ".png")));
    }
}
