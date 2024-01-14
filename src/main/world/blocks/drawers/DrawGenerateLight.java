package main.world.blocks.drawers;

import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.draw.DrawBlock;

public class DrawGenerateLight extends DrawBlock {

    public float scl, mag, opacity, baseRadius;
    public Color color;

    public DrawGenerateLight(){
        scl = 10;
        mag = 5;
        opacity = 0.5f;
        color = Color.orange;
        baseRadius = 60;
    }
    @Override
    public void drawLight(Building build) {
        super.drawLight(build);

        Drawf.light(build.x, build.y, (baseRadius + Mathf.absin(scl, mag)), color, opacity * build.warmup());
    }
}
