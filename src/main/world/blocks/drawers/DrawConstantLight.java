package main.world.blocks.drawers;

import arc.graphics.Color;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawConstantLight extends DrawBlock {
    public float radius, alpha;

    public Color color;

    @Override
    public void load(Block block) {
        super.load(block);
        if(radius == 0) radius = block.lightRadius;
        if(color == null) color = block.lightColor;
        if(alpha == 0) alpha = block.lightColor.a;
    }

    @Override
    public void drawLight(Building build) {
        super.drawLight(build);
        Drawf.light(build.x, build.y, radius, color, alpha);
    }
}
