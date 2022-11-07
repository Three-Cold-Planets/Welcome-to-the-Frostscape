package frostscape.ui.overlay;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import frostscape.content.Palf;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;

//THERE WILL BE BLOOD-SHED
public class ScanningOverlay {

    public Vec2 scanPos = new Vec2();
    public static Color[] colors = new Color[]{Pal.accentBack, Pal.accent};
    public Tile tile;

    public void draw(){
        Draw.reset();
        Lines.stroke(2);
        Draw.color(Pal.accent);
        Tmp.v1.set(Core.input.mouseWorldX(), Core.input.mouseWorldY());
        scanPos.lerp(Tmp.v1, 0.35f);
        tile = Vars.world.tileWorld(scanPos.x, scanPos.y);

        Tmp.v1.set(tile.worldx(), tile.worldy());
        float y = Mathf.sin(Time.time, 15, 5);
        Lines.line(Tmp.v1.x - 6, Tmp.v1.y + y, Tmp.v1.x + 6, Tmp.v1.y + y);

        Lines.arc(Tmp.v1.x, Tmp.v1.y, 7, 0.75f, Time.time * 2);
        Lines.arc(Tmp.v1.x, Tmp.v1.y, 12, 0.6f, Time.time + 35);
        Lines.arc(Tmp.v1.x, Tmp.v1.y, 14, 0.4f, Time.time + 60);
    }

    public void drawScan(){
        Draw.color(Pal.accent);

        Tmp.v1.set(tile.worldx(), tile.worldy());

        //Draw the target lines
        float rotation = Time.time/2;
        Tmp.v2.trns(rotation, 8);
        Fill.square(Tmp.v1.x, Tmp.v1.y, 4);
    }
}
