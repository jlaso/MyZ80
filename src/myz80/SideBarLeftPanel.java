
package myz80;

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
  
    public SideBarLeftPanel(AreaEditorHandler areaEditorHdl){
     
        super();
        setSize(200, 400);
        setLayout(new BorderLayout());
        areaEditorHandler = areaEditorHdl;

        root = new DefaultMutableTreeNode("root", true);
        //getList(root, new File(System.getProperty("user.dir")));
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
                    areaEditorHandler.loadFile(project.getPath() + "/" + nodeInfo.getName());
                }
            }
        });
    }

    public void loadProject(Project prj) {

        scrollPane.remove(tree);
        tree.remove(root);
        project = prj;
        getList(root, new File(project.getPath()));
        //tree.repaint();
        scrollPane.add(tree);
        repaint();
        doLayout();

    }

    public Dimension getPreferredSize(){

        return new Dimension(200, 120);

    }
    
    public void getList(DefaultMutableTreeNode node, File f) {
        if(!f.isDirectory()) {
            if (f.getName().endsWith("asm")) {
               DefaultMutableTreeNode child = new DefaultMutableTreeNode(new TreeNodeObj(false, f.getName()));
               node.add(child);
            }
        } else {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(new TreeNodeObj(true, f.getName()));
            node.add(child);
            File fList[] = f.listFiles();
            for(int i = 0; i  < fList.length; i++)
                getList(child, fList[i]);
        }
    }
  

    private class TreeNodeObj {

        protected Boolean isDir;
        protected String name;

        public TreeNodeObj(Boolean isDir, String name) {
            this.isDir = isDir;
            this.name = name;
        }

        public Boolean getIsDir() {
            return isDir;
        }

        public String getName() {
            return name;
        }
    }
    
}
