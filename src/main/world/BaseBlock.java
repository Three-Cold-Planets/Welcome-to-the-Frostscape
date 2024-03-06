package main.world;

import arc.math.geom.Point2;
import arc.struct.Seq;
import main.world.blocks.drawers.UpgradeDrawer;
import main.world.systems.heat.HeatControl;
import main.world.systems.upgrades.Upgrade;
import main.world.systems.upgrades.UpgradeEntry;
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

    public class HeatPart{
        //The material that this part is made from
        public HeatControl.MaterialPreset preset;

        //Mass of the part. Only affects newly constructed buildings.
        public float mass;

        //All the positions that this state will update.
        public transient Point2[] envUpdates;
        public transient int[] internalUpdates;
    }
}
