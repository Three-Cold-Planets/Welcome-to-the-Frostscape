package frostscape.world;

import arc.struct.Seq;
import frostscape.type.upgrade.Upgrade;
import frostscape.world.upgrades.UpgradeEntry;
import frostscape.world.upgrades.UpgradeState;
import mindustry.type.ItemStack;

public interface UpgradesType {
    Seq<UpgradeEntry> entries();
}
