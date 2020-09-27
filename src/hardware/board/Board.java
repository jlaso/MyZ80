package MyZ80.hardware.board;

import MyZ80.hardware.devices.peripheral.Led8;
import MyZ80.myz80.StatusBarPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class Board extends JFrame {

    private final StatusBarPanel statusBarPanel;
    private final LedArray ledArray;

    public Board() throws Exception {
        this("Z80 Board");
    }

    public Board(String title) throws Exception {
        super(title);

        setLayout(new BorderLayout());
        setExtendedState(Frame.MAXIMIZED_BOTH);

        statusBarPanel = new StatusBarPanel();
        add(statusBarPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel();
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
