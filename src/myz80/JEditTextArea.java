/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myz80;

import javax.swing.JTextArea;
import jEditSyntax.inputHandler.InputHandler;
import jEditSyntax.marker.TokenMarker;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author joseluislaso
 */
public class JEditTextArea extends JTextArea {
    
    public static String LEFT_OF_SCROLLBAR = "los";
    protected static jEditSyntax.JEditTextArea focusedComponent;
    protected static Timer caretTimer;

    public JEditTextArea(int rows, int columns){
        this(rows, columns, "sample text");
    }
    
    public JEditTextArea(int rows, int columns, String title){
        super(null, title, rows, columns);
    }
    
    static class CaretBlinker implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            if(focusedComponent != null
                    && focusedComponent.hasFocus())
                    focusedComponent.blinkCaret();
        }
    }
        
    static
    {
        caretTimer = new Timer(500,new CaretBlinker());
        caretTimer.setInitialDelay(500);
        caretTimer.start();
    }
}
