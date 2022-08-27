package frostscape.world;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.type.upgrade.Upgrade;
import frostscape.type.upgrade.Upgradeable;
import frostscape.world.module.UpgradeModule;
import frostscape.world.upgrades.UpgradeState;
import frostscape.world.upgrades.UpgradeState.ProgressType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;

public class FrostscapeBuilding extends Building implements Upgradeable {
    public float
            damageMultiplier,
            healthMultiplier,
            speedMultiplier,
            reloadMultiplier,
            buildSpeedMultiplier;

    @Override
    public void update() {
        resetDeltas();
        upgrades.update(this);
        super.update();
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
        damageMultiplier += state.upgrade.damageMultiplier[state.level];
        healthMultiplier += state.upgrade.healthMultiplier[state.level];
        speedMultiplier += state.upgrade.speedMultiplier[state.level];
        reloadMultiplier += state.upgrade.reloadMultiplier[state.level];
        buildSpeedMultiplier += state.upgrade.buildSpeedMultiplier[state.level];
    }

    @Override
    public void resetDeltas() {
        damageMultiplier = healthMultiplier = speedMultiplier = reloadMultiplier = buildSpeedMultiplier = 1;
    }
}
