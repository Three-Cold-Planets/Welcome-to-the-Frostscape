package frostscape.ui.frag;

import arc.Core;
import arc.Events;
import arc.func.Boolf;
import arc.func.Cons;
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
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import frostscape.Frostscape;
import frostscape.type.upgrade.UpgradeableBuilding;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.fragments.BlockConfigFragment;
import mindustry.world.blocks.units.UnitFactory;

import static mindustry.Vars.control;
import static mindustry.Vars.player;

public class BlockSelectFrag {
    public Table table = new Table(), content = new Table();
    public Seq<SelectButton> buttons = new Seq<>(), current = new Seq<>();
    private SelectButton selected = null;

    public void setButton(SelectButton b){
        selected = b;
    }
    public static int i = 0;
    public void build(Group parent){
        table.visible = false;
        parent.addChild(table);

        Events.on(EventType.ResetEvent.class, e -> forceHide());

        //Add exit button
        buttons.add(new SelectButton("Exit", Icon.left, false, (b) -> true, (t) -> forceHide()));
        //Upgrades
        SelectButton upgrades = new SelectButton("Upgrades", Icon.hammer, true, (b) -> b.find(build -> build instanceof UpgradeableBuilding) != null, (table) -> {
            table.pane(Styles.horizontalPane, e -> {

            }).width(160);
        });
        buttons.add(upgrades);
    }

    public boolean hasConfigMouse(){
        Element e = Core.scene.hit(Core.input.mouseX(), Core.graphics.getHeight() - Core.input.mouseY(), true);
        return e != null && (e == table || e.isDescendantOf(table));
    }
    public void forceHide(){
        table.visible = false;
        if(selected == null || selected.table == null) return;
        selected.table.clear();
        selected.table.visible = false;
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
        table.setOrigin(Align.center);
        updateTableAlign(table);

        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(0);
        table.defaults().size(40);

        i = 0;
        current.each(c -> {

            ImageButton button = table.button(Tex.whiteui, Styles.clearTogglei, 24, () -> {
                if(c == selected) return;
                setButton(c);
                //set up the table
                if(c.table != null) {
                    c.table.visible = true;
                    c.table.clear();
                    c.table.pack();
                    c.table.setTransform(true);
                    c.table.actions(Actions.scaleTo(0f, 1f), Actions.visible(true),
                        Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out));
                    c.table.setOrigin(Align.center);
                    updateTableAlign(c.table);
                    content.add(c.table);
                }
                c.cons.get(c.table);
            }).group(group).tooltip(c.name).get();
            button.getStyle().imageUp = c.icon;
            button.table().pad(0);
            if(i++ % 4 == 3){
                table.row();
            }
        });

        if(i % 4 != 0){
            int remaining = 4 - (i % 4);
            for(int j = 0; j < remaining; j++){
                table.image(Styles.black6);
            }
        }

        table.row();
        table.add(content);

        return true;
    };

    public void updateTableAlign(Table table) {
        Vec2 pos = Tmp.v1.set(Core.input.mouseX(), Core.input.mouseY());
        table.setPosition(pos.x, pos.y, 2);
    }

    public void hideConfig(){
        current.clear();
        table.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.visible(false));
    }

    public class SelectButton{
        public String name;
        public Drawable icon;
        public Boolf<Seq<Building>> cond;
        public Cons<Table> cons;
        public Table table;
        public SelectButton(String name, Drawable icon, boolean hasTable, Boolf<Seq<Building>> cond, Cons<Table> cons){
            this.name = name;
            this.icon = icon;
            if(hasTable) table = new Table();
            this.cond = cond;
            this.cons = cons;
        }
    }
}
