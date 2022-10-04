package frostscape.type.upgrade;

import frostscape.world.UpgradesType;
import frostscape.world.module.UpgradeModule;
import frostscape.world.upgrades.UpgradeEntry;
import frostscape.world.upgrades.UpgradeState;

//An interface which marks an entity as upgradeable, and returns it's current state
public interface Upgradeable {

    UpgradesType type();
    UpgradeModule upgrades();
    void applyDeltas(UpgradeState state);
    void resetDeltas();
}
