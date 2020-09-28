
package MyZ80.myz80;

import sun.jvm.hotspot.debugger.cdbg.Sym;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author joseluislaso
 */
public class SideBarLeftPanel extends JPanel {
    
    JTree tree;
    DefaultMutableTreeNode root;
    AreaEditorHandler areaEditorHandler;
    Project project = null;
    JScrollPane scrollPane;
    String currentOpenedFile;

    public SideBarLeftPanel(AreaEditorHandler areaEditorHdl){
     
        super();
        setSize(200, 400);
        setLayout(new BorderLayout());
        areaEditorHandler = areaEditorHdl;

        root = new DefaultMutableTreeNode("root", true);

        setLayout(new BorderLayout());
        tree = new JTree(root);
        tree.setRootVisible(false);
        scrollPane = new JScrollPane((JTree)tree);
        add(scrollPane,"Center");

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if (project==null) return;

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                /* if nothing is selected */
                if (node == null) return;

                /* retrieve the node that was selected */
                TreeNodeObj nodeInfo = (TreeNodeObj)node.getUserObject();

                System.out.println(nodeInfo.getName());
                /* React to the node selection. */

                if (!nodeInfo.getIsDir()) {
                    currentOpenedFile = nodeInfo.getFullName();
                    areaEditorHandler.loadFile(currentOpenedFile);
                }
            }
        });
    }

    public void loadProject(Project prj) {

        root.removeAllChildren();

        project = prj;
        getList(root, new File(project.getPath()));

        ((DefaultTreeModel) tree.getModel()).reload(root);

    }

    public Dimension getPreferredSize(){

        return new Dimension(200, 120);

    }
    
    public void getList(DefaultMutableTreeNode node, File f) {
        if(!f.isDirectory()) {
            if (f.getName().endsWith("asm")) {
               DefaultMutableTreeNode child = new DefaultMutableTreeNode(new TreeNodeObj(false, f.getName(), f.getAbsolutePath()));
               node.add(child);
            }
        } else {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(new TreeNodeObj(true, f.getName(), null));
            node.add(child);
            File fList[] = f.listFiles();
            for(int i = 0; i  < fList.length; i++)
                getList(child, fList[i]);
        }
    }
  

    private class TreeNodeObj {

        protected Boolean isDir;
        protected String name;
        protected String fullName;

        public TreeNodeObj(Boolean isDir, String name, String fullName) {
            this.isDir = isDir;
            this.name = name;
            this.fullName = fullName;
        }

        public Boolean getIsDir() {
            return isDir;
        }

        public String getName() {
            return name;
        }

        public String getFullName() {
            return fullName;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
}
