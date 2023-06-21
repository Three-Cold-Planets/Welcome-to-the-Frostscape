package main.type.upgrade;

import main.world.UpgradesType;
import main.world.systems.upgrades.UpgradeModule;
import main.world.systems.upgrades.UpgradeState;

//An interface which marks an entity as upgradeable, and returns it's current state
public interface Upgradeable {

    UpgradesType type();
    UpgradeModule upgrades();
    void applyDeltas(UpgradeState state);
    void resetDeltas();
}
