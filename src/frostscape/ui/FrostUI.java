package frostscape.ui;

import frostscape.ui.dialog.FamilyDescriptionDialog;

/** Contains several dialogs used within the mod **/
public class FrostUI {
    public static FamilyDescriptionDialog family;

    public static void load(){
        family = new FamilyDescriptionDialog("@family");
    }
}
