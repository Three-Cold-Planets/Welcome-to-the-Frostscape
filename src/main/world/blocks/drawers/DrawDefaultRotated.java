package main.world.blocks.drawers;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawDefault;

public class DrawDefaultRotated extends DrawDefault {

    public String suffix;

    public TextureRegion region;
    public DrawDefaultRotated(String suffix){
        this.suffix = suffix;
    }

    @Override
    public void load(Block block) {
        super.load(block);
        region = Core.atlas.find(block.name + suffix);
    }

    @Override
    public void draw(Building build){
        Draw.rect(region, build.x, build.y, build.rotation * 90);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        block.drawDefaultPlanRegion(plan, list);
    }
}
