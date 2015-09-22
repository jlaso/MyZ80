package hardware.board;

import hardware.devices.peripheral.Led8;
import myz80.StatusBarPanel;
import resources.Resources;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class Board extends JFrame {

    private StatusBarPanel statusBarPanel;
    private JPanel mainPanel;
    private LedArray ledArray;

    public Board() throws Exception {
        super();

        setLayout(new BorderLayout());
        setExtendedState(Frame.MAXIMIZED_BOTH);
        
        setTitle("Z80 Board");

        statusBarPanel = new StatusBarPanel();
        add(statusBarPanel, BorderLayout.SOUTH);

        mainPanel = new JPanel();
        mainPanel.setSize(200, getHeight());
        //mainPanel.setLayout(new BorderLayout());

        ledArray = new LedArray(0x03, LedArray.RED);
        add(ledArray, BorderLayout.EAST);

        setVisible(true);

        //ledArray.changeLed(3, true);
        Led8 led8 = new Led8(ledArray.getLeds());

//        int dd = 0;
//
//        while (true) {
//            Thread.sleep(1000);
//            led8.write(dd);
//            dd = (dd + 1) & 0x00ff;
//        }

    }

    public LedArray getLedArray() {
        return ledArray;
    }

    public StatusBarPanel getStatusBarPanel() {
        return statusBarPanel;
    }
}
