package myz80;

import com.sun.tools.javac.comp.Flow;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by joseluislaso on 03/09/15.
 */
public class ProjectConfigurationForm implements ActionListener {

    JFrame frame = new JFrame("Project configuration");
    JLabel nameLbl;
    JTextField nameFld;
    ProjectConfiguration projectConfig = null;
    JButton acceptBtn;
    JButton cancelBtn;
    CallbackRunnable callback = null;

    public ProjectConfigurationForm() {

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        frame.setLayout(new GridBagLayout());
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 10;
        frame.add(new JLabel("Configuration of the project"), c);

        c.gridy = 1;

        nameLbl = new JLabel("Name");
        c.gridx = 0;
        frame.add(nameLbl, c);

        nameFld = new JTextField("");
        c.gridx = 1;
        c.gridwidth = 9;
        frame.add(nameFld, c);

        c.weighty = 1.0;
        c.anchor = GridBagConstraints.PAGE_END;
        
        acceptBtn = new JButton("Save");
        c.gridy = 6;
        c.gridx = 4;
        frame.add(acceptBtn, c);

        cancelBtn = new JButton("Cancel");
        c.gridy = 6;
        c.gridx = 5;
        frame.add(cancelBtn, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==acceptBtn){

            projectConfig.setName(nameFld.getText());
            projectConfig.save();
            hideDialog();
            if (callback != null)
                callback.run();

        }
        if(e.getSource()==cancelBtn){

            hideDialog();

        }
    }

    public void showDialog(ProjectConfiguration projectConfig, CallbackRunnable callback) {
        this.callback = callback;
        this.projectConfig = projectConfig;
        nameFld.setText(projectConfig.getName());
        frame.setVisible(true);
        acceptBtn.addActionListener(this);
    }

    public void hideDialog() {
        acceptBtn.removeActionListener(this);
        frame.setVisible(false);
    }
}
