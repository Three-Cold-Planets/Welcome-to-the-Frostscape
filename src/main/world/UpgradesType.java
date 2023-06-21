package main.world;

import arc.struct.Seq;
import main.world.systems.upgrades.UpgradeEntry;

public interface UpgradesType {
    Seq<UpgradeEntry> entries();
}
