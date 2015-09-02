/*
 * This simple editor is based on the idea of Turk4n
 * see original post here: http://forum.codecall.net/topic/49721-simple-text-editor/
 */
package myz80;

import jEditSyntax.JEditTextArea;
import jEditSyntax.TextAreaDefaults;
import jEditSyntax.marker.ASMZ80TokenMarker;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.text.DefaultEditorKit;

/**
 *
 * @author joseluislaso
 */
public class TextEditor extends JFrame {
    
    private JEditTextArea area;
    private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
    private String currentFile = "Untitled";
    private boolean changed = false;
    
    Action New, Open, Save, SaveAs, Quit;
    
    ActionMap m;
    Action Cut, Copy, Paste;
    
    public TextEditor(AppConfiguration appConfiguration) 
    {
        super();
        
        setLayout(new BorderLayout());
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        
        // Left SideBar Panel
        SideBarLeftPanel sideBarLeftPanel = new SideBarLeftPanel();
        add(sideBarLeftPanel, BorderLayout.WEST);
        
        // Right SideBar Panel
        SideBarRightPanel sideBarRightPanel = new SideBarRightPanel();
        add(sideBarRightPanel, BorderLayout.EAST);
        
        // Status Bar Panel
        StatusBarPanel statusBarPanel = new StatusBarPanel();
        add(statusBarPanel, BorderLayout.SOUTH);
        
        TextAreaDefaults defaults = TextAreaDefaults.getDefaults();
        defaults.rows = 20;
        defaults.cols = 200;
        area = new JEditTextArea(defaults);
        
        New = new AbstractAction("New", new ImageIcon("icons/new.gif")) {
            public void actionPerformed(ActionEvent e) {
                saveOld();
                area.setText("");
                currentFile = "Untitled";
                setTitle(currentFile);
                changed = false;
                Save.setEnabled(false);
                SaveAs.setEnabled(false);
            }
        };

        Open = new AbstractAction("Open", new ImageIcon("icons/open.gif")) {
            public void actionPerformed(ActionEvent e) {
                saveOld();
                if(dialog.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
                    area.readInFile(dialog.getSelectedFile().getAbsolutePath());
                }
                SaveAs.setEnabled(true);
            }
        };

        Save = new AbstractAction("Save", new ImageIcon("icons/save.gif")) {
            public void actionPerformed(ActionEvent e) {
                if(!currentFile.equals("Untitled"))
                    saveFile(currentFile);
                else
                    saveFileAs();
            }
        };

        SaveAs = new AbstractAction("Save as...") {
            public void actionPerformed(ActionEvent e) {
                saveFileAs();
            }
        };

        Quit = new AbstractAction("Quit") {
            public void actionPerformed(ActionEvent e) {
                saveOld();
                System.exit(0);
            }
        };

        m = area.getActionMap();
        Cut = m.get(DefaultEditorKit.cutAction);
        Copy = m.get(DefaultEditorKit.copyAction);
        Paste = m.get(DefaultEditorKit.pasteAction);

        area.setTokenMarker(new ASMZ80TokenMarker());
        area.setFont(new Font("Monospaced",Font.PLAIN,12));
        //JScrollPane scroll = new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //add(scroll,BorderLayout.CENTER);
        add(area, BorderLayout.CENTER);
        
        JMenuBar JMB = new JMenuBar();
        setJMenuBar(JMB);
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMB.add(file); 
        JMB.add(edit);

        file.add(New);
        file.add(Open);
        file.add(Save);
        file.add(Quit);
        file.add(SaveAs);
        file.addSeparator();

        for(int i=0; i<4; i++)
            file.getItem(i).setIcon(null);

        edit.add(Cut);
        edit.add(Copy);
        edit.add(Paste);

        edit.getItem(0).setText("Cut out");
        edit.getItem(1).setText("Copy");
        edit.getItem(2).setText("Paste");

        JToolBar tool = new JToolBar();
        add(tool,BorderLayout.NORTH);
        tool.add(New);
        tool.add(Open);
        tool.add(Save);
        tool.addSeparator();

        JButton cut = tool.add(Cut), cop = tool.add(Copy),pas = tool.add(Paste);

        cut.setText(null); cut.setIcon(new ImageIcon("icons/cut.gif"));
        cop.setText(null); cop.setIcon(new ImageIcon("icons/copy.gif"));
        pas.setText(null); pas.setIcon(new ImageIcon("icons/paste.gif"));

        Save.setEnabled(false);
        SaveAs.setEnabled(false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        area.addKeyListener(k1);
        setTitle(currentFile);
        setVisible(true);
    };
    
    private KeyListener k1 = new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
            changed = true;
            Save.setEnabled(true);
            SaveAs.setEnabled(true);
        }
    };
    
    private void saveFileAs() {
        if(dialog.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
            saveFile(dialog.getSelectedFile().getAbsolutePath());
    }
    
    private void saveOld() {
        if(changed) {
            if(JOptionPane.showConfirmDialog(this, "Would you like to save "+ currentFile +" ?","Save",JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION)
                saveFile(currentFile);
        }
    }
   
    
    private void saveFile(String fileName) {
        try {
            FileWriter w = new FileWriter(fileName);
            area.write(w);
            w.close();
            currentFile = fileName;
            setTitle(currentFile);
            changed = false;
            Save.setEnabled(false);
        }
        catch(IOException e) {
        }
    }
}
