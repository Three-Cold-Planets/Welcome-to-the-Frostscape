package main.world.blocks.drawers;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Eachable;
import main.world.blocks.light.SolarReflector.SolarReflectorBuild;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class ReflectorDrawer extends DrawBlock {
    public String name;
    public TextureRegion reflector, shine, outline;
    public float layer, topLayer;

    public ReflectorDrawer(String name, float layer, float topLayer){
        this.name = name;
        this.layer = layer;
        this.topLayer = topLayer;
    }

    @Override
    public void load(Block block) {
        reflector = Core.atlas.find(name);
        outline = Core.atlas.find(name + "-outline");
        shine = Core.atlas.find(name + "-shine");
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{reflector, outline};
    }

    @Override
    public void getRegionsToOutline(Block block, Seq<TextureRegion> out) {
        out.add(reflector);
    }

    @Override
    public void draw(Building build) {
        SolarReflectorBuild reflector = (SolarReflectorBuild) build;
        Draw.z(layer);
        if (this.outline.found()) {
            Draw.rect(this.outline, build.x, build.y, reflector.rotation);
        }
        Draw.rect(this.reflector, build.x, build.y, reflector.rotation);
        Draw.z(topLayer);
        Draw.blend(Blending.additive);
        Draw.rect(this.shine, build.x, build.y, reflector.rotation);
        Draw.blend();
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(reflector, plan.drawx(), plan.drawy(), (plan.rotation) * 90);
    }
}
