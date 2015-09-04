package myz80;

import javax.swing.*;
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
    JButton acceptBtn, cancelBtn;
    ProjectConfiguration projectConfig = null;
    CallbackRunnable callback = null;

    public ProjectConfigurationForm() {

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        frame.setLayout(new GridBagLayout());
        //frame.setExtendedState(Frame.MAXIMIZED_BOTH);

        frame.setPreferredSize(new Dimension(400,300));

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 10;
        c.ipady = 40;
        frame.add(new JLabel("Configuration of the project"), c);
        c.ipady = 0;

        c.gridy = 1;

        nameLbl = new JLabel("Name");
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridwidth = 1;
        c.weightx = 0.5;
        frame.add(nameLbl, c);

        nameFld = new JTextField("");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridwidth = 9;
        c.weightx = 0;
        frame.add(nameFld, c);

        c.weighty = 1.0;
        c.anchor = GridBagConstraints.PAGE_END;

        acceptBtn = new JButton("Save");
        c.gridy = 6;
        c.gridx = 0;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(acceptBtn, c);

        cancelBtn = new JButton("Cancel");
        c.gridy = 6;
        c.gridx = 1;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(cancelBtn, c);

        frame.pack();
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
