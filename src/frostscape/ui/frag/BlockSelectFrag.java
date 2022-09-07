package frostscape.ui.frag;

import arc.Core;
import arc.Events;
import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import frostscape.Frostscape;
import frostscape.type.upgrade.Upgrade;
import frostscape.type.upgrade.UpgradeableBuilding;
import frostscape.world.upgrades.UpgradeEntry;
import frostscape.world.upgrades.UpgradeState;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.DatabaseDialog;
import mindustry.ui.dialogs.SchematicsDialog;
import mindustry.ui.fragments.BlockConfigFragment;
import mindustry.world.blocks.units.UnitFactory;

import static mindustry.Vars.*;
import static mindustry.Vars.state;

public class BlockSelectFrag {
    private static int i = 0;
    public Table table = new Table(), content = new Table();
    public Seq<SelectButton> buttons = new Seq<>(), current = new Seq<>();
    public SelectButton selected = null;

    public void setButton(SelectButton b){
        selected = b;
    }
    public void build(Group parent){
        table.visible = false;
        parent.addChild(table);

        Events.on(EventType.ResetEvent.class, e -> forceHide());

        //Add exit button
        buttons.add(new SelectButton("Exit", Icon.left, false, (b) -> true, (t, builds) -> hideConfig()));
        //Enable/Disable
        buttons.add(new SelectButton("Disable", Icon.cancel, false, (b) -> true, (t, builds) -> builds.each(b -> b.enabled = false)));
        buttons.add(new SelectButton("Enable", Icon.play, false, (b) -> true, (t, builds) -> builds.each(b -> b.enabled = true)));
        //Upgrades
        SelectButton upgrades = new SelectButton("Upgrades", Icon.hammer, true, (b) -> b.find(build -> build instanceof UpgradeableBuilding) != null, (table, builds) -> {
            table.clear();
            table.background(Styles.black6);
            table.setOrigin(Align.top);
            table.setSize(160, 800);
            ObjectMap<Upgrade, ItemSeq> currentMap = new ObjectMap<>();
            Seq<UpgradeableBuilding> buildings = new Seq<>();
            builds.each(b -> {
                if(b instanceof UpgradeableBuilding) {
                    UpgradeableBuilding building = (UpgradeableBuilding) b;
                    buildings.add(building);
                    building.type().entries().each(entry -> {
                        if(!currentMap.containsKey(entry.upgrade)) currentMap.put(entry.upgrade, new ItemSeq());
                        UpgradeState state = building.upgrades().getState(entry.upgrade);
                        ItemStack[] add;
                        if(state == null) add = entry.costs[0];
                        else if(state.installing || state.level >= entry.stacks()) return;
                        else add = entry.costs[state.level + 1];
                        currentMap.get(entry.upgrade).add(add);
                    });
                }
            });
            table.pane(pane -> {
                float length = 0;
                pane.image().fillX().height(5).padTop(3).top();
                pane.row();
                pane.table(t -> {
                    i = 0;
                    currentMap.each((upgrade, arr) -> {
                        Log.info(upgrade);
                        Log.info(arr);
                        ClickListener listener = new ClickListener();
                        Image image = new Image(upgrade.region);
                        image.addListener(listener);
                        image.update(() -> image.color.lerp(!listener.isOver() ? Color.lightGray : Color.white, Mathf.clamp(0.4f * Time.delta)));
                        image.addListener(new HandCursorListener());
                        image.clicked(() -> {
                            Log.info("hi");
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
                pane.row();
                pane.image().fillX().height(5).padBottom(3).top();
            }).width(200).height(300);
        });
        buttons.add(upgrades);
    }

    public boolean hasConfigMouse(){
        Element e = Core.scene.hit(Core.input.mouseX(), Core.graphics.getHeight() - Core.input.mouseY(), true);
        return e != null && (e == table || e.isDescendantOf(table));
    }
    public void forceHide(){
        table.visible = false;
        content.visible = false;
        selected = null;
    }

    public void setButtons(Seq<Building> buildings){
        current.clear();
        buttons.each(button -> {
            if(button.cond.get(buildings)) current.add(button);
        });
    }
    public boolean showConfig(Seq<Building> buildings){
        if(buildings.size == 0) return false;
        setButtons(buildings);
        //For whatever higher-being-forsaken reason you remove the exit button I will... release the lions
        if(buttons.size == 0) {
            try {
                throw new IllegalStateException("RELEASE THE LIONS");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        table.clear();
        content.clear();
        table.visible = true;
        table.pack();
        table.setTransform(true);
        table.actions(Actions.scaleTo(0f, 1f), Actions.visible(true),
        Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out));
        table.setOrigin(Align.top);
        updateTableAlign(table);

        content.setOrigin(Align.center);

        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(0);
        table.table(t -> {
            t.defaults().size(50);
            i = 0;
            current.each(c -> {
                ImageButton button = t.button(Tex.whiteui, Styles.clearTogglei, 24, () -> {
                    if(c == selected) {
                        hideCurrent();
                        selected = null;
                        return;
                    };
                    setButton(c);
                    //set up the table
                    if(c.hasTable) {
                        content.visible = true;
                        content.actions(Actions.scaleTo(0f, 1f), Actions.visible(true),
                                Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out));
                    }
                    c.cons.get(content, buildings);
                }).group(group).tooltip(c.name).get();
                button.getStyle().imageUp = c.icon;
                button.table().pad(0);
                if(i++ % 4 == 3){
                    t.row();
                }
            });

            if(i % 4 != 0){
                int remaining = 4 - (i % 4);
                for(int j = 0; j < remaining; j++){
                    t.image(Styles.black6);
                }
            }
        });

        table.row();
        table.add(content);

        return true;
    };

    public void updateTableAlign(Table table) {
        Vec2 pos = Tmp.v1.set(Core.input.mouseX(), Core.input.mouseY());
        table.setPosition(pos.x, pos.y, 2);
    }

    public void hideConfig(){
        table.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.visible(false));
        current.clear();
        selected = null;
    }

    public void hideCurrent(){
        content.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.visible(false));
    }

    public class SelectButton{
        public String name;
        public Drawable icon;
        public Boolf<Seq<Building>> cond;
        public Cons2<Table, Seq<Building>> cons;
        public boolean hasTable = false;
        public SelectButton(String name, Drawable icon, boolean hasTable, Boolf<Seq<Building>> cond, Cons2<Table, Seq<Building>> cons){
            this.name = name;
            this.icon = icon;
            this.hasTable = hasTable;
            this.cond = cond;
            this.cons = cons;
        }
    }
}
