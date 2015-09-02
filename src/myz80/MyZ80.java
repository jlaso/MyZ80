/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myz80;

import javax.swing.UIManager;

/**
 *
 * @author joseluislaso
 */
public class MyZ80 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        UIManager.put("EditTextAreaUI", "jEditSyntax.EditTextAreaUI");
        
        AppConfiguration appConfig = new AppConfiguration();
        
        new TextEditor(appConfig);   
        
        appConfig.save();
        
        /*
        JFrame fm = new JFrame();
        
        JEditTextArea jta = new JEditTextArea(20,100, fm); 
        jta.setTokenMarker(new ASMZ80TokenMarker());
        jta.readInFile("samples/normals.asm");
        jta.recalculateVisibleLines();
        jta.setFirstLine(0);
        jta.setElectricScroll(0);
        jta.getPainter().setSelectionColor(UIManager.getColor("TextArea.selectionBackground"));
        SyntaxStyle[] styles = SyntaxUtilities.getDefaultSyntaxStyles();
        styles[Token.COMMENT1] = new SyntaxStyle(Color.GRAY,true,false);
        //styles[Token.KEYWORD1] = new SyntaxStyle(new Color(0x000080),false,true);
        //styles[Token.KEYWORD2] = new SyntaxStyle(new Color(0x000080),false,true);
        //styles[Token.KEYWORD3] = new SyntaxStyle(new Color(0x000080),false,true);
        styles[Token.LITERAL1] = new SyntaxStyle(new Color(0x008000),false,true);
        styles[Token.LITERAL2] = new SyntaxStyle(new Color(0x000080),false,true);

        jta.getPainter().setStyles(styles);
        
        fm.add(jta);
        fm.setDefaultCloseOperation(EXIT_ON_CLOSE);
        fm.pack();
        fm.setVisible(true);
*/
    }
    
}
