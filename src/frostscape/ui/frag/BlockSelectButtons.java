package frostscape.ui.frag;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Button;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import frostscape.type.upgrade.Upgrade;
import frostscape.type.upgrade.UpgradeableBuilding;
import frostscape.ui.FrostUI;
import frostscape.ui.frag.BlockSelectFrag.*;
import frostscape.world.upgrades.UpgradeEntry;
import frostscape.world.upgrades.UpgradeState;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

import static frostscape.ui.frag.BlockSelectFrag.buttons;

public class BlockSelectButtons {
    private static int i = 0, currentLevel = 0, maxLevel = 0;
    public static Upgrade currentUpgrade;
    private static boolean isValid = false;
    public static Table info = new Table(), costs = new Table(), list = new Table(), topBar = new Table(), upgradeList = new Table();
    public static ObjectMap<Upgrade, ObjectMap<UpgradeEntry, Seq<UpgradeState>>> currentMap = new ObjectMap<>();
    public static ObjectMap<UpgradeEntry, Seq<UpgradeState>> currentStates = new ObjectMap<>(), currentStatesClone = new ObjectMap<>();

    public static void setup(){

        //Add exit button
        buttons.add(new SelectButton("Exit", Icon.left, false, (b) -> true, (t, builds) -> FrostUI.select.hideConfig()));
        //Enable/Disable
        buttons.add(new SelectButton("Disable", Icon.cancel, false, (b) -> true, (t, builds) -> builds.each(b -> b.enabled = false)));
        buttons.add(new SelectButton("Enable", Icon.play, false, (b) -> true, (t, builds) -> builds.each(b -> b.enabled = true)));
        //Upgrades tables
        int curLevel = 0;
        BlockSelectFrag.SelectButton upgrades = new BlockSelectFrag.SelectButton("Upgrades", Icon.hammer, true, (b) -> b.find(build -> build instanceof UpgradeableBuilding) != null, (table, builds) -> {
            table.clear();
            info.clear();
            currentMap.clear();
            currentStates.clear();
            currentStatesClone.clear();
            table.background(Styles.black6);
            table.setOrigin(Align.top);
            table.setSize(160, 800);
            Seq<UpgradeableBuilding> buildings = new Seq<>();
            builds.each(b -> {
                if(b instanceof UpgradeableBuilding) {
                    UpgradeableBuilding building = (UpgradeableBuilding) b;
                    buildings.add(building);
                    building.type().entries().each(entry -> {
                        if(!currentMap.containsKey(entry.upgrade)) currentMap.put(entry.upgrade, new ObjectMap<>());
                        if(!currentMap.get(entry.upgrade).containsKey(entry)) currentMap.get(entry.upgrade).put(entry, new Seq<>());
                        UpgradeState state = building.upgrades().getState(entry.upgrade);
                        currentMap.get(entry.upgrade).get(entry).add(state);
                    });
                }
            });
            table.table(select -> {
                select.pane(upgradeList);
            }).width(200).height(400);
            rebuildBuildings();

            table.image().height(400).width(10);
            table.add(info).width(400).height(400);
        });
        buttons.add(upgrades);
    }

    public static void rebuildBuildings(){
        upgradeList.clear();
        upgradeList.image().fillX().height(5).padTop(3).top();
        upgradeList.row();
        upgradeList.table(t -> {
            i = 0;
            currentMap.each((upgrade, arr) -> {
                ClickListener listener = new ClickListener();
                Image image = new Image(upgrade.region);
                image.addListener(listener);
                image.update(() -> image.color.lerp(!listener.isOver() ? Color.lightGray : Color.white, Mathf.clamp(0.4f * Time.delta)));
                image.addListener(new HandCursorListener());
                image.clicked(() -> {
                    currentUpgrade = upgrade;
                    currentLevel = 0;
                    rebuildInfo(upgrade, currentLevel);
                });

                t.add(image).size(40).pad(2);
                if(i++ % 4 == 3){
                    i = 0;
                    t.row();
                }
            });
            if(i % 4 != 3){
                for (int j = 0; j < 4 - i; j++) {
                    t.image().color(Color.clear).size(40).pad(2);
                }
            }
        }).top();
        upgradeList.row();
        upgradeList.image().fillX().height(5).padBottom(3).top();
    }

    public static void rebuildInfo(Upgrade u, int currentLevel){
        info.clear();
        currentStates.clear();
        currentStatesClone.clear();
        maxLevel = 0;
        currentStatesClone.merge(currentMap.get(u).copy()).each((entry, states) -> {
            isValid = false;
            maxLevel = Math.max(maxLevel, entry.stacks() - 1);
            Seq<UpgradeState> curStates = Seq.with();
            states.each(s -> {
                Log.info(s);
                if(!s.installed || s.level < entry.stacks()) {
                    curStates.add(s);
                }
            });
            if(!curStates.isEmpty()) currentStates.put(entry, curStates);
        });
        Log.info(currentStates);
        Log.info(currentMap);
        i = 0;
        info.add(topBar).left().height(40).fillX();
        rebuildInfoButtons();
    }

    public static void rebuildInfoButtons(){
        topBar.clear();
        Cell<ImageButton> current = topBar.button(Icon.up, () -> {
            currentLevel++;
            rebuildInfo(currentUpgrade, currentLevel);
        });
        current.size(80, 40);
        current.get().setDisabled(() -> currentLevel >= maxLevel);
        current = topBar.button(Icon.down, () -> {
            currentLevel--;
            rebuildInfo(currentUpgrade, currentLevel);
        });
        current.size(80, 40);
        current.get().setDisabled(() -> currentLevel <= 0);
    }
}
