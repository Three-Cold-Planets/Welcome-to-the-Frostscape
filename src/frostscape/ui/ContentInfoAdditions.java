package frostscape.ui;

import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.editor.MapInfoDialog;
import mindustry.editor.MapObjectivesDialog;

import static frostscape.Frostscape.NAME;

@Deprecated
public class ContentInfoAdditions {
    public static void load(){
        //TODO: Remove this class's implementation
        if(true) return;
        Vars.ui.content.shown(() -> {
            Table info = (((Table) ((ScrollPane) (Vars.ui.content.cont.getChildren().get(0))).getWidget()).getCells().get(0).getTable());
            StringBuilder text = ((Label) ((Table) (info.getChildren().get(0))).getChildren().get(1)).getText();
            String name = text.substring(text.length()/2+8, text.length());
            if(!name.contains(NAME)) return;
        });
    }
}
