package frostscape.world;

import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.type.upgrade.UpgradeableBuilding;
import frostscape.world.module.UpgradeModule;
import frostscape.world.upgrades.UpgradeEntry;
import frostscape.world.upgrades.UpgradeState;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Statusc;

public abstract class FrostscapeBuilding extends Building implements UpgradeableBuilding {

    public UpgradeModule upgrades = new UpgradeModule();
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
