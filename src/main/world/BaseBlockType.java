package main.world;

import arc.struct.Seq;
import main.world.blocks.drawers.UpgradeDrawer;
import main.world.module.BlockHeatModule;
import main.world.systems.heat.HeatControl;

public interface BaseBlockType extends UpgradesType {
    Seq<UpgradeDrawer> drawers();

    BlockHeatModule heat();
}
