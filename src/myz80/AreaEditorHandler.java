package MyZ80.myz80;

import MyZ80.jEditSyntax.JEditTextArea;

/**
 * Created by joseluislaso on 02/09/15.
 */
public class AreaEditorHandler {

    private JEditTextArea area;

    public AreaEditorHandler(JEditTextArea area) {

        this.area = area;

    }

    public Boolean loadFile(String file) {

        area.readInFile(file);
        return true;
    }
}
