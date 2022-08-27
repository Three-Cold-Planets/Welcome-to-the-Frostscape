package frostscape.ui.overlay;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
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

//LIFE IS PAIN
public class SelectOverlay {

    public Seq<Runnable> stateListeners = new Seq<Runnable>();
    public Vec2 selectStart = new Vec2();
    public Rect selectArea = new Rect();
    public boolean selectingStill = true;

    public boolean selecting = false, override = true;
    public Seq<Building> buildings = new Seq<>(), last = new Seq<>();

    public void update(){
        Fx.smoke.at(selectArea.x, selectArea.y);
        Fx.smoke.at(selectArea.x + selectArea.width, selectArea.y + selectArea.height);
        override = !Core.input.keyDown(KeyCode.c);
        if(!buildings.equals(last)) stateChange();

        if(Core.input.keyDown(KeyCode.controlLeft) && Core.input.keyDown(KeyCode.mouseLeft)){
            if(!selecting) {
                selecting = true;
                selectStart.set(Core.input.mouseWorld());
                selectArea.x = selectStart.x;
                selectArea.y = selectStart.y;
            }
            selectArea.width = Core.input.mouseWorldX() - selectStart.x;
            selectArea.height = Core.input.mouseWorldY() - selectStart.y;
        }
        else if(selecting) {
            selecting = false;
            if(override) buildings.clear();
            Vars.indexer.eachBlock(Vars.player.team(), selectArea, b -> b.block instanceof FrostscapeBlock, b -> {
                buildings.add(b);
            });
            Sounds.buttonClick.play();
            stateChange();
        }
    }

    public void stateChange(){

    }

    public void drawSelect(){
        if(selecting) {
            Draw.color(Pal.accentBack);
            Draw.alpha(0.2f);
            Fill.rect(selectArea.x + selectArea.width/2, selectArea.y + selectArea.height/2, selectArea.width, selectArea.height);
        }
        buildings.each(b -> Drawf.select(b.x, b.y, b.block.size * 4, Pal.accentBack));
    }
}
