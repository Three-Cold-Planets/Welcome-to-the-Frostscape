package main.world;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.ColorCodes;
import arc.util.Log;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pools;
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
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Tile;

import static main.world.module.BlockHeatModule.*;
import static main.world.systems.heat.HeatControl.*;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

/** Base building entity for upgradeable blocks in Frostscape. Use as example/boilerplate for implementing upgrades in vanilla buildings */
public abstract class BaseBuilding extends HeatBuilding implements UpgradeableBuilding {

    public static Rect rotRect = new Rect();
    public BlockHeatModule blockHeat;

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

                rotRect.set(area.x, area.y, area.width, area.height);
                rotRect.x -= 0.5f;
                rotRect.y -= 0.5f;

                if(area.rotate) {
                    //bottom left point
                    Tmp.v1.set(rotRect.x, rotRect.y);

                    //Top right point
                    Tmp.v2.set(rotRect.x + rotRect.width, rotRect.y + rotRect.height);


                    float offsetFactor = -Mathf.mod((block.size + 1.0f) / 2.0f, 1);
                    Log.info(offsetFactor);

                    //Bring the points to the center
                    Tmp.v1.add(offsetFactor, offsetFactor);
                    Tmp.v2.add(offsetFactor, offsetFactor);

                    Tmp.v1.rotate(rotation * 90);
                    Tmp.v2.rotate(rotation * 90);

                    //Bring points back to being an offset
                    Tmp.v1.sub(offsetFactor, offsetFactor);
                    Tmp.v2.sub(offsetFactor, offsetFactor);

                    float minX = Math.min(Tmp.v1.x, Tmp.v2.x), minY = Math.min(Tmp.v1.y, Tmp.v2.y);
                    float maxX = Math.max(Tmp.v1.x, Tmp.v2.x), maxY = Math.max(Tmp.v1.y, Tmp.v2.y);

                    rotRect.set(minX, minY, maxX - minX, maxY - minY);
                    Fx.plasticburn.at(tile.worldx() + minX * tilesize, tile.worldy() + minY * tilesize);
                    Fx.plasticburn.at(tile.worldx() + maxX * tilesize, tile.worldy() + maxY * tilesize);
                }

                Point2 refrencePos = Tmp.p1.set(tileX() + Mathf.ceil(rotRect.x), tileY() + Mathf.ceil(rotRect.y));
                Fx.smoke.at(refrencePos.x * tilesize, refrencePos.y * tilesize);

                for (int y = 0; y < rotRect.height; y++) {
                    for (int x = 0; x < rotRect.width; x++) {
                        Fx.placeBlock.at((refrencePos.x + x) * tilesize, (refrencePos.y + y) * tilesize, 1);
                        GridTile tile = getTile(refrencePos.x + x, refrencePos.y + y);
                        if(area.floorEnabled()) HeatControl.handleExchange(state, tile.floor, area.rate);
                        if(area.blockEnabled()) HeatControl.handleExchange(state, tile.block, area.rate);
                        if(area.airEnabled()) HeatControl.handleExchange(state, tile.air, area.rate);
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
