package main.world.blocks.core;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import main.type.upgrade.UpgradeableBuilding;
import main.world.BaseBlockType;
import main.world.UpgradesType;
import main.world.blocks.drawers.UpgradeDrawer;
import main.world.module.BlockHeatModule;
import main.world.systems.heat.HeatControl;
import main.world.systems.upgrades.Upgrade;
import main.world.systems.upgrades.UpgradeEntry;
import main.world.systems.upgrades.UpgradeModule;
import main.world.systems.upgrades.UpgradeState;
import mindustry.gen.Building;
import mindustry.world.blocks.storage.CoreBlock;

//TODO: Implement heat for the core
public class BaseCore extends CoreBlock implements BaseBlockType {
    public BaseCore(String name) {
        super(name);
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

    public class BaseCoreBuild extends CoreBuild implements UpgradeableBuilding {
        @Override
        public void upgraded(UpgradeState state) {

        }

        @Override
        public UpgradesType type() {
            return (UpgradesType) block;
        }

        public float
                damageMultiplier = 1,
                healthMultiplier = 1,
                speedMultiplier = 1,
                reloadMultiplier = 1,
                rangeMultiplier = 1,
                buildSpeedMultiplier = 1;


        @Override
        public void updateTile() {
            super.updateTile();
            resetDeltas();
            upgrades.update();
        }

        @Override
        public void draw() {
            super.draw();
        }

        @Override
        public void damage(float damage) {
            super.damage(damage/healthMultiplier);
        }

        @Override
        public void heal(float amount) {
            super.heal(amount/healthMultiplier);
        }

        public UpgradeModule upgrades = new UpgradeModule(this);

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

        @Override
        public void applyDeltas(UpgradeState state) {
            UpgradeEntry entry = type().entries().find(e -> e.upgrade == state.upgrade);
            if(entry == null) return;
            damageMultiplier *= entry.damageMultiplier[state.level];
            healthMultiplier *= entry.healthMultiplier[state.level];
            speedMultiplier *= entry.speedMultiplier[state.level];
            reloadMultiplier *= entry.reloadMultiplier[state.level];
            rangeMultiplier *= entry.rangeMultiplier[state.level];
            buildSpeedMultiplier *= entry.buildSpeedMultiplier[state.level];
        }

        @Override
        public void resetDeltas() {
            damageMultiplier = healthMultiplier = speedMultiplier = reloadMultiplier = rangeMultiplier = buildSpeedMultiplier = 1;
        }

        @Override
        public Building self() {
            return this;
        }
    }
}