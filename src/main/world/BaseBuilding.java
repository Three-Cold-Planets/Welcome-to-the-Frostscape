package main.world;

import arc.util.io.Reads;
import arc.util.io.Writes;
import main.entities.comp.HeatComp;
import main.gen.Heatc;
import main.type.upgrade.UpgradeableBuilding;
import main.world.systems.upgrades.UpgradeEntry;
import main.world.systems.upgrades.UpgradeModule;
import main.world.systems.upgrades.UpgradeState;
import mindustry.gen.Building;

/** Base building entity for upgradeable blocks in Frostscape. Use as example/boilerplate for implementing upgrades in vanilla buildings */
public abstract class BaseBuilding extends Building implements UpgradeableBuilding {

    public UpgradeModule upgrades = new UpgradeModule(this);
    public float damageMultiplier = 1,
            healthMultiplier = 1,
            armorMultiplier = 1,
            speedMultiplier = 1,
            reloadMultiplier = 1,
            rangeMultiplier = 1,
            buildSpeedMultiplier = 1;


    @Override
    public void upgraded(UpgradeState state) {
        resetDeltas();
        upgrades.update();
    }

    @Override
    public void updateTile() {
        super.updateTile();
        resetDeltas();
        upgrades.update();
    }

    @Override
    public void damage(float damage) {
        super.damage(damage/healthMultiplier);
    }

    @Override
    public void heal(float amount) {
        super.heal(amount/healthMultiplier);
    }

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
        if(!state.installed) return;
        UpgradeEntry entry = type().entries().find(e -> e.upgrade == state.upgrade);
        if(entry == null) return;
        damageMultiplier *= entry.damageMultiplier[state.level];
        healthMultiplier *= entry.healthMultiplier[state.level];
        armorMultiplier *= entry.armorMultiplier[state.level];
        speedMultiplier *= entry.speedMultiplier[state.level];
        reloadMultiplier *= entry.reloadMultiplier[state.level];
        rangeMultiplier *= entry.rangeMultiplier[state.level];
        buildSpeedMultiplier *= entry.buildSpeedMultiplier[state.level];
    }
    @Override
    public void resetDeltas() {
        damageMultiplier = healthMultiplier = armorMultiplier = speedMultiplier = reloadMultiplier = rangeMultiplier = buildSpeedMultiplier = 1;
    }

    @Override
    public Building self() {
        return this;
    }

    @Override
    public UpgradesType type() {
        return (UpgradesBlock) block;
    }
}
