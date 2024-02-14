package main.world.blocks.drawers;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawConfigItem extends DrawBlock {
    public TextureRegion itemRegion;
    public String suffix;

    public DrawConfigItem(){
        suffix = "-top";
    }

    public DrawConfigItem(String suffix){
        this.suffix = suffix;
    }

    @Override
    public void load(Block block) {
        super.load(block);
        itemRegion = Core.atlas.find(block.name + suffix);
    }

    @Override
    public void draw(Building build) {
        if(build.config() instanceof Item content){
            Draw.color(content.color);
            Draw.rect(itemRegion, build.x, build.y);
        }
    }
}
