
package myz80;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author joseluislaso
 */
public class SideBarRightPanel extends JPanel{
    
    public SideBarRightPanel(){
     
        super();
        
        setSize(200, 400);
        setLayout(new BorderLayout());        
        
        String[] columnNames = { "Register", "Hi", "Lo" };
        
        Object[][] data = {
            { "A", "0x00", "" },
            { "BC", "0x00", "0x00" }
        };
        
        JTable table = new JTable(data, columnNames);
        table.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        table.setGridColor(Color.GRAY);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        add(table.getTableHeader(), BorderLayout.PAGE_START);
        add(table, BorderLayout.CENTER);

        // Footer of this sidebar
        add(new Button("South"), BorderLayout.SOUTH);

            
    }           
    
}
