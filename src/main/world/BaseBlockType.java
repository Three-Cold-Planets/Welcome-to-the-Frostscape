package main.world;

import arc.struct.Seq;
import main.world.blocks.drawers.UpgradeDrawer;

public interface BaseBlockType extends UpgradesType {
    Seq<UpgradeDrawer> drawers();
}
