package main.world.blocks.drawers;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Tmp;
import main.world.blocks.plug.ItemPlug;
import mindustry.gen.Building;
import mindustry.world.draw.DrawGlowRegion;

public class DrawItemGlowRegion extends DrawGlowRegion {
    public DrawItemGlowRegion(){

    }
    public DrawItemGlowRegion(String suffix){
        this.suffix = suffix;
    }

    @Override
    public void draw(Building build){
        if(build.warmup() <= 0.001f) return;

        ItemPlug.ItemPlugBuild plug = (ItemPlug.ItemPlugBuild) build;

        Tmp.c1.set(color);
        if(plug.stockItem != null) Tmp.c1.lerp(plug.stockItem.color, 0.7f);

        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        Draw.blend(blending);
        Draw.color(Tmp.c1);
        Draw.alpha((Mathf.absin(build.totalProgress(), glowScale, alpha) * glowIntensity + 1f - glowIntensity) * build.warmup() * alpha);
        Draw.rect(region, build.x, build.y, build.totalProgress() * rotateSpeed + (rotate ? build.rotdeg() : 0f));
        Draw.reset();
        Draw.blend();
        Draw.z(z);
    }
}
