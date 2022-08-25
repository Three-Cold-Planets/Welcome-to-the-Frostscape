package frostscape.world.blocks.core;

import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.type.upgrade.Upgradeable;
import frostscape.world.module.UpgradeModule;
import mindustry.world.blocks.storage.CoreBlock;

public class BaseCore extends CoreBlock {
    public BaseCore(String name) {
        super(name);
    }

    public class BaseCoreBuild extends CoreBuild implements Upgradeable {
        public UpgradeModule upgrades = new UpgradeModule();

        @Override
        public void writeBase(Writes write) {
            super.writeBase(write);
            upgrades.write(write);
        }

        @Override
        public void readBase(Reads read) {
            super.readBase(read);
            upgrades.read(read);
        }

        @Override
        public UpgradeModule upgrades() {
            return upgrades;
        }
    }
}