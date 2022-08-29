package frostscape.world.blocks.drawers;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Log;
import arc.util.Nullable;
import frostscape.type.upgrade.Upgrade;
import frostscape.type.upgrade.UpgradeableBuilding;
import frostscape.world.UpgradesBlock;
import frostscape.world.upgrades.UpgradeState;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ShockMine;
import mindustry.world.blocks.production.GenericCrafter;

public class DrawUpgradePart extends UpgradeDrawer{
    public TextureRegion[] regions;
    public TextureRegion base;

    public DrawUpgradePart(TextureRegion base, TextureRegion[] regions, Upgrade upgrade){
        this.regions = regions;
        this.base = base;
    }

    @Override
    public void draw(Building build) {
        super.draw(build);
        TextureRegion region = getRegion(build);
        if(region == null) region = base;
    }

    public TextureRegion getRegion(Building build){
        Seq<Upgrade> upgrades = expectUpgradeable(build.block).upgrades();
        UpgradeableBuilding building = (UpgradeableBuilding) build;
        UpgradeState state = building.upgrades().states.find(s -> s.upgrade == upgrade);
        if(state == null) return null;
        return regions[state.level];
    }

    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){

    }

    public void load(Block block){

    }

    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{block.region};
    }

    public static TextureRegion[] genRegionsList(int size, TextureRegion base, int[] indexes, TextureRegion[] values){
        TextureRegion[] returnArr = new TextureRegion[size];
        TextureRegion current = base;
        int index = 0;
        for (int i = 0; i < size; i++) {
            Log.info(index);
            Log.info(index < indexes.length);
            if(index < indexes.length && indexes[index] == i) {
                current = values[index];
                index++;
            }
            returnArr[i] = current;
        }
        return returnArr;
    }
}
