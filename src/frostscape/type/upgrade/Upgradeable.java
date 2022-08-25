package frostscape.type.upgrade;

import frostscape.world.module.UpgradeModule;

//An interface which marks an entity as upgradeable, and returns it's current state
public interface Upgradeable {

    public UpgradeModule upgrades();
}
