package main.world;

import arc.struct.Seq;
import main.world.blocks.drawers.UpgradeDrawer;
import main.world.systems.upgrades.Upgrade;
import main.world.systems.upgrades.UpgradeEntry;
import mindustry.world.Block;

/** Base building block for upgradeable blocks in Frostscape. Use as example/boilerplate for implementing upgrades in vanilla blocks */
public class BaseBlock extends Block implements UpgradesBlock{
    public BaseBlock(String name) {
        super(name);
        this.update = true;
        this.solid = true;
    }


    public final Seq<Upgrade> upgrades = new Seq<>();

    public Seq<UpgradeEntry> entries = new Seq<>();

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
    }


    @Override
    public Seq<UpgradeEntry> entries() {
        return entries;
    }

    @Override
    public Seq<UpgradeDrawer> drawers() {
        return null;
    }
}
