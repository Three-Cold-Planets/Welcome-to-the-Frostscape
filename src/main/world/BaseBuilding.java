package main.world;

import arc.math.geom.Point2;
import arc.util.Log;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import main.gen.HeatBuilding;
import main.type.upgrade.UpgradeableBuilding;
import main.world.module.BlockHeatModule;
import main.world.module.HeatModule;
import main.world.systems.heat.EntityHeatState;
import main.world.systems.heat.HeatControl;
import main.world.systems.upgrades.UpgradeEntry;
import main.world.module.UpgradeModule;
import main.world.systems.upgrades.UpgradeState;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

import static main.world.module.BlockHeatModule.*;
import static main.world.systems.heat.HeatControl.*;

/** Base building entity for upgradeable blocks in Frostscape. Use as example/boilerplate for implementing upgrades in vanilla buildings */
public abstract class BaseBuilding extends HeatBuilding implements UpgradeableBuilding {

    public static BlockHeatModule blockHeat;

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

        for (int i = 0; i < blockHeat.entries.length; i++) {
            PartEntry entry = blockHeat.entries[i];
            EntityHeatState state = heat.states[i];
            entry.tileFlowmap.each(area -> {
                Point2 refrencePos = Tmp.p1.set(tileX() + area.x, tileY() + area.y);

                for (int y = 0; y < area.height; y++) {
                    for (int x = 0; x < area.width; x++) {
                        GridTile tile = getTile(refrencePos.x + x, refrencePos.y + y);
                        if(area.floorEnabled()) HeatControl.handleExchange(state, tile.floor);
                        if(area.blockEnabled()) HeatControl.handleExchange(state, tile.block);
                        if(area.airEnabled()) HeatControl.handleExchange(state, tile.air);
                    }
                }
            });
        }

        heat.finalizeEnergy();
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
        heat.write(write);
    }

    @Override
    public void readBase(Reads read) {
        super.readBase(read);
        upgrades.read(read);
        Log.info("reading!");
        heat.read(read, false);
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
    public BaseBlockType type() {
        return (BaseBlockType) block;
    }



    @Override
    public Building create(Block block, Team team) {
        Building returnBlock = super.create(block, team);
        Log.info("creating!");

        heat = new HeatModule();

        blockHeat = type().heat();

        HeatModule.setup(heat, blockHeat);

        return returnBlock;
    }

    @Override
    public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {

        return super.init(tile, team, shouldAdd, rotation);
    }
}
