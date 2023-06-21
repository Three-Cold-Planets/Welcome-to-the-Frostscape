package main.ui.frag;

import main.world.meta.LoreNote;
import mindustry.ui.dialogs.BaseDialog;

public class NoteInfoDialog extends BaseDialog {
    public NoteInfoDialog(String title) {
        super(title);
        addCloseButton();
    }

    public void show(LoreNote note){

    }
}
