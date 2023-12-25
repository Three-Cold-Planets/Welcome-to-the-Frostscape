package main.world.blocks.core;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import main.type.upgrade.UpgradeableBuilding;
import main.world.UpgradesBlock;
import main.world.UpgradesType;
import main.world.blocks.drawers.UpgradeDrawer;
import main.world.systems.upgrades.UpgradeEntry;
import main.world.systems.upgrades.UpgradeModule;
import main.world.systems.upgrades.UpgradeState;
import mindustry.gen.Building;
import mindustry.world.blocks.storage.CoreBlock;

public class BaseCore extends CoreBlock implements UpgradesBlock {
    public BaseCore(String name) {
        super(name);
    }


    public Seq<UpgradeEntry> entries = new Seq<>();

    @Override
    public Seq<UpgradeEntry> entries() {
        return entries;
    }


    @Override
    public boolean isVisible() {
        return super.isVisible();
    }

    @Override
    public Seq<UpgradeDrawer> drawers() {
        return null;
    }

    @Override
    public void init() {
        super.init();
        entries.each(e -> e.initialiseDeltas());
    }

    public class BaseCoreBuild extends CoreBuild implements UpgradeableBuilding {
        @Override
        public void upgraded() {

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