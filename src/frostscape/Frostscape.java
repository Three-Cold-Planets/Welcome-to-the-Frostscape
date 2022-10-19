package frostscape;

import arc.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Shape2D;
import arc.struct.Seq;
import arc.util.*;
import frostscape.entities.effect.DataEffect;
import frostscape.entities.effect.FrostEffect;
import frostscape.game.ScriptedSectorHandler;
import frostscape.mods.Compatibility;
import frostscape.ui.FrostUI;
import frostscape.ui.overlay.SelectOverlay;
import frostscape.util.UIUtils;
import frostscape.world.environment.FloorDataHandler;
import frostscape.world.light.LightBeams;
import frostscape.world.light.LightBeams.ColorData;
import frostscape.world.light.LightBeams.LightSource;
import frostscape.world.light.Lightc;
import frostscape.world.meta.Family;
import frostscape.world.research.ResearchHandler;
import frostscape.world.upgrades.UpgradeHandler;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.gen.EffectState;
import mindustry.graphics.Layer;
import mindustry.mod.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;

import static mindustry.Vars.ui;

public class Frostscape extends Mod{

    public static NativeJavaPackage p = null;

    public static final String NAME = "hollow-frostscape";
    public static final float VERSION = 136.1f;
    public static ScriptedSectorHandler sectors = new ScriptedSectorHandler();
    public static FloorDataHandler floors = new FloorDataHandler();
    public static ResearchHandler research = new ResearchHandler();

    public static UpgradeHandler upgrades = new UpgradeHandler();

    public static SelectOverlay selection = new SelectOverlay();

    public static LightBeams lights = new LightBeams();

    public Frostscape(){

        Events.on(EventType.ClientLoadEvent.class,
                e -> {
                    loadSettings();
                    Family.all.each(Family::load);
                }
        );

        Events.on(EventType.ContentInitEvent.class, e -> {

        });

        Events.run(EventType.SaveWriteEvent.class, () -> {
            lights.lights.clear();
        });

        Events.run(Trigger.update, () -> {
            lights.updateBeams();
            if(!Vars.state.isPlaying()) return;
            selection.update();
        });

        Events.run(Trigger.draw, () -> {
            Draw.draw(Layer.overlayUI, selection::drawSelect);
            Draw.draw(Layer.light + 1, lights::draw);
        });
    }

    @Override
    public void init() {
        ImporterTopLevel scope = (ImporterTopLevel) Vars.mods.getScripts().scope;

        Seq<String> packages = Seq.with(
                "frostscape",
                "frostscape.content",
                "frostscape.game",
                "frostscape.math",
                "frostscape.ui",
                "frostscape.util",
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

            current1 = Time.millis();
            //Run after all content has loaded
            Compatibility.handle();
            Log.info(String.format("Loaded Frostscape compat in: %s", (Time.millis() - current1)));
        });
    }

    void loadSettings(){
        ui.settings.addCategory(Core.bundle.get("settings.frostscape-title"), NAME + "-hunter", t -> {
            t.sliderPref(Core.bundle.get("frostscape-parallax"), 100, 1, 100, 1, s -> s + "%");
            t.sliderPref(Core.bundle.get("frostscape-wind-visual-force"), 100, 0, 800, 1, s -> s + "%");
        });
    }
}
