package main.world;

import arc.struct.Seq;
import main.world.blocks.drawers.UpgradeDrawer;

public interface UpgradesBlock extends UpgradesType {
    Seq<UpgradeDrawer> drawers();
}
