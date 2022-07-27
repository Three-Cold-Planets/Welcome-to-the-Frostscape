package frostscape;

import arc.*;
import arc.util.*;
import frostscape.ui.FrostUI;
import frostscape.util.UIUtils;
import frostscape.world.meta.Family;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.type.UnitType;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;

public class Main extends Mod{

    public static final String NAME = "hollow-frostscape";
    public static final float VERSION = 136;
    public Main(){

        Events.on(EventType.ClientLoadEvent.class,
                e -> {
                    Family.all.each(f -> f.load());
                }
        );

        Events.on(EventType.ContentInitEvent.class, e -> {

        });
    }


    public void loadContent(){
        float current = Time.millis();
        FrostContentLoader.load();
        Log.format("Loaded Frostscape content in. {0}", (Time.millis() - current));

        Events.run(ClientLoadEvent.class, () -> {
            float current1 = Time.millis();
            FrostUI.load();
            UIUtils.loadAdditions();
            Log.format("Loaded Frostscape ui in. {0}", (Time.millis() - current1));
        });


    }

}
