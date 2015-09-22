package hardware.board;

import myz80.StatusBarPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by joseluislaso on 22/09/15.
 */
public class Board extends JFrame {

    private StatusBarPanel statusBarPanel;

    public Board() {
        super();

        setLayout(new BorderLayout());
        setExtendedState(Frame.MAXIMIZED_BOTH);

    }
}
