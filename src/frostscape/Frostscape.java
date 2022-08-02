package frostscape;

import arc.*;
import arc.struct.Seq;
import arc.util.*;
import frostscape.game.ScriptedSector;
import frostscape.game.ScriptedSectorHandler;
import frostscape.ui.FrostUI;
import frostscape.util.UIUtils;
import frostscape.world.meta.Family;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;

public class Frostscape extends Mod{

    public static NativeJavaPackage p = null;

    public static final String NAME = "hollow-frostscape";
    public static final float VERSION = 136.1f;
    public static ScriptedSectorHandler sectors = new ScriptedSectorHandler();

    public Frostscape(){

        Events.on(EventType.ClientLoadEvent.class,
                e -> {
                    Family.all.each(f -> f.load());
                }
        );

        Events.on(EventType.ContentInitEvent.class, e -> {

        });
    }

    @Override
    public void init() {
        ImporterTopLevel scope = (ImporterTopLevel) Vars.mods.getScripts().scope;

        Seq<String> packages = Seq.with(
                "frostscape",
                "frostscape.content",
                "frostscape.ui",
                "frostscape.game"
        );

        packages.each(name -> {
            p = new NativeJavaPackage(name, Vars.mods.mainLoader());

            p.setParentScope(scope);

            scope.importPackage(p);
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
