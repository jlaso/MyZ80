package MyZ80.hardware.board;

import MyZ80.assembler.Tools;
import MyZ80.resources.Resources;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class LedArray extends JPanel {

    final static String RED = "red";
    final static String GREEN = "green";
    final static String YELLOW = "yellow";

    protected String color;
    LedIcon[] leds = new LedIcon[8];

    public LedArray(int port, String color) {
        super();

        this.color = color;
        add(new JLabel("Port ["+Tools.byteToHex(port)+"] "));

        for (int i = 7; i >= 0; i--) {
            leds[i] = new LedIcon(i, color);
            leds[i].setLocation(10 + i * 100, 322);
            add(leds[i]);
        }
    }

    public void changeLed(int bit, boolean status) {
        leds[bit & 0x07].change(status);
    }

    public LedIcon[] getLeds() {
        return leds;
    }
}
