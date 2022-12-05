package frostscape.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Tmp;
import frostscape.entities.bullet.BouncyBulletType;
import frostscape.util.DrawUtils;
import frostscape.world.blocks.core.CoreBunker;
import frostscape.world.blocks.drawers.ReflectorDrawer;
import frostscape.world.blocks.environment.ParticleFloor;
import frostscape.world.blocks.light.ReflectiveWall;
import frostscape.world.blocks.light.SolarReflector;
import frostscape.world.blocks.core.BuildBeamCore;
import frostscape.world.blocks.core.FrostscapeCore;
import frostscape.world.blocks.defense.MinRangeTurret;
import frostscape.world.blocks.defense.ThermalMine;
import frostscape.world.blocks.drawers.DrawUpgradePart;
import frostscape.world.blocks.drill.CoreSiphon;
import frostscape.world.blocks.environment.CrackedBlock;
import frostscape.world.blocks.environment.SteamVentProp;
import frostscape.world.light.LightBeams;
import frostscape.world.meta.stat.FrostStats;
import frostscape.world.upgrades.UpgradeEntry;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Building;
import mindustry.gen.EntityMapping;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.ContinuousTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.environment.*;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Env;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static frostscape.Frostscape.NAME;

public class FrostBlocks {
    //Base names
    public static final String
        baseRock = NAME + "-base-rock-0",
        baseReflector = NAME + "-base-reflector-0";
    public static Floor sulphuricWater, deepSulphuricWater, sulphuricAndesiteWater, sulphuricGraystoneWater,
            frostStone, frostSnow,
            andesiteFloor, volcanicAndesiteFloor, volcanicPebbledAndesiteFloor, sulphanatedAndesite,
            graystoneFloor, graystoneSlatedFloor,
            volcanicDaciteFloor, roughVolcanicDaciteFloor,
            tephra;

    public static Prop algae, frostCluster, wornBoulderLarge, wornBoulderHuge;

    public static CrackedBlock crackedAndesiteFloor, fracturedAndesiteFloor;
    public static StaticWall frostWall, volcanicAndesiteWall, magnetiteAndesite, grayWall, sulphurGraystone, wornWall, volcanicDaciteWall;
    public static StaticTree tephraWall;
    public static SteamVentProp frostVent;

    public static CoreSiphon coreSiphon;
    public static ItemTurret pyroclast;
    public static CoreBunker coreBunker;
    public static ThermalMine thermalLandmine;

    public static SolarReflector solarReflector;

    public static ReflectiveWall reflectiveWall;

    public static void load(){

        sulphuricWater = new ParticleFloor("sulphuric-water"){{
            isLiquid = true;
            liquidDrop = Liquids.water;
            variants = 4;
            effect = Fxf.sulphuricSmoke;
            chance = 0.00012f;
            status = FrostStatusEffects.causticCoating;
            statusDuration = 120.0F;
            cacheLayer = CacheLayer.water;
            albedo = 0.9F;
            supportsOverlay = true;
            speedMultiplier = 0.8f;
        }};

        deepSulphuricWater = new ParticleFloor("deep-sulphuric-water"){{
            isLiquid = true;
            liquidDrop = Liquids.water;
            variants = 4;
            effect = Fxf.sulphuricSmoke;
            chance = 0.00012f;
            status = FrostStatusEffects.causticCoating;
            statusDuration = 120.0F;
            drownTime = 200.0F;
            cacheLayer = CacheLayer.water;
            albedo = 0.9F;
            supportsOverlay = true;
            speedMultiplier = 0.6f;
        }};

        sulphuricAndesiteWater = new ParticleFloor("sulphuric-andesite-water"){{
            isLiquid = true;
            liquidDrop = Liquids.water;
            variants = 3;
            effect = Fxf.sulphuricSmoke;
            chance = 0.00012f;
            status = FrostStatusEffects.causticCoating;
            statusDuration = 120.0F;
            cacheLayer = CacheLayer.water;
            albedo = 0.9F;
            supportsOverlay = true;
            speedMultiplier = 0.92f;
        }};

        sulphuricGraystoneWater = new ParticleFloor("sulphuric-graystone-water"){{
            isLiquid = true;
            liquidDrop = Liquids.water;
            variants = 3;
            effect = Fxf.sulphuricSmoke;
            chance = 0.00012f;
            status = FrostStatusEffects.causticCoating;
            statusDuration = 120.0F;
            cacheLayer = CacheLayer.water;
            albedo = 0.9F;
            supportsOverlay = true;
            speedMultiplier = 0.92f;
        }};

        frostStone = new Floor("frost-stone"){{
            variants = 4;
        }};

        frostSnow = new Floor("frost-snow"){{
            variants = 3;
        }};

        frostVent = new SteamVentProp("frost-vent"){{
            parent = blendGroup = frostStone;
            hasShadow = false;
            offsets = new Point2[25];
            int size = 5;
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    offsets[x * 5 + y] = new Point2(x - 2, y - 2);
                }
            }

            effects = new EffectData[]{
                    new EffectData(){{
                        effect = Fxf.steamEffect(100, 3);
                        pos = new Point2(-32/4, 32/4);
                        posRand = new Point2(4, 4);
                        chance = 0.15f;
                        rotation = 0;
                    }}
            };
            clipSize = 170;
            variants = 1;
        }};

        andesiteFloor = new Floor("andesite-floor"){{
            variants = 3;
        }};

        volcanicAndesiteFloor = new Floor("volcanic-andesite-floor"){{
            variants = 3;
        }};

        volcanicPebbledAndesiteFloor = new Floor("volcanic-pebbled-andesite-floor"){{
            variants = 4;
            blendGroup = volcanicAndesiteFloor;
        }};

        sulphanatedAndesite = new Floor("sulphanated-andesite-floor"){{
            variants = 3;
        }};

        graystoneFloor = new Floor("graystone-floor"){{
            variants = 3;
        }};

        graystoneSlatedFloor = new Floor("graystone-slated-floor"){{
            variants = 8;
        }};

        volcanicDaciteFloor = new Floor("volcanic-dacite-floor"){{
            variants = 4;
        }};

        roughVolcanicDaciteFloor = new Floor("rough-volcanic-dacite-floor"){{
            variants = 4;
        }};

        tephra = new Floor("tephra"){{
            variants = 3;
        }};

        crackedAndesiteFloor = new CrackedBlock("cracked-andesite-floor"){{
            variants = 5;
            blendGroup = volcanicAndesiteFloor;
            blinkTimeRange = 60 * 5;
            maxBlinkTime = 60 * 9;
        }};

        fracturedAndesiteFloor = new CrackedBlock("fractured-andesite-floor"){{
            variants = 3;
            blendGroup = volcanicAndesiteFloor;
            blinkTimeRange = 60 * 3;
            maxBlinkTime = 60 * 6;
        }};

        frostWall = new StaticWall("frost-wall"){{
            variants = 3;
        }};

        volcanicAndesiteWall = new StaticWall("volcanic-andesite-wall"){{
            variants = 3;
        }};

        magnetiteAndesite = new StaticWall("magnetite-andesite"){{
            variants = 3;
            itemDrop = FrostItems.magnetite;
        }};

        grayWall = new StaticWall("graystone-wall"){{
            variants = 4;
        }};

        sulphurGraystone = new StaticWall("sulphur-graystone"){{
            variants = 3;
            itemDrop = FrostItems.sulphur;
        }};

        wornWall = new StaticWall("worn-wall"){{
            variants = 3;
        }};

        volcanicDaciteWall = new StaticWall("volcanic-dacite-wall"){{
            variants = 2;
        }};

        tephraWall = new StaticTree("tephra-wall"){{
            variants = 2;
        }};

        algae = new WobbleProp("algae"){{
            variants = 3;
            hasShadow = false;
        }};

        frostCluster = new Prop("frost-cluster"){{
            variants = 3;
            size = 1;
            breakable = alwaysReplace = false;
            solid = true;
            BulletType p = null;
        }};

        wornBoulderLarge = new Prop("worn-boulder-large"){{
            variants = 0;
            size = 1;
            breakable = alwaysReplace = false;
        }};

        wornBoulderHuge = new Prop("worn-boulder-huge"){{
            variants = 0;
            size = 1;
            breakable = alwaysReplace = false;
        }};

        coreSiphon = new CoreSiphon("core-siphon"){{
            requirements(Category.production, ItemStack.with());
            liquidPadding = 6;
            this.size = 7;
            liquidCapacity = 1000;
            boost = consumeLiquid(Liquids.water, 0.05F);
            heatColor = new Color(Palf.heat).a(0.35f);
            boost.boost();
            drillEffectRnd = 0.1f;
            updateEffectChance = 0.15f;
            rotateSpeed = 5;
            drills = 4;
            positions = new Vec2[]{
                    new Vec2(44/4, 44/4),
                    new Vec2(44/4, -44/4),
                    new Vec2(-44/4, 44/4),
                    new Vec2(-44/4, -44/4)
            };
        }};

        pyroclast = new MinRangeTurret("pyroclast"){{
            requirements(Category.turret, ItemStack.with());
            size = 3;
            health = 54 * size * size;
            reload = 235;
            minRange = 160;
            range = 350;
            velocityRnd = 0.05f;
            recoil = 6;
            rotateSpeed = 2.5f;
            cooldownTime = 75;
            shootY = -8;
            shootWarmupSpeed = 0.08f;
            minWarmup = 0.9f;
            drawer = new DrawTurret("elevated-"){{
                Color heatc = Pal.turretHeat;
                heatColor = heatc;
                liquidDraw = Liquids.oil;
                liquidCapacity = 300;
                parts.addAll(
                        new RegionPart("-liquid-base"){{
                            progress = PartProgress.recoil;
                            heatColor = Color.clear;
                            mirror = false;
                            layerOffset = -0.00002f;
                            outlineLayerOffset = -0.00002f;
                        }},
                        new RegionPart("-back"){{
                            progress = PartProgress.warmup;
                            mirror = true;
                            moveRot = -15f;
                            x = (30 - 6) / 4f;
                            y = (-34.5f + 6) / 4f;
                            moveX = 6/4;
                            moveY = -6/4;
                            under = true;
                            heatColor = Color.clear;
                            children = Seq.with(
                                    new RegionPart("-back"){
                                        {
                                            progress = PartProgress.recoil;
                                            mirror = true;
                                            moveRot = 360 -35f;
                                            x = 0;
                                            y = 0;
                                            moveX = 6 / 4;
                                            moveY = -6 / 4;
                                            under = true;
                                            heatColor = Color.clear;
                                            children = Seq.with(
                                                    new RegionPart("-fang"){
                                                        {
                                                            progress = PartProgress.warmup;
                                                            mirror = true;
                                                            moveRot = -15f;
                                                            x = 0;
                                                            y = 0;
                                                            moveX = 4 / 4;
                                                            moveY = -4 / 4;
                                                            under = true;
                                                            heatColor = Color.clear;
                                                        }}
                                            );
                                        }}
                            );
                        }},
                        new RegionPart("-barrel"){{
                            progress = PartProgress.recoil.curve(Interp.pow2In);
                            moveY = -9;
                            heatColor = Color.valueOf("f03b0e");
                            mirror = false;
                            layerOffset = -0.00001f;
                            outlineLayerOffset = -0.00002f;
                        }}
                );
            }};
            outlineColor = Pal.darkOutline;

            ammo(
                    Items.pyratite,
                    new BulletType(){{
                        speed = 3.5f;
                        lifetime = 200;
                        instantDisappear = true;
                        for (int i = 0; i < 6; i++) {
                            final float j = i;
                            spawnBullets.add(new BouncyBulletType(3.5f + j/7, 5, "shell"){{
                                                 collidesBounce = true;
                                                 pierceBuilding = false;
                                                 lifetime = 200;
                                                 drag = 0.006f;
                                                 minLife = 55f;
                                                 hitEffect = Fx.blastExplosion;
                                                 despawnEffect = Fx.blastExplosion;
                                                 width = 6;
                                                 height = 6;
                                                 shrinkX = 0.9f;
                                                 shrinkY = 0.9f;
                                                 status = FrostStatusEffects.napalm;
                                                 statusDuration = 12f * 60f;
                                                 gravity = 0.00216f;
                                                 startingLift = 0.066f + j/100;
                                                 bounceShake = 0.7f;
                                                 bounceEfficiency = 0.85f;
                                                 bounceForce = 10;
                                                 bounceCap = 0;
                                                 keepLift = false;
                                                 keepHeight = false;
                                                 frontColor = Pal.lightishOrange;
                                                 backColor = Pal.lightOrange;
                                                 hitShake = 3.2f;
                                                 bounceEffect = Fx.explosion;
                                                 incendAmount = 2;
                                                 incendChance = 1;
                                                 puddleLiquid = Liquids.oil;
                                                 puddleAmount = 25;
                                                 puddles = 1;
                                                 splashDamage = 15;
                                                 splashDamageRadius = 16;
                                                 knockback = 1;
                                                 trailEffect = Fxf.emberTrail;
                                                 trailChance = 0.65f;
                                                 fragBullets = 3;
                                                 fragBullet = FrostBullets.pyraGel.fragBullet;
                                                 hitSound = Sounds.explosion;
                                             }}
                            );
                        }
                    }},
                    Items.blastCompound,
                    new BouncyBulletType(3.5f, 10, NAME + "-napalm-canister"){{
                        frontColor = backColor = Color.white;
                        lifetime = 17.5f;
                        drag = 0.016f;
                        hitEffect = Fx.blastExplosion;
                        despawnEffect = Fx.blastExplosion;
                        width = 8;
                        height = 8;
                        shrinkX = 1f;
                        shrinkY = 1f;
                        status = StatusEffects.burning;
                        statusDuration = 12f * 60f;
                        gravity = 0.00144f;
                        startingLift = 0.0576f;
                        bounceShake = 0.7f;
                        bounceEfficiency = 0.65f;
                        bounceForce = 10;
                        bounceCap = 0;
                        hitShake = 6.2f;
                        hittable = true;
                        hitSound = Sounds.bang;
                        bounceEffect = new MultiEffect(){{
                            effects = new Effect[]{
                                    Fx.unitLandSmall,
                                    Fx.fireHit,
                                    new Effect(15, e -> {
                                        Draw.color(Pal.lightPyraFlame);
                                        Draw.alpha(e.fout() * e.fout());
                                        Lines.circle(e.x, e.y, e.finpow() * 16);
                                    })
                            };
                        }};
                        hitEffect = new Effect(85, e -> {
                            e.scaled(15, e1 -> {

                                Lines.stroke(e1.fout() * 0.65f);
                                color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
                                Lines.circle(e1.x, e1.y, e1.finpow() * 35);

                                Lines.stroke(e1.fout() * 3);
                                Angles.randLenVectors(e.id + 1, (int) (Mathf.randomSeed(e.id, 3) + 5), e1.fin() * 54 + 6, e.rotation, 54, (x, y) -> {
                                    Lines.line(e.x + x * 0.3f, e.y + y * 0.3f, e.x + x, e.y + y);
                                });
                            });

                            color(Pal.stoneGray);
                            randLenVectors(e.id, 5, 3f + e.fin() * 8f, (x, y) -> {
                                Fill.square(e.x + x, e.y + y, e.fout() * 2f + 0.5f, 45);
                            });

                            color(Color.gray);
                            Draw.alpha(e.fout());
                            Angles.randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 3) + 5), e.fin() * 54 + 6, (x, y) -> {
                                Fill.circle(e.x + x, e.y + y, 5 * e.fout(Interp.pow4));
                            });

                        });

                        trailEffect = new Effect(55, e -> {
                            float h = (float) e.data;
                            float radius = 8.8f * h;
                            float[] layers = new float[]{visualHeightMax, visualHeightMin};
                            Draw.color(Liquids.oil.color);
                            DrawUtils.speckOffset(e.x, e.y, h, e.fin() * e.lifetime, DrawUtils.smokeWeight, Tmp.v1);
                            float ox = Tmp.v1.x, oy = Tmp.v1.y;
                            Angles.randLenVectors(e.id + 1, (int) (Mathf.randomSeed(e.id, 3) + 1), e.fin() * 12, e.rotation, 35, (x, y) -> {
                                float visibility = h/visualHeightRange;
                                Draw.alpha(visibility * e.fout() * 0.23f);
                                Draw.z(layers[0]);
                                Fill.circle(x + ox, y + oy, radius);
                                visibility = 1 - visibility;
                                Draw.alpha(visibility * e.fout() * 0.23f);
                                Draw.z(layers[1]);
                                Fill.circle(x + ox, y + oy, radius);
                            });
                        });
                        trailChance = 0.65f;
                        trailRotation = true;
                        fragBullet = new BouncyBulletType(4.5f, 5, "shell"){{
                            collidesBounce = true;
                            pierceBuilding = false;
                            lifetime = 120;
                            drag = 0.006f;
                            minLife = 55f;
                            hitEffect = Fx.blastExplosion;
                            despawnEffect = Fx.blastExplosion;
                            width = 6;
                            height = 6;
                            shrinkX = 0.9f;
                            shrinkY = 0.9f;
                            status = FrostStatusEffects.napalm;
                            statusDuration = 12f * 60f;
                            gravity = 0.00328f;
                            bounceShake = 0.7f;
                            bounceEfficiency = 0.65f;
                            bounceForce = 10;
                            bounceCap = 3;
                            keepLift = true;
                            frontColor = Pal.lightishOrange;
                            backColor = Pal.lightOrange;
                            hitShake = 3.2f;
                            bounceEffect = Fx.explosion;
                            incendAmount = 2;
                            incendChance = 1;
                            puddleLiquid = Liquids.oil;
                            puddleAmount = 25;
                            puddles = 1;
                            splashDamage = 15;
                            splashDamageRadius = 16;
                            knockback = 1;
                            trailEffect = Fxf.emberTrail;
                            trailChance = 0.65f;
                            fragBullets = 3;
                            fragBullet = FrostBullets.pyraGel.fragBullet;
                            hitSound = Sounds.explosion;
                            homing = velHomeDefault;
                        }};
                        fragBullets = 5;
                        fragSpread = 5;
                        fragRandomSpread = 5;
                        fragLifeMin = 0.6f;
                        fragLifeMax = 1f;
                        fragVelocityMin = 0.6f;
                        fragVelocityMax = 1f;
                        fragLiftMin = 0.3f;
                        fragLiftMax = 2f;
                        splashDamage = 55;
                        splashDamageRadius = 16;
                        scaleLife = false;
                    }
                    }
            );
            consumeLiquids(new LiquidStack(Liquids.oil, 1.35f));

            shoot = new ShootSpread(){{
                shotDelay = 5;
                shots = 1;
                inaccuracy = 0;
                spread = 0;
            }};
            float passiveRise = 0.2f, rise = 0.85f, riseShort = 0.35f;
            shootSound = Sounds.artillery;
            shootEffect = new Effect(185, e -> {
                e.scaled(50, e1 -> {
                    Draw.color(Pal.lightPyraFlame);
                    Draw.alpha(e1.foutpow());

                    Lines.stroke(e1.fout() * 0.65f);
                    Lines.circle(e1.x, e1.y, e1.finpow() * 15);

                    Lines.stroke(e1.fout() * 3);
                    Angles.randLenVectors(e.id + 1, (int) (Mathf.randomSeed(e.id, 3) + 5), e1.fin() * 54 + 6, e.rotation, 35, (x, y) -> {
                        Lines.line(e.x + x * 0.3f, e.y + y * 0.3f, e.x + x, e.y + y);
                    });
                });

                Draw.color(Pal.darkerGray);
                Angles.randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 5) + 4), e.finpow() * 165, e.rotation, 25, (x, y) -> {
                    DrawUtils.speckOffset(e.x + x, e.y + y, e.fin() * passiveRise + rise * Mathf.dst(x, y)/165, e.fin() * e.lifetime, DrawUtils.smokeWeight, Tmp.v1);
                    Fill.circle(Tmp.v1.x, Tmp.v1.y, 5 * e.fout(Interp.pow4));
                });

                e.scaled(90, e1 -> {
                    Draw.color(Pal.gray);
                    Angles.randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 6) + 12), e1.finpow() * 205, e.rotation, 25, (x, y) -> {
                        DrawUtils.speckOffset(e.x + x, e.y + y, e.fin() * passiveRise + riseShort * Mathf.dst(x, y)/205, e.fin() * e.lifetime, DrawUtils.smokeWeight, Tmp.v1);
                        Fill.circle(Tmp.v1.x, Tmp.v1.y, 5 * e1.fout(Interp.pow4));
                    });
                });
            });
        }};

        coreBunker = new CoreBunker("core-bunker"){{

            consumeLiquid(Liquids.water, 0.01f);
            liquidPadding = 5;

            float centerOffset = 55/4, floatMirrorSide = 17/4;

            hitboxEdges = new float[]{
                    -floatMirrorSide, centerOffset,
                    floatMirrorSide, centerOffset,
                    centerOffset, floatMirrorSide,
                    centerOffset, -floatMirrorSide,
                    floatMirrorSide, -centerOffset,
                    -floatMirrorSide, -centerOffset,
                    -centerOffset, -floatMirrorSide,
                    -centerOffset, floatMirrorSide
            };


            requirements(Category.effect, ItemStack.empty);
            size = 5;
            mountPoses = new Seq<>();
            for (int i = 1; i < Geometry.d8.length; i += 2) {
                mountPoses.add(new Vec2(Geometry.d8[i].x * 26/2, Geometry.d8[i].y * 26/2));
            }
            units.addAll(
                    new UnitEntry(null, new ResearchedLockedCond(FrostResearch.improvedWelding), 180, UnitTypes.pulsar),
                    new UnitEntry(new ResearchedLockedCond(FrostResearch.improvedWelding), null, 300, UnitTypes.quasar)
            );
            entries.addAll(
                    new UpgradeEntry(FrostUpgrades.improvedBase){{
                        costs = new ItemStack[][]{
                                ItemStack.empty
                        };
                    }},
                    new UpgradeEntry(FrostUpgrades.improvedPayload){{
                        costs = new ItemStack[][]{
                                ItemStack.empty
                        };
                    }},
                    new UpgradeEntry(FrostUpgrades.improvedWelding){{
                        costs = new ItemStack[][]{
                                ItemStack.empty
                        };
                    }},
                    new UpgradeEntry(FrostUpgrades.INVINCIBLE){{
                        costs = new ItemStack[][]{
                                ItemStack.empty
                        };
                    }},
                    new UpgradeEntry(FrostUpgrades.wheeeez){{
                        costs = new ItemStack[][]{
                                ItemStack.empty
                        };
                    }}
            );
            defaultEntry = units.get(0);
        }};

        thermalLandmine = new ThermalMine("thermal-landmine"){{
            requirements(Category.effect, ItemStack.with(Items.graphite, 10, Items.silicon, 15, Items.pyratite, 15));
            health = 55;
            tileDamage = 0.75f;
            warmupSpeed = 0.002f;
            warmDownSpeed = 0.06f;
            entries.addAll(
                    new UpgradeEntry(FrostUpgrades.improvedBase){{
                        healthMultiplier = new float[]{
                                1.1f,
                                1.5f,
                                3.5f
                        };
                        speedMultiplier = new float[]{
                                1,
                                0.85f,
                                0.65f
                        };
                        costs = new ItemStack[][]{
                                ItemStack.with(Items.graphite, 5, Items.lead, 10),
                                ItemStack.with(Items.metaglass, 7),
                                ItemStack.with(Items.titanium, 10, Items.graphite, 15)
                        };
                    }},
                    new UpgradeEntry(FrostUpgrades.improvedPayload){{
                        damageMultiplier = new float[]{
                                1.2f,
                                1.5f,
                                1.8f
                        };
                        reloadMultiplier = new float[]{
                                1,
                                1.3f,
                                2
                        };
                        rangeMultiplier = new float[]{
                                1.5f,
                                2.1f,
                                2.8f
                        };
                        costs = new ItemStack[][]{
                                ItemStack.with(Items.coal, 5),
                                ItemStack.with(Items.pyratite, 10),
                                ItemStack.with(Items.coal, 10, Items.pyratite, 25)
                        };
                    }}
            );
            drawer = new DrawMulti(
                    new DrawUpgradePart(
                            NAME + "-thermal-landmine-base0",
                            new String[]{
                                    NAME + "-thermal-landmine-base0",
                                    NAME + "-thermal-landmine-base1",
                                    NAME + "-thermal-landmine-base2"
                            },
                            FrostUpgrades.improvedBase
                    ),
                    new DrawUpgradePart(
                            NAME + "-thermal-landmine-payload0",
                            new String[]{
                                    NAME + "-thermal-landmine-payload0",
                                    NAME + "-thermal-landmine-payload1",
                                    NAME + "-thermal-landmine-payload2"
                            },
                            FrostUpgrades.improvedPayload
                    )
            );
        }};

        solarReflector = new SolarReflector("solar-reflector"){{
            requirements(Category.effect, ItemStack.with());
            data = new LightBeams.ColorData(2.4f, 2f, 1.6f);
            drawer = new DrawMulti(
                    new DrawUpgradePart(
                            baseRock,
                            new String[]{
                                    baseReflector
                            },
                            FrostUpgrades.improvedBase
                    ),
                    new ReflectorDrawer(name, Layer.blockBuilding,  Layers.light + 0.1f)
            );

            entries.add(
                new UpgradeEntry(FrostUpgrades.improvedBase){{
                    healthMultiplier = new float[]{
                            3.5f
                    };
                    costs = new ItemStack[][]{
                            ItemStack.with(FrostItems.ferricPanels, 5)
                    };
                }}
            );
        }};

        reflectiveWall = new ReflectiveWall("reflective-wall"){{
            requirements(Category.effect, ItemStack.with());
        }};
    }
}