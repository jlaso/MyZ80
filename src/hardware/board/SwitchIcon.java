package MyZ80.hardware.board;

import MyZ80.hardware.devices.Interruptible;
import MyZ80.resources.Resources;

import javax.swing.*;
import java.awt.*;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class SwitchIcon extends JLabel {

    public final static int MODE_ON_OFF = 0;
    public final static int MODE_BUTTON = 1;

    public final static int INTERRUPT_POLLING = 0;
    public final static int INTERRUPT_INTERRUPT = 1;

    protected int mode;
    protected int interrupt;
    protected Interruptible interruptible = null;

    public SwitchIcon(int bit, int mode, int interrupt) {
        super("" + bit);
        this.mode = mode;
        this.interrupt = interrupt;
        setPreferredSize(new Dimension(40, 32));
        setIcon(new ImageIcon(Resources.getFileName("devices/switch/switch" + (mode == MODE_ON_OFF ? "-off" : "") + ".png")));
    }

    public void change(boolean status) {
        if (mode == MODE_ON_OFF) {
            setIcon(new ImageIcon(Resources.getFileName("devices/switch/switch-" + (status ? "on" : "off") + ".png")));
        }
    }

    public void attachInterruptible(Interruptible interruptible) {
        this.interruptible = interruptible;
    }
}
