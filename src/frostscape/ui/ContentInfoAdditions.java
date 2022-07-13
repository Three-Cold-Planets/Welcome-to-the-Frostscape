package frostscape.ui;

import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ctype.ContentType;

import static frostscape.Main.NAME;

public class ContentInfoAdditions {
    public static void load(){
        Vars.ui.content.shown(() -> {
            Table info = (((Table) ((ScrollPane) (Vars.ui.content.cont.getChildren().get(0))).getWidget()).getCells().get(0).getTable());
            StringBuilder text = ((Label) ((Table) (info.getChildren().get(0))).getChildren().get(1)).getText();
            String name = text.substring(text.length()/2+8, text.length());
            if(!name.contains(NAME)) return;
            Log.info(Vars.content.getByName(ContentType.unit, name).name);
            Log.info("hi! I showed the content " + name);
        });
    }
}
