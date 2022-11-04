package frostscape;

import arc.*;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Cell;
import arc.struct.Seq;
import arc.util.*;
import frostscape.game.ScriptedSectorHandler;
import frostscape.graphics.FrostShaders;
import frostscape.mods.Compatibility;
import frostscape.ui.FrostUI;
import frostscape.ui.overlay.SelectOverlay;
import frostscape.util.UIUtils;
import frostscape.world.environment.FloorDataHandler;
import frostscape.world.light.LightBeams;
import frostscape.world.meta.Family;
import frostscape.world.research.ResearchHandler;
import frostscape.world.upgrades.UpgradeHandler;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.mod.*;
import mindustry.ui.dialogs.BaseDialog;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static mindustry.Vars.ui;

public class Frostscape extends Mod{

    public static NativeJavaPackage p = null;

    public static final String NAME = "hollow-frostscape";
    public static final float VERSION = 136.1f;
    public static String VERSION_NAME = "", LAST_VERSION_NAME = "";
    public static ScriptedSectorHandler sectors = new ScriptedSectorHandler();
    public static FloorDataHandler floors = new FloorDataHandler();
    public static ResearchHandler research = new ResearchHandler();

    public static UpgradeHandler upgrades = new UpgradeHandler();

    public static SelectOverlay selection = new SelectOverlay();

    public static LightBeams lights = new LightBeams();

    public Frostscape(){

        Events.on(FileTreeInitEvent.class, e -> {
            Core.app.post(FrostShaders::load);
        });

        Events.on(EventType.ClientLoadEvent.class,
                e -> {
                    loadSettings();
                    Family.all.each(Family::load);
                    VERSION_NAME = Vars.mods.getMod(NAME).meta.version;
                    LAST_VERSION_NAME = Core.settings.getString(NAME + "-last-version", "0.0");
                    if(!LAST_VERSION_NAME.equals(VERSION_NAME)) Time.runTask(10f, () -> {
                        BaseDialog dialog = new BaseDialog("phrog");
                        Image warning = new Image(Icon.warning);
                        Cell<Image> cell = dialog.cont.add(warning);
                        cell.pad(20f).row();

                        //Ok look I was lazy
                        AtomicBoolean warned = new AtomicBoolean(false);
                        AtomicReference<Float> rotationSpeed = new AtomicReference<>((float) 0);
                        warning.clicked(() -> {
                            Sounds.wind3.play();
                            warning.color.set(Mathf.random(1), Mathf.random(1), Mathf.random(1));
                            cell.width(30 + Mathf.random(69));
                            cell.height(30 + Mathf.random(69));
                            cell.expand();
                            warned.set(true);
                            rotationSpeed.set((rotationSpeed.get() + 1));
                            Core.settings.put(NAME + "-farted", true);
                        });

                        warning.update(() -> {
                            if (warned.get()) warning.rotation += rotationSpeed.get();
                        });

                        dialog.cont.add("[red]WARNING").padTop(50).row();
                        dialog.cont.add("[#dde6f0]Welcome to the Frostscape[] is still wip, proceed at your own [red]risk").row();
                        dialog.cont.add("[lightgray]Your last run version was [red]" + LAST_VERSION_NAME + "[],\n The mod has been updated to it's [cyan]" + VERSION_NAME).row();
                        dialog.cont.button("Understood, pushing on!", () -> {
                            dialog.hide();
                            Core.settings.put(NAME + "-last-version", VERSION_NAME);
                            Core.settings.put(NAME + "-re-installations", Core.settings.getInt(NAME + "-re-installations", 0) + 1);
                        }).size(300f, 50f);
                        dialog.show();
                    });
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

        Vars.mods.getScripts().runConsole(
                "function buildWorldP(){return Vars.world.buildWorld(Vars.player.x, Vars.player.y)}");
        ImporterTopLevel scope = (ImporterTopLevel) Vars.mods.getScripts().scope;

        Seq<String> packages = Seq.with(
                "frostscape",
                "frostscape.content",
                "frostscape.game",
                "frostscape.graphics",
                "frostscape.math",
                "frostscape.ui",
                "frostscape.util",
                "frostscape.world",
                "frostscape.world.light",
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
