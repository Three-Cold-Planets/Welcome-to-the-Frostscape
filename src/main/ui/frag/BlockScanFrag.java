package main.ui.frag;

import arc.Core;
import arc.Events;
import arc.math.geom.Vec2;
import arc.scene.Group;
import arc.scene.ui.layout.Table;
import arc.util.Tmp;
import mindustry.game.EventType;

public class BlockScanFrag {
    public Table table = new Table();

    public void build(Group parent){
        table.visible = false;
        parent.addChild(table);

        Events.on(EventType.ResetEvent.class, e -> forceHide());
    }

    public void forceHide(){
        table.visible = false;
    }

    public void updateTableAlign(Table table) {
        Vec2 pos = Tmp.v1.set(Core.scene.getWidth()/2, Core.scene.getHeight()/2);
        table.setPosition(pos.x, pos.y, 2);
    }
}
