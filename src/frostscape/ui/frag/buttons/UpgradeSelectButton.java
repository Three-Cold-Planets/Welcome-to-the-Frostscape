package frostscape.ui.frag.buttons;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
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
import frostscape.ui.frag.BlockSelectFrag;
import frostscape.world.upgrades.UpgradeEntry;
import frostscape.world.upgrades.UpgradeState;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

public class UpgradeSelectButton extends BlockSelectFrag.SelectButton {
    private static int i = 0, currentLevel = 0, maxLevel = 0;
    public Upgrade currentUpgrade;
    private boolean isValid = false;
    public Table info = new Table(), costs = new Table(), infoList = new Table(), list = new Table(), topBar = new Table(), upgradeList = new Table();
    public ObjectMap<Upgrade, ObjectMap<UpgradeEntry, Seq<UpgradeState>>> currentMap = new ObjectMap<>();
    public ObjectMap<UpgradeEntry, Seq<UpgradeState>> currentStates = new ObjectMap<>(), currentStatesClone = new ObjectMap<>();

    public UpgradeSelectButton(){
        name = "Upgrades";
        icon = Icon.hammer;
        hasTable = true;
        cond = (b) -> b.find(build -> build instanceof UpgradeableBuilding) != null;
        cons = (table, builds) -> {
            table.clear();
            table.background(Styles.black6);
            table.setOrigin(Align.top);
            table.setSize(600, 400);
            //Rebuild individual tables
            rebuild(table, builds);

            //Add them to content table
            table.table(select -> {
                select.pane(upgradeList);
            }).width(200).height(400);
            table.image().height(400).width(10);
            table.add(info).width(400).height(400);
        };
        update = (table, builds) -> rebuild(table, builds);
    }

    public void rebuild(Table table, Seq<Building> builds){
        //Update the list of upgrades
        info.clear();
        currentMap.clear();
        currentStates.clear();
        currentStatesClone.clear();
        Seq<UpgradeableBuilding> buildings = new Seq<>();
        //Mess
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
        rebuildUpgrades();
    }

    public void rebuildUpgrades(){
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

    public void rebuildInfo(Upgrade u, int currentLevel){
        info.clear();
        currentStates.clear();
        currentStatesClone.clear();
        info.left().marginLeft(0);
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
        info.add(topBar).left().top().height(40).width(400);
        rebuildInfoButtons();
        info.row();
        info.table(bottomTable -> {
            bottomTable.background(Tex.button);
            bottomTable.add(infoList).width(300).height(360);
            rebuildInfoList();
            bottomTable.add(costs).width(100).height(360);
            costs.background(Tex.button);
        }).width(400).height(360);
    }

    public void rebuildInfoButtons(){
        topBar.clear();
        topBar.left();
        topBar.table(buttonTable -> {
            Cell<ImageButton> current = buttonTable.button(Icon.up, () -> {
                currentLevel++;
                rebuildInfo(currentUpgrade, currentLevel);
            });
            current.size(60, 40);
            current.get().setDisabled(() -> currentLevel >= maxLevel);
            current = buttonTable.button(Icon.down, () -> {
                currentLevel--;
                rebuildInfo(currentUpgrade, currentLevel);
            });
            current.size(60, 40);
            current.get().setDisabled(() -> currentLevel <= 0);
        }).width(120).height(40);

        topBar.label(() -> "[gray]" + (currentLevel - 1 < 0 ? "NULL" : Integer.toString(currentLevel - 1)) + " < ").padLeft(20).width(70);
        topBar.label(() -> "LV " + currentLevel + (Time.time % 90 < 45 ? "_" : "")).padLeft(15).width(70);
        topBar.label(() ->  "[gray] < " + (currentLevel + 1 > maxLevel ? "NULL[]" : Integer.toString(currentLevel + 1))).padLeft(15).width(70);
    }

    public void rebuildInfoList(){
        infoList.clear();
        infoList.top().left();
        infoList.table(upgradeInfo -> {
            upgradeInfo.background(Tex.button);
            upgradeInfo.top().left();
            upgradeInfo.label(() -> currentUpgrade.localisedName).get().setFontScale(0.85f, 0.85f);
            upgradeInfo.row();
            upgradeInfo.image().fillX();
            upgradeInfo.row();
            upgradeInfo.image(currentUpgrade.region).size(50);
        }).width(300).height(100);
        infoList.background(Tex.button);
    }
}
