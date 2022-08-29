package frostscape.world.blocks.drawers;

import arc.graphics.g2d.TextureRegion;
import frostscape.type.upgrade.Upgrade;
import frostscape.world.UpgradesBlock;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

//Upgrade drawers draw certain parts on blocks if they have a matching upgrade. Sometimes blocks will have hardecoded drawing based on upgrades as flags, check the building class instead.
public class UpgradeDrawer extends DrawBlock {
    public Upgrade upgrade;

    public UpgradesBlock expectUpgradeable(Block block){
        if(!(block instanceof UpgradesBlock crafter)) throw new ClassCastException("This drawer requires the block to be a GenericCrafter. Use a different drawer.");
        return (UpgradesBlock) crafter;
    }
}
