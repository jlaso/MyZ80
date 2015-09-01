
package myz80;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author joseluislaso
 */
public class SideBarLeftPanel extends JPanel implements MouseListener {
    
    JTree tree;
    DefaultMutableTreeNode root;

//    MouseListener ml = new MouseAdapter() {
//        public void mousePressed(MouseEvent e) {
//            int selRow = tree.getRowForLocation(e.getX(), e.getY());
//            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
//            if(selRow != -1) {
//                if(e.getClickCount() == 1) {
//                    mySingleClick(selRow, selPath);
//                }
//                else if(e.getClickCount() == 2) {
//                    myDoubleClick(selRow, selPath);
//                }
//            }
//        }
//    };
//        
  
    public SideBarLeftPanel(){
     
        super();
        setSize(200, 400);
        setLayout(new BorderLayout());
        
        add(new Button("North"), BorderLayout.NORTH);
        add(new Button("South"), BorderLayout.SOUTH);
        add(new Button("East"), BorderLayout.EAST);
        add(new Button("West"), BorderLayout.WEST);
        add(new Button("Center"), BorderLayout.CENTER);

        root = new DefaultMutableTreeNode("root", true);
        getList(root, new File(System.getProperty("user.dir")));
        setLayout(new BorderLayout());
        tree = new JTree(root);
        tree.setRootVisible(false);
        add(new JScrollPane((JTree)tree),"Center");
                
        tree.addMouseListener(ml);
    }

    public Dimension getPreferredSize(){
        return new Dimension(200, 120);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                System.out.println(path.getLastPathComponent().toString());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    
    public void getList(DefaultMutableTreeNode node, File f) {
        if(!f.isDirectory()) {
            if (f.getName().endsWith("asm")) {
               DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
               node.add(child);
            }
        } else {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
            node.add(child);
            File fList[] = f.listFiles();
            for(int i = 0; i  < fList.length; i++)
                getList(child, fList[i]);
        }
    }
  

    
}
