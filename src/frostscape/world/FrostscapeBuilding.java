package frostscape.world;

import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.type.upgrade.UpgradeableBuilding;
import frostscape.world.module.UpgradeModule;
import frostscape.world.upgrades.UpgradeState;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Statusc;

public class FrostscapeBuilding extends Building implements UpgradeableBuilding {
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
        upgrades.update(this);
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

    @Override
    public void applyDeltas(UpgradeState state) {
        damageMultiplier *= state.upgrade.damageMultiplier[state.level];
        healthMultiplier *= state.upgrade.healthMultiplier[state.level];
        speedMultiplier *= state.upgrade.speedMultiplier[state.level];
        reloadMultiplier *= state.upgrade.reloadMultiplier[state.level];
        rangeMultiplier *= state.upgrade.rangeMultiplier[state.level];
        buildSpeedMultiplier *= state.upgrade.buildSpeedMultiplier[state.level];
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
