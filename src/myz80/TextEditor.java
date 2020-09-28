/*
 * This simple editor is based on the idea of Turk4n
 * see original post here: http://forum.codecall.net/topic/49721-simple-text-editor/
 */
package MyZ80.myz80;

import MyZ80.assembler.Program;
import MyZ80.assembler.parser.ProgramParser;
import MyZ80.jEditSyntax.JEditTextArea;
import MyZ80.jEditSyntax.TextAreaDefaults;
import MyZ80.jEditSyntax.marker.ASMZ80TokenMarker;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author joseluislaso
 */
public class TextEditor extends JFrame {

    private static String UNTITLED = "Untitled";

    private boolean changed = false;
    private JEditTextArea area;
    private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
    private String currentFile;
    private String currentProject;
    private StatusBarPanel statusBarPanel;
    private AppConfiguration appConfig;
    private AreaEditorHandler areaEditorHandler;
    private SideBarLeftPanel sideBarLeftPanel;
    private Project project;
    private ProjectConfigurationForm configForm;

    ActionMap m;
    Action NewAction, OpenAction, ConfigAction, QuitAction;
    Action CutAction, CopyAction, PasteAction;
    Action AssembleAction;

    public TextEditor()
    {
        super();

        System.out.println("Starting MyZ80...");

        appConfig = new AppConfiguration();

        currentFile = UNTITLED;
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dialog.setDialogType(JFileChooser.SAVE_DIALOG);
        dialog.setApproveButtonText("Select");
        dialog.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Any folder";
            }

        });

        setLayout(new BorderLayout());
        if ((appConfig.getxWindowSize() == -1) || (appConfig.getyWindowSize() == -1)) {
            setExtendedState(Frame.MAXIMIZED_BOTH);
        }else{
            setExtendedState(Frame.NORMAL);
            setBounds(appConfig.getxWindowPos(), appConfig.getyWindowPos(), appConfig.getxWindowSize(), appConfig.getyWindowSize());
            setLocationByPlatform(false);
        }

        // Main text area (editor)
        TextAreaDefaults defaults = TextAreaDefaults.getDefaults();
        defaults.rows = 20;
        defaults.cols = 200;
        area = new JEditTextArea(defaults);
        area.setTokenMarker(new ASMZ80TokenMarker());
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(area, BorderLayout.CENTER);

        areaEditorHandler = new AreaEditorHandler(area);
        
        // Left SideBar Panel
        sideBarLeftPanel = new SideBarLeftPanel(areaEditorHandler);
        add(sideBarLeftPanel, BorderLayout.WEST);
        
        // Right SideBar Panel
        SideBarRightPanel sideBarRightPanel = new SideBarRightPanel();
        add(sideBarRightPanel, BorderLayout.EAST);
        
        // Status Bar Panel
        statusBarPanel = new StatusBarPanel();
        add(statusBarPanel, BorderLayout.SOUTH);

        initializeMenuActions();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //pack();
        area.addKeyListener(k1);
        setTitle(currentFile);
        setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Rectangle r = getBounds();
                appConfig.setxWindowPos(r.x);
                appConfig.setyWindowPos(r.y);
                appConfig.setxWindowSize(r.width);
                appConfig.setyWindowSize(r.height);
                // save the app configuration
                appConfig.save();
                System.out.println("Finishing MyZ80...");
            }
        });

        if (appConfig.getLastProject() != "") {
            String file = appConfig.getLastProject();
            System.out.println("Opening " + file + "...");
            openProject(file);
        }

        configForm = new ProjectConfigurationForm(this);
    };

    private void openProject(String projectName) {
        projectName = projectName.replaceFirst("^~", System.getProperty("user.home"));
        project = new Project(projectName);
        sideBarLeftPanel.loadProject(project);
        ConfigAction.setEnabled(true);
        appConfig.setLastProject(projectName);
        setTitle(project.getConfig().getName());
    }

    private void initializeMenuActions() {

        NewAction = new AbstractAction("New", new ImageIcon("icons/new.gif")) {
            public void actionPerformed(ActionEvent e) {
                saveOld();
                // empty editor area
                area.setText("");
                currentFile = UNTITLED;
                changed = false;
                ConfigAction.setEnabled(true);
                updateStatusBar();
            }
        };

        OpenAction = new AbstractAction("Open", new ImageIcon("icons/open.gif")) {
            public void actionPerformed(ActionEvent e) {
                saveOld();
                String home = System.getProperty("user.dir");
                if (currentProject != "")
                    home = currentProject;

                dialog.setCurrentDirectory(new File(home+"/."));
                if(dialog.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
                    File selectedFile = dialog.getSelectedFile();
                    if (!dialog.getSelectedFile().isDirectory()) {
                        currentProject = selectedFile.getParent();
                    }else{
                        currentProject = selectedFile.getAbsolutePath();
                    }
                    openProject(currentProject);
                }
                ConfigAction.setEnabled(true);
                updateStatusBar();
            }
        };

        ConfigAction = new AbstractAction("Config", new ImageIcon("icons/config.gif")) {
            public void actionPerformed(ActionEvent e) {
                configForm.showDialog(project.getConfig(), new UpdateProjectConfig());
            }
        };

        QuitAction = new AbstractAction("Quit") {
            public void actionPerformed(ActionEvent e) {
                saveOld();
                System.exit(0);
            }
        };

        m = area.getActionMap();
        CutAction = m.get(DefaultEditorKit.cutAction);
        CopyAction = m.get(DefaultEditorKit.copyAction);
        PasteAction = m.get(DefaultEditorKit.pasteAction);

        AssembleAction = new AbstractAction("Assemble") {
            public void actionPerformed(ActionEvent e) {
                // ProgramParser pparser = new ProgramParser();
                Program program = new Program(sideBarLeftPanel.currentOpenedFile, true);
                try {
                    program.assemble();
                }catch (Exception exception){
                    statusBarPanel.setText(exception.getMessage());
                }
            }
        };

        // Menu bar
        JMenuBar JMB = new JMenuBar();
        setJMenuBar(JMB);
        JMenu projectMenu = new JMenu("Project");
        JMenu fileMenu = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMB.add(projectMenu);
        JMB.add(fileMenu);
        JMB.add(edit);

        projectMenu.add(NewAction);
        projectMenu.add(OpenAction);
        projectMenu.add(QuitAction);

        fileMenu.add(AssembleAction);

        JMenuItem ConfigMenuItem = new JMenuItem();
        ConfigMenuItem.setAction(ConfigAction);
        ConfigMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        projectMenu.add(ConfigMenuItem);

        projectMenu.addSeparator();

        for(int i=0; i<4; i++)
            projectMenu.getItem(i).setIcon(null);

        edit.add(CutAction);
        edit.add(CopyAction);
        edit.add(PasteAction);

        edit.getItem(0).setText("Cut out");
        edit.getItem(1).setText("Copy");
        edit.getItem(2).setText("Paste");

        JToolBar tool = new JToolBar();
        add(tool, BorderLayout.NORTH);
        tool.add(NewAction);
        tool.add(OpenAction);
        tool.addSeparator();

        JButton cut = tool.add(CutAction), cop = tool.add(CopyAction),pas = tool.add(PasteAction);

        cut.setText(null); cut.setIcon(new ImageIcon("icons/cut.gif"));
        cop.setText(null); cop.setIcon(new ImageIcon("icons/copy.gif"));
        pas.setText(null); pas.setIcon(new ImageIcon("icons/paste.gif"));

        ConfigAction.setEnabled(false);
    }
    
    private KeyListener k1 = new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
        changed = true;
        //ConfigAction.setEnabled(true);
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
        }
        catch(IOException e) {
        }
    }
    
    private void updateStatusBar() {
        statusBarPanel.setText(currentFile);
    }

    private class UpdateProjectConfig implements CallbackRunnable {

        public void run() {

            ProjectConfiguration config = project.getConfig();

            setTitle(config.getName());

        }

    }
}
