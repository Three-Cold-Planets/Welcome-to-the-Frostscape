package main.world.blocks.drawers;

import main.world.BaseBlockType;
import main.world.systems.upgrades.Upgrade;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

//Upgrade drawers draw certain parts on blocks if they have a matching upgrade. Sometimes blocks will have hardecoded drawing based on upgrades as flags, check the building class instead.
public class UpgradeDrawer extends DrawBlock {
    public Upgrade upgrade;

    public BaseBlockType expectUpgradeable(Block block){
        if(!(block instanceof BaseBlockType upgradeable)) throw new ClassCastException("This drawer requires the block to implement UpgradeableBlock. Use a different drawer.");
        return upgradeable;
    }
}
