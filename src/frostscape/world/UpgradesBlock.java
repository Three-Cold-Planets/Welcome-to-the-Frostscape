package frostscape.world;

import arc.struct.Seq;
import frostscape.type.upgrade.Upgrade;
import frostscape.world.blocks.drawers.UpgradeDrawer;

public interface UpgradesBlock extends UpgradesType {
    Seq<Upgrade> upgrades();
    Seq<UpgradeDrawer> drawers();
}
