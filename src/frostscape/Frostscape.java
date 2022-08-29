package frostscape;

import arc.*;
import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import arc.util.*;
import frostscape.game.ScriptedSectorHandler;
import frostscape.type.upgrade.Upgrade;
import frostscape.ui.FrostUI;
import frostscape.ui.overlay.SelectOverlay;
import frostscape.util.UIUtils;
import frostscape.world.environment.FloorDataHandler;
import frostscape.world.meta.Family;
import frostscape.world.research.ResearchHandler;
import frostscape.world.upgrades.UpgradeHandler;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.graphics.Layer;
import mindustry.mod.*;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;

public class Frostscape extends Mod{

    public static NativeJavaPackage p = null;

    public static final String NAME = "hollow-frostscape";
    public static final float VERSION = 136.1f;
    public static ScriptedSectorHandler sectors = new ScriptedSectorHandler();
    public static FloorDataHandler floors = new FloorDataHandler();
    public static ResearchHandler research = new ResearchHandler();

    //Initialized during adding processes to the async core
    public static SelectOverlay selection = new SelectOverlay();

    public Frostscape(){

        Events.on(EventType.ClientLoadEvent.class,
                e -> {
                    Family.all.each(Family::load);
                    UpgradeHandler.upgrades.each(Upgrade::initialiseDeltas);
                }
        );

        Events.on(EventType.ContentInitEvent.class, e -> {

        });

        Events.run(Trigger.update, () -> {
            if(!Vars.state.isPlaying()) return;
            selection.update();
        });

        Events.run(Trigger.draw, () -> {
            Draw.draw(Layer.overlayUI, selection::drawSelect);
        });
    }

    @Override
    public void init() {
        ImporterTopLevel scope = (ImporterTopLevel) Vars.mods.getScripts().scope;

        Seq<String> packages = Seq.with(
                "frostscape",
                "frostscape.content",
                "frostscape.math",
                "frostscape.ui",
                "frostscape.game",
                "frostscape.world",
                "frostscape.world.upgrades"
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
        final float time = Time.millis() - current;

        Events.run(ClientLoadEvent.class, () -> {
            //Log content loading time in ClientLoadEvent
            Log.info(String.format("Loaded Frostscape content in: %s", time));

            float current1 = Time.millis();
            FrostUI.load();
            UIUtils.loadAdditions();

            Log.info(String.format("Loaded Frostscape ui in: %s", (Time.millis() - current1)));
        });
    }

}
