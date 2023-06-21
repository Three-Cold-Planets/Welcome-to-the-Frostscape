package main.world;

import arc.struct.Seq;
import main.world.blocks.drawers.UpgradeDrawer;
import main.world.systems.upgrades.Upgrade;

public interface UpgradesBlock extends UpgradesType {
    Seq<Upgrade> upgrades();
    Seq<UpgradeDrawer> drawers();
}
