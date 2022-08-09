package frostscape.world.blocks.drill;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import arc.util.*;
import frostscape.graphics.Draww;
import frostscape.math.Mathh;
import frostscape.math.MultiInterp;
import frostscape.world.FrostscapeBlock;
import frostscape.world.FrostscapeBuilding;
import frostscape.world.blocks.environment.CrackedBlock;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.*;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor.UpdateRenderState;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.Pump;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.meta.BlockGroup;

import java.util.Iterator;

import static frostscape.world.environment.FloorDataHandler.updateFloors;

public class CoreSiphon extends FrostscapeBlock {

    private static float returnCount = 0;
    public static float[] delays = new float[]{0, 45, 90, 105};
    
    public Interp visibility = new MultiInterp(new float[]{0, 0.8f}, new Interp[]{Interp.pow4Out, Interp.bounceIn});
    public float liquidPadding = 0.0F;
    public float warmupSpeed = 0.0025f;

    public Color heatColor = Color.valueOf("ff5512");
    public Liquid slagLiquid = Liquids.slag;
    public TextureRegion rotatorRegion, topRegion, capRegion, bottomRegion, liquidRegion, scaffoldingRegion;
    public TextureRegion[] beamRegions, beamGlowRegions;
    public float beamMoveDst = 9, beamSpeed = 65f;
    public ConsumeLiquid boost;
    public float pumpAmount = 0.25f;
    public float heatAlpha = 0.65f, heatRange = 0.35f, cycleSpeed = 85;

    public Effect drillFinish = new MultiEffect(){{
        effects = new Effect[]{
            Fx.mineImpact,
            Fx.mineImpactWave
        };
    }},
    updateEffect = Fx.pulverizeSmall;

    public float updateEffectChance = 0.04f, drillEffectRnd = 5, rotateSpeed = 1;

    public int beams = 4;

    public int drills = 4;

    public float circleRadius = 3, inactiveCircleRadius = 0.1f, circleTime = 34, headSpeed = 0.3f;

    public Vec2[] positions;

    public CoreSiphon(String name) {
        super(name);
        placeableLiquid = true;
        group = BlockGroup.drills;
        ambientSound = Sounds.drill;
        ambientSoundVolume = 0.018F;
        envEnabled |= 2;
    }

    @Override
    public void load() {
        super.load();
        rotatorRegion = Core.atlas.find(name +"-rotator", Core.atlas.find("clear"));
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find(name, Core.atlas.find("clear")));
        bottomRegion = Core.atlas.find(name + "-bottom", Core.atlas.find(name, Core.atlas.find("clear")));
        liquidRegion = Core.atlas.find(name + "-liquid", Core.atlas.find(name, Core.atlas.find("clear")));
        scaffoldingRegion = Core.atlas.find(name + "-scaffolding", Core.atlas.find("clear"));
        capRegion = Core.atlas.find(name + "-cap", Core.atlas.find(name, Core.atlas.find("clear")));
        beamRegions = new TextureRegion[beams];
        beamGlowRegions = new TextureRegion[beams];
        for (int i = 0; i < beams; i++) {
            beamRegions[i] = Core.atlas.find(name + "-beam" + (i));
            beamGlowRegions[i] = Core.atlas.find(name + "-beam-glow" + (i));
        }
    }

    public void setBars() {
        super.setBars();
        addLiquidBar(slagLiquid);
    }

    public boolean canMine(Tile tile) {
        return tile != null && !tile.block().isStatic() && tile.floor() instanceof CrackedBlock;
    }

    protected boolean canPump(Tile tile) {
        return tile != null && tile.floor().liquidDrop == slagLiquid;
    }


    public void drawPlace(int x, int y, int rotation, boolean valid) {

        drawPotentialLinks(x, y);
        drawOverlay((float)(x * 8) + offset, (float)(y * 8) + offset, rotation);

        Tile tile = Vars.world.tile(x, y);
        if (tile != null) {
            countOre(tile);
            if(returnCount == 0) drawPlaceText(Core.bundle.get("bar.nocrackedfloors"), x, y, valid);
            else drawPlaceText(Core.bundle.format("bar.crackedfloors", returnCount), x, y, valid);
        }
    }

    public CrackedBlock getCracked(Tile tile) {
        return canMine(tile) ? (CrackedBlock) tile.floor() : null;
    }

    protected void countOre(Tile tile) {
        returnCount = 0;

        Iterator var2 = tile.getLinkedTilesAs(this, tempTiles).iterator();

        while(var2.hasNext()) {
            Tile other = (Tile)var2.next();
            if (canMine(other)) {
                returnCount++;
            }
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        boolean cracked = super.canPlaceOn(tile, team, rotation);
        if (isMultiblock()) {
            Liquid last = null;
            Iterator ittr = tile.getLinkedTilesAs(this, tempTiles).iterator();

            while(ittr.hasNext()) {
                Tile other = (Tile)ittr.next();
                if (other.floor().liquidDrop != null) {
                    last = other.floor().liquidDrop;
                }
            }

            return last != null || cracked;
        } else {
            return canPump(tile) || cracked;
        }
    }

    public Tile nextTarget(Tile tile, IntSeq banned){
        Tile returnTile = null;
        CrackedBlock floor = null;
        Iterator var2 = tile.getLinkedTilesAs(this, tempTiles).iterator();

        while(var2.hasNext()) {
            Tile other = (Tile)var2.next();
            if (!banned.contains(other.pos()) && canMine(other) && (floor == null || getCracked(other).crackTime < floor.crackTime)) {
                returnTile = other;
                floor = getCracked(tile);
            }
        }
        return returnTile;
    }

    public class CoreSiphonBuild extends FrostscapeBuilding {

        public boolean doneDrilling = false;
        public IntMap<Integer> drillPositions = new IntMap<Integer>();
        public IntSeq drilling = new IntSeq();
        public DrillData[] drillHeads = new DrillData[drills];
        public float timePumped;
        public int state = 0;
        public float amount = 0.0F;
        public Liquid liquidDrop = null;
        public float timeDrilled, warmup, progress;

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            boolean onCracked = nextTarget(tile, drilling) != null;
            for (int i = 0; i < drills; i++) {
                drillHeads[i] = new DrillData() {{
                    pos = new Vec2();
                    targetPos = new Vec2();
                    progress = 0;
                    rotation = 0;
                    rotationSpeed = 0;
                    retired = !onCracked;
                }};
            }

            if(!onCracked) {
                state = 2;
                Time.run(15, () -> Sounds.buttonClick.at(x, y, 3.45f, 1));
            }
            return super.init(tile, team, shouldAdd, rotation);
        }

        @Override
        public void updateTile() {

            countOre(tile);
            if (timer(timerDump, 5.0F)) {
                //dump items here
            }

            if (efficiency > 0.0F) {
                warmup = Mathf.approachDelta(warmup, efficiency, warmupSpeed);
                progress += edelta();

                timeDrilled += edelta();
                if(state == 2) timePumped += edelta();

            } else {
                warmup = Mathf.approachDelta(warmup, 0.0F, warmupSpeed);
            }

            switch (state){
                case 0: {
                    //Handle drilling tiles first
                    state = 1;
                    boolean drilled = false;
                    for (int i = 0; i < drillHeads.length; i++) {
                        drilled = false;
                        DrillData drill = drillHeads[i];

                        if(drill.progress >= 1){
                            Tile targTile = Vars.world.tileWorld(drill.targetPos.x + x, drill.targetPos.y + y);
                            UpdateRenderState state = updateFloors.find(r -> r.tile == targTile);
                            if(state != null) updateFloors.remove(state);
                            CrackedBlock cracked = (CrackedBlock) targTile.floor();
                            targTile.setFloor(cracked.cracked);
                            drill.progress = 0;
                            drill.drilling = false;
                        }
                        if(!drill.drilling) {
                            Tile nextTarget = nextTarget(tile, drilling);
                            if (nextTarget != null) {
                                drill.targetPos.set(Tmp.v1.set(nextTarget).sub(x, y));
                                drill.progress = 0;
                                drill.curFloor = getCracked(nextTarget);
                                drill.drilling = true;
                                drilling.add(nextTarget.pos());
                                state = 0;
                            } else {
                                Vec2 pos = drill.pos;
                                float per = timeDrilled + i * 360 / drillHeads.length;

                                Tmp.v1.set(Mathf.sin(per, circleTime, (efficiency() > 0.001F ? 1 : inactiveCircleRadius)) + positions[i].x, Mathf.sin(per + 90, circleTime, circleRadius) + positions[i].y);
                                drill.pos = Mathh.moveToward(pos, Tmp.v1, headSpeed * warmup * Time.delta);
                            }
                        }
                        else{
                            if(drill.pos.within(drill.targetPos, 1)) {
                                drill.progress += 1 / drill.curFloor.crackTime * warmup * Time.delta;
                                drilled = true;
                                if (Mathf.chanceDelta(updateEffectChance * warmup)) {
                                    updateEffect.at(drill.pos.x + x + Mathf.range(drillEffectRnd * 2.0F), drill.pos.y + y + Mathf.range(drillEffectRnd * 2.0F));
                                }
                            }
                            else{
                                Mathh.moveToward(drill.pos, drill.targetPos, headSpeed * warmup * Time.delta);
                            }
                            state = 0;
                        }

                        drill.rotationSpeed = Mathf.approachDelta(drill.rotationSpeed, drilled ? rotateSpeed * 3 : rotateSpeed, warmup * delta());
                        drill.rotation += drill.rotationSpeed;
                        if(drilled) state = 0;
                    }
                    break;
                }
                case 1: {
                    state = 2;
                    //extract slag, retract drillheads and retire the drills
                    for(int i = 0; i < drillHeads.length; i++){
                        DrillData drill = drillHeads[i];
                        if(!drill.retired) {
                            state = 1;
                            drill.rotationSpeed = Mathf.approachDelta(drill.rotationSpeed, 0, (warmup * delta())/5);
                            drill.rotation += drill.rotationSpeed;
                            Mathh.moveToward(drill.pos, Tmp.v1.set(0, 0), headSpeed * Time.delta);
                            if (drill.pos.within(Tmp.v1, 1)) {
                                drill.retired = true;
                                Fx.explosion.at(x, y);
                                Sounds.explosion.at(x, y, 1, 1);
                            }
                        }
                    }
                    if(state == 2){
                        updateProximity();
                        Sounds.buttonClick.at(x, y, 3.45f, 1);
                        drillFinish.at(x, y);
                        warmup = 0;
                    }
                    break;
                }
                case 2: {
                    if (efficiency > 0.0F && liquidDrop != null) {
                        float maxPump = Math.min(liquidCapacity - liquids.get(liquidDrop), amount * pumpAmount * edelta());
                        liquids.add(liquidDrop, maxPump);
                        timePumped += warmup * delta();
                    }

                    break;
                }
            }
        }

        @Override
        public void drawLight() {
            return;
        }

        @Override
        public void draw() {
            for (int i = 0; i < drillHeads.length; i++) {
                DrillData drill = drillHeads[i];
                if(!drill.retired) {
                    float alpha = 1 - Interp.slope.apply(drill.progress) * Interp.slope.apply(drill.progress);
                    Draw.alpha(alpha);
                    Drawf.spinSprite(rotatorRegion, x + drill.pos.x, y + drill.pos.y, drill.rotation);

                    Draw.alpha(1);
                    Draww.drawChain(scaffoldingRegion, x, y, x + drill.pos.x, y + drill.pos.y, -90);
                    Draw.rect(capRegion, x + drill.pos.x, y + drill.pos.y, -90 + drill.progress * 360);
                }
            }

            Draw.rect(bottomRegion, x, y);
            if (liquids.get(slagLiquid) > 0.001F) {
                LiquidBlock.drawTiledFrames(size, x, y, liquidPadding, slagLiquid, liquids.get(slagLiquid)/liquidCapacity);
            }
            float coolantAmount = liquids.get(boost.liquid)/liquidCapacity;
            Drawf.liquid(liquidRegion, x, y, coolantAmount, boost.liquid.color);

            Draw.alpha(1);

            float a = Mathf.sin(timePumped, cycleSpeed, 1);
            float alpha = (heatAlpha + heatRange * a) * warmup;
            for (int i = 0; i < beams; i++) {
                int j = i + 1;
                float ox = Geometry.d4x(j), oy = Geometry.d4y(j);
                float offset = Mathf.sin(Math.max(timePumped - delays[i], 0), beamSpeed, beamMoveDst);

                Draw.rect(beamRegions[i], x + offset * ox, y + offset * oy, 0);
                if(state == 2){
                    Draw.blend(Blending.additive);
                    Draw.color(Tmp.c1.set(heatColor).a(alpha));
                    Draw.rect(beamGlowRegions[i], x + offset * ox, y + offset * oy, 0);
                    Draw.color();
                    Draw.alpha(1);
                    Draw.blend();
                }
            }
            Draw.rect(topRegion, x, y, 0);
        }

        public void onProximityUpdate() {
            super.onProximityUpdate();
            amount = 0.0F;
            liquidDrop = null;
            Iterator ittr = tile.getLinkedTiles(Pump.tempTiles).iterator();

            while(ittr.hasNext()) {
                Tile other = (Tile)ittr.next();
                if (canPump(other)) {
                    liquidDrop = other.floor().liquidDrop;
                    amount += other.floor().liquidMultiplier;
                }
            }

        }
    }

    public class DrillData{
        public float rotation;
        public float rotationSpeed;
        public float progress;
        public Vec2 pos;
        public Vec2 targetPos;
        public boolean drilling;
        public CrackedBlock curFloor;
        public boolean retired;
    }
}
