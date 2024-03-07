package main;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Cell;
import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Structs;
import arc.util.Time;
import main.entities.comp.HeatComp;
import main.game.ScriptedSectorHandler;
import main.graphics.FrostShaders;
import main.graphics.ModPal;
import main.mods.Compatibility;
import main.type.HollusUnitType.LoadableEngine;
import main.ui.FrostUI;
import main.ui.ModTex;
import main.ui.overlay.ScanningOverlay;
import main.ui.overlay.SelectOverlay;
import main.util.UIUtils;
import main.util.WeatherUtils;
import main.world.meta.Family;
import main.world.meta.LoreNote;
import main.world.systems.bank.ResourceBankHandler;
import main.world.systems.heat.HeatSetup;
import main.world.systems.heat.HeatControl;
import main.world.systems.heat.TileHeatOverlay;
import main.world.systems.light.LightBeams;
import main.world.systems.research.ResearchHandler;
import main.world.systems.upgrades.Upgrade;
import main.world.systems.upgrades.UpgradeHandler;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.io.SaveVersion;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.ui.dialogs.BaseDialog;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static mindustry.Vars.ui;

public class Frostscape extends Mod{

    public static boolean photosensitiveMode;

    public static NativeJavaPackage p = null;

    public static final String NAME = "hollow-frostscape";
    public static Mods.LoadedMod MOD;
    public static final float VERSION = 136.1f;
    public static String VERSION_NAME = "", LAST_VERSION_NAME = "";
    public static ScriptedSectorHandler sectors = new ScriptedSectorHandler();
    public static SelectOverlay selection = new SelectOverlay();
    public static ScanningOverlay scan = new ScanningOverlay();
    public static TileHeatOverlay heatOverlay;

    public Frostscape(){

        Color.cyan.set(ModPal.pulseChargeEnd);
        Color.sky.set(ModPal.pulseChargeStart);

        Events.on(FileTreeInitEvent.class, e -> {
            Core.app.post(FrostShaders::load);
            MOD = Vars.mods.getMod(NAME);
        });

        Events.on(ClientLoadEvent.class,
                e -> {
                    loadSettings();
                    LoreNote.all.each(LoreNote::load);
                    Family.all.each(Family::load);
                    LoadableEngine.engines.each(LoadableEngine::load);

                    VERSION_NAME = MOD.meta.version;
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

        Events.run(ContentInitEvent.class, this::loadSplash);

        Events.run(WinEvent.class, this::loadSplash);

        HeatControl heat = HeatControl.get();

        //Most of theese are singletons for the sake of being able to port these over to the Arctic-Insurrection mod more easly.
        SaveVersion.addCustomChunk("upgrade-handler", UpgradeHandler.get());
        SaveVersion.addCustomChunk("research-handler", ResearchHandler.get());
        //Note: Do NOT change the name to tile-heat-control. Please.
        //SaveVersion.addCustomChunk("tile-heat", heat);
        SaveVersion.addCustomChunk("light-beams", LightBeams.get());
        SaveVersion.addCustomChunk("resource-bank", ResourceBankHandler.get());

        heat.setup = new HeatSetup();

        heat.setup.initialize(heat);

        heatOverlay = new TileHeatOverlay();

        Events.run(EventType.WorldLoadEvent.class, () -> {
            if(!Vars.state.isEditor()) heat.start(Vars.world.width(), Vars.world.height());
        });

        Events.on(StateChangeEvent.class, e -> {
            if(e.from == GameState.State.playing && e.to == GameState.State.menu) LightBeams.get().lights.clear();
        });

        Events.run(Trigger.update, () -> {
            scan.update();
            selection.update();

            if(!Vars.state.isPlaying()) return;
            LightBeams.get().updateBeams();
            ResourceBankHandler.power.graph.update();
            ResourceBankHandler.liquids.updateFlow();

            WeatherUtils.updateWind();
        });

        Events.run(Trigger.draw, () -> {
            Draw.draw(Layer.overlayUI, selection::drawSelect);
            Draw.draw(Layer.overlayUI, scan::draw);
            Draw.draw(Layer.buildBeam, scan::drawScan);
            Draw.draw(Layer.light + 1, LightBeams.get()::draw);
        });

        Events.run(EventType.WorldLoadEndEvent.class, () -> {
            ResourceBankHandler.setup();
        });

        Events.run(EventType.ClientLoadEvent.class, () -> {
            UpgradeHandler.upgrades.each(Upgrade::load);
            ModTex.load(NAME);
            ResourceBankHandler.init();
        });
    }

    @Override
    public void init() {

        //Import classes manually into Console.S
        //Todo: Automatically import classes into the js console
        Vars.mods.getScripts().runConsole(
                "function buildWorldP(){return Vars.world.buildWorld(Vars.player.x, Vars.player.y)}");
        ImporterTopLevel scope = (ImporterTopLevel) Vars.mods.getScripts().scope;

        Seq<String> packages = Seq.with(
                "main",
                "main.content",
                "main.game",
                "main.graphics",
                "main.math",
                "main.mods",
                "main.ui",
                "main.util",
                "main.world",
                "main.world.systems.light",
                "main.world.systems.heat",
                "main.world.systems.upgrades",
                "main.world.systems.research",
                "main.world.systems.bank"
        );

        packages.each(name -> {

            p = new NativeJavaPackage(name, Vars.mods.mainLoader());

            p.setParentScope(scope);

            scope.importPackage(p);
        });
    }

    public void loadContent(){
        long current = Time.millis();
        FrostContentLoader.load();
        final float time = Time.timeSinceMillis(current);

        Events.run(ClientLoadEvent.class, () -> {

            //Log content loading time in ClientLoadEvent
            Log.info(String.format("Loaded Frostscape content in: %s", time));

            long current1 = Time.millis();
            FrostUI.load();
            UIUtils.loadAdditions();

            Log.info(String.format("Loaded Frostscape ui in: %s", (Time.timeSinceMillis(current1))));

            current1 = Time.millis();
            //Run after all content has loaded
            Compatibility.handle();
            Log.info(String.format("Loaded Frostscape compat in: %s", (Time.timeSinceMillis(current1))));
        });
    }

    void loadSettings(){
        heatOverlay.enabled = Core.settings.getBool("settings.frostscape-heat-overlay", false);
        ui.settings.addCategory(Core.bundle.get("settings.frostscape-title"), NAME + "-hunter", t -> {
            t.sliderPref(Core.bundle.get("settings.frostscape-parallax"), 100, 1, 100, 1, s -> s + "%");
            t.sliderPref(Core.bundle.get("settings.frostscape-wind-visual-force"), 1, 0, 8, 1, s -> s * 100 + "%");
            t.checkPref(Core.bundle.get("settings.frostscape-flashing-lights-safety"), false, b -> {
                photosensitiveMode = b;
            });
            t.row();
            t.add(Core.bundle.get("settings.frostscape.flashingwarning")).wrap().left().growX().padTop(3);
            t.checkPref(Core.bundle.get("settings.frostscape-heat-overlay"), false, b -> {
                heatOverlay.enabled = b;
            });
        });
    }

    void loadSplash(){

        MOD.meta.subtitle = null;

        ObjectFloatMap<String> categories = getCategories(NAME + ".splash.chances");

        categories.each(e -> {
            if(!(MOD.meta.subtitle == null)) return;

            boolean showing = Mathf.chance(e.value);
            if(showing){
                String[] subtitles = getEntries(NAME + ".splash." + e.key);
                MOD.meta.subtitle = subtitles[Mathf.random((int)Mathf.maxZero(subtitles.length - 1))];
            }
        });

        if(!(MOD.meta.subtitle == null)) return;

        String[] subtitles = getEntries(NAME + ".splash.default");
        MOD.meta.subtitle = subtitles[Mathf.random((int)Mathf.maxZero(subtitles.length - 1))];
    }

    String[] getEntries(String key){
        String packed = Core.bundle.get(key);
        int end = 0;
        String[] list = new String[]{};
        for (int i = 0; i < packed.length(); i++) {
            if(packed.charAt(i) == '|' && packed.charAt(i - 1) != '\\') {
                String entry = packed.substring(end, i);
                list = Structs.add(list, entry);
                end = i + 1;
            }
        }
        String subtitle = packed.substring(end);
        return Structs.add(list, subtitle);
    }

    ObjectFloatMap<String> getCategories(String key){
        String packed = Core.bundle.get(key);
        int end = 0;
        ObjectFloatMap<String> categories = new ObjectFloatMap<>();
        String lastName = "";
        boolean name = true;
        for (int i = 0; i < packed.length(); i++) {
            if(packed.charAt(i) == '|' && packed.charAt(i - 1) != '\\') {
                String entry = packed.substring(end, i);
                if(name) {
                    lastName = entry;
                }
                else categories.put(lastName, Strings.parseFloat(entry));

                end = i + 1;

                name = !name;
            }
        }
        String ending = packed.substring(end);
        categories.put(lastName, Strings.parseFloat(ending));
        return categories;
    }
}
