package main.ui.frag.buttons;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Button;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import main.type.upgrade.UpgradeableBuilding;
import main.ui.frag.BlockSelectFrag;
import main.world.UpgradesType;
import main.world.systems.upgrades.Upgrade;
import main.world.systems.upgrades.UpgradeEntry;
import main.world.systems.upgrades.UpgradeState;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;

import java.util.Iterator;

import static mindustry.Vars.*;

public class UpgradeSelectButton extends BlockSelectFrag.SelectButton {
    private static int i = 0, currentLevel = 0, maxLevel = 0;
    public Upgrade currentUpgrade;
    private boolean isValid = false;
    public Table content = null, info = new Table(), costTable = new Table(), infoList = new Table(), list = new Table(), topBar = new Table(), upgradeList = new Table();
    public Seq<Building> buildings = new Seq<>();
    public ObjectMap<Upgrade, IntMap<ItemSeq>> costs = new ObjectMap<>();
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
        costTable.clear();
        infoList.clear();
        list.clear();
        topBar.clear();
        topBar.clear();
        upgradeList.clear();

        buildings.clear();
        currentMap.clear();
        currentStates.clear();
        currentStatesClone.clear();
        costs.clear();

        content = table;
        //Mess
        costs.clear();
        builds.each(b -> {
            //if the building isn't an upgradeable building, return
            if(!(b instanceof UpgradeableBuilding)) return;
            UpgradeableBuilding building = (UpgradeableBuilding) b;

            buildings.add(b);
            //Building the map
            building.type().entries().each(entry -> {
                if(!currentMap.containsKey(entry.upgrade)) currentMap.put(entry.upgrade, new ObjectMap<>());
                if(!currentMap.get(entry.upgrade).containsKey(entry)) currentMap.get(entry.upgrade).put(entry, new Seq<>());
                UpgradeState state = building.upgrades().getState(entry.upgrade);
                currentMap.get(entry.upgrade).get(entry).add(state);

                //Calculate costs
                if(!costs.containsKey(entry.upgrade)) costs.put(entry.upgrade, new IntMap());

                IntMap<ItemSeq> mappedCosts = costs.get(entry.upgrade);

                //Entry is at its max level, return
                if(state.level + 1 > entry.stacks()) return;

                if(!mappedCosts.containsKey(state.level + 1)) mappedCosts.put(state.level + 1, new ItemSeq());

                ItemStack[] add;
                if(state == null) add = entry.costs[0];
                else if(state.installing || state.level + 1 >= entry.stacks()) return;
                else add = entry.costs[state.level + 1];
                mappedCosts.get(state.level + 1).add(add);
            });
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
        info.defaults().left().top().padLeft(0).padTop(0);

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
        info.image().fillX().height(10);
        info.row();
        info.table(bottomTable -> {
            bottomTable.add(infoList).width(300).height(340);
            rebuildInfoList();
            bottomTable.image().height(340).width(4).padLeft(8).padRight(8);
            bottomTable.add(costTable).width(80).height(340);
            rebuildCosts();
        }).width(400).height(340).padTop(10);
    }

    public void rebuildCosts(){
        costTable.clear();
        if(costs.get(currentUpgrade).get(currentLevel) == null) {
            //:(
            costTable.add(":(");
            return;
        }

        costTable.pane(Styles.horizontalPane, pane -> {
            pane.top().left().defaults().padLeft(4);

            ItemSeq current = costs.get(currentUpgrade).get(currentLevel);
            Iterator<ItemStack> iterator = current.iterator();
            while (iterator.hasNext()){
                ItemStack s = iterator.next();
                pane.image(s.item.uiIcon).left().size(iconMed);
                pane.label(() -> {
                    Building core = player.core();
                    if(core == null || state.rules.infiniteResources || core.items.has(s.item, s.amount)) return "[lightgray]" + s.amount + "";
                    return (core.items.has(s.item, s.amount) ? "[lightgray]" : "[scarlet]") + Math.min(core.items.get(s.item), s.amount) + "[lightgray]/" + s.amount;
                }).padLeft(2).left().padRight(4).wrap();
                pane.row();
            }
        }).width(100).height(300);
        costTable.row();
        Button button = new Button();
        button.image(Icon.add);
        button.setDisabled(() -> !player.core().items.has(costs.get(currentUpgrade).get(currentLevel)));
        costTable.button(Icon.add, () -> {
            isValid = false;
            buildings.each(b -> {
                UpgradeableBuilding build = (UpgradeableBuilding) b;
                UpgradesType type = build.type();

                type.entries().each(entry -> {
                    if(entry.upgrade != currentUpgrade) return;
                    UpgradeState state = build.upgrades().getState(entry.upgrade);
                    if(state.installing || state.level != currentLevel - 1) return;
                    build.upgrades().startUpgrade(entry);
                    Fx.coreBuildBlock.at(build.self().x, build.self().y, build.self().block.size);
                    isValid = true;
                });
            });
            if(isValid){
                rebuild(content, buildings.copy());
                currentLevel = Math.min(currentLevel + 1, maxLevel);

                rebuildInfo(currentUpgrade, currentLevel);
            }
        }).width(100).height(40);
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

        topBar.label(() -> (costs.get(currentUpgrade).get(currentLevel - 1) == null ? "[gray]" : "[lightgray]") + (currentLevel - 1 < 0 ? "NULL" : Integer.toString(currentLevel - 1)) + " < ").padLeft(20).width(70);
        topBar.label(() -> "LV " + currentLevel + (Time.time % 90 < 45 ? "_" : "")).padLeft(15).width(70);
        topBar.label(() ->  (costs.get(currentUpgrade).get(currentLevel + 1) == null ? "[gray]" : "[lightgray]") + " < " + (currentLevel + 1 > maxLevel ? "NULL[]" : Integer.toString(currentLevel + 1))).padLeft(15).width(70);
    }

    public void rebuildInfoList(){
        infoList.clear();
        infoList.top().left();
        infoList.table(upgradeInfo -> {
            upgradeInfo.top().left();
            upgradeInfo.label(() -> currentUpgrade.localisedName).get().setFontScale(0.85f, 0.85f);
            upgradeInfo.row();
            upgradeInfo.image().fillX();
            upgradeInfo.row();
            upgradeInfo.image(currentUpgrade.region).size(50);
        }).width(280).height(100);
    }
}
