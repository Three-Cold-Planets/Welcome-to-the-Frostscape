package frostscape.ui.overlay;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import frostscape.content.Palf;
import frostscape.world.FrostscapeBlock;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.MendProjector;

import static mindustry.Vars.tilesize;

//LIFE IS PAIN
public class SelectOverlay {
    public Rect select = new Rect();

    public Seq<Runnable> stateListeners = new Seq<Runnable>();
    public Vec2 selectStart = new Vec2();
    private Rect selectArea = new Rect();
    public boolean awaitingSelect = true;

    public boolean selecting = false, override = true;
    private boolean changed = false;
    public Seq<Building> buildings = new Seq<>(), last = new Seq<>();

    public void update(){
        changed = false;

        if(Core.input.keyTap(KeyCode.controlLeft)) {
            if(!awaitingSelect && !selecting) {
                awaitingSelect = true;
                Vars.ui.showInfoToast("Block select active! Press Ctrl again to cancel", 1);
            }
            else{
                awaitingSelect = false;
                selecting = false;
                Vars.ui.showInfoToast("Block selection canceled", 1);
            }
        }
        override = !Core.input.keyDown(KeyCode.c);

        if(!buildings.equals(last)) stateChange();

        //Start selecting if awaiting input
        if(awaitingSelect && Core.input.keyDown(KeyCode.mouseLeft)){
            awaitingSelect = false;
            selecting = true;
            selectStart.set(Core.input.mouseWorld());
            selectArea.x = selectStart.x;
            selectArea.y = selectStart.y;
        }
        //After started, run logic
        if(selecting){
            if(Core.input.keyDown(KeyCode.mouseLeft)) {
                selectArea.width = Core.input.mouseWorldX() - selectArea.x;
                selectArea.height = Core.input.mouseWorldY() - selectArea.y;
            }
            //If mouse is lifted, stop running logic and add buildings
            else {
                selecting = false;
                if(override) buildings.clear();
                intersectRect();
                Vars.indexer.eachBlock(Vars.player.team(), select, b -> b.block instanceof FrostscapeBlock, b -> {
                    buildings.add(b);
                });
                changed = true;
            }
        }

        last.set(buildings);
        last.each(b -> {
            if(!b.isAdded()) {
                buildings.remove(b);
                changed = true;
            }
        });
        //alert listeners that buildings have been changed
        if(changed) stateChange();
    }

    public void stateChange(){
        stateListeners.each(listener -> listener.run());
    }
    public void intersectRect(){
        select.set(selectArea);
        if(selectArea.width < 0){
            select.x += select.width;
            select.width *= -1;
        }
        if(selectArea.height < 0){
            select.y += select.height;
            select.height *= -1;
        }
    };

    public void drawSelect(){
        if(selecting) {
            Draw.color(Palf.select);
            Draw.alpha(0.45f);
            intersectRect();
            Fill.rect(select.x + select.width/2, select.y + select.height/2, select.width, select.height);
            Vars.indexer.eachBlock(Vars.player.team(), select, b -> b.block instanceof FrostscapeBlock, b -> {
                if(!buildings.contains(b)) Drawf.square(b.x, b.y, b.block.size * tilesize / 2f, Palf.select);
            });
        }
        buildings.each(b -> Drawf.select(b.x, b.y, b.block.size * 4, Palf.select));
    }
}
