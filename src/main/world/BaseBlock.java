package main.world;

import arc.math.geom.Point2;
import arc.struct.Seq;
import main.world.blocks.drawers.UpgradeDrawer;
import main.world.module.BlockHeatModule;
import main.world.systems.heat.HeatControl;
import main.world.systems.upgrades.Upgrade;
import main.world.systems.upgrades.UpgradeEntry;
import main.world.systems.upgrades.UpgradeModule;
import mindustry.world.Block;

/** Base building block for upgradeable blocks in Frostscape. Use as example/boilerplate for implementing upgrades in vanilla blocks */
public class BaseBlock extends Block implements BaseBlockType{
    public BaseBlock(String name) {
        super(name);
        this.update = true;
        this.solid = true;
    }


    public final Seq<Upgrade> upgrades = new Seq<>();

    public Seq<UpgradeEntry> entries = new Seq<>();

    public BlockHeatModule heat = new BlockHeatModule();

    @Override
    public void load() {
        super.load();
        entries.each(entry -> {
            entry.initialiseDeltas();
            upgrades.add(entry.upgrade);
        });
    }
    @Override
    public void init() {
        super.init();
        entries.each(e -> e.initialiseDeltas());
        if(heat.material == null) heat.material = HeatControl.defaultBlock;
        if(heat.mass == -1) heat.mass = HeatControl.defaultMass;
    }

    @Override
    public Seq<UpgradeEntry> entries() {
        return entries;
    }

    @Override
    public Seq<UpgradeDrawer> drawers() {
        return null;
    }

    @Override
    public BlockHeatModule heat() {
        return heat;
    }
}
