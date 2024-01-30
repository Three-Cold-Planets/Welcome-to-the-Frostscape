package main.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import main.entities.bullet.BouncyBulletType;
import main.entities.bullet.RicochetBulletType;
import main.entities.part.AccelPartProgress;
import main.entities.part.EffectPart;
import main.graphics.Layers;
import main.graphics.ModPal;
import main.math.Interps;
import main.math.Math3D;
import main.util.DrawUtils;
import main.world.blocks.PlugBlock;
import main.world.blocks.core.CoreBunker;
import main.world.blocks.defense.CrumblingWall;
import main.world.blocks.defense.LightningMine;
import main.world.blocks.defense.MinRangeTurret;
import main.world.blocks.defense.ThermalMine;
import main.world.blocks.drawers.DrawConstantLight;
import main.world.blocks.drawers.DrawGenerateLight;
import main.world.blocks.drawers.DrawUpgradePart;
import main.world.blocks.drawers.ReflectorDrawer;
import main.world.blocks.drill.CoreSiphon;
import main.world.blocks.environment.*;
import main.world.blocks.light.ReflectiveWall;
import main.world.blocks.light.SolarReflector;
import main.world.blocks.power.PowerPlug;
import main.world.systems.bank.ResourceBankHandler;
import main.world.systems.light.LightBeams;
import main.world.systems.upgrades.UpgradeEntry;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ExplosionBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.units.UnitCargoLoader;
import mindustry.world.blocks.units.UnitCargoUnloadPoint;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static main.Frostscape.NAME;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.Env.scorching;
import static mindustry.world.meta.Env.underwater;

public class FrostBlocks {
    //Base names
    public static final String
        baseRock = NAME + "-base-rock-0",
        baseReflector = NAME + "-base-reflector-0";

    public static Block

        //Serpulo Tech
        unitCargoLoader, unitCargoUnloadPoint,

        //environment

        //floors - complex
        grating, plating, platingCross, socket, powerSocket, powerSocketLarge, itemSocket, itemSocketLarge, liquidSocket, liquidSocketLarge,

        //floors - hollus
        sulphuricWater, deepSulphuricWater, sulphuricAndesiteWater, sulphuricGraystoneWater,
        sulphuricIce,
        frostStone, frostSnow,
        andesiteFloor, volcanicAndesiteFloor, volcanicPebbledAndesiteFloor, sulphanatedAndesite,
        crackedAndesiteFloor, fracturedAndesiteFloor,
        graystoneFloor, graystoneSlatedFloor,
        volcanicDaciteFloor, roughVolcanicDaciteFloor,
        tephra,

        //floors - volcanic moon
        maficFloor,

        //props - hollus
        algae, frostCluster, wornBoulderLarge, wornBoulderHuge, frostVent,

        //props - complex

        //props - volcanic moon

        maficBoulder,

        //walls - hollus
        frostWall, volcanicAndesiteWall, magnetiteAndesite, grayWall, sulphurGraystone, wornWall, volcanicDaciteWall, tephraWall,

        //walls - complex
        enclosureWall,

        //walls - volcanic moon
        maficStone,

    //production - hollus
    coreSiphon,

    //distribution - complex

    itemPlug,

    //fluids - complex

    liquidPlug,

    //storage - hollus
    coreBunker,

    //defense - hollus
    pyroclast, cryonis, rivulet,
    thermalLandmine, lightningMine,

    //light - hollus
    solarReflector,

    reflectiveWall,

    //power - complex
    powerPlug, powerPlugLarge, conductiveWall,

    //defense - complex
    stoneWall;

    public static void load(){
        //region internal
        //you REALLY don't want to mess with theese

        ResourceBankHandler.block = new Block("resource-bank-handler"){{
            update = false;
            destructible = false;
            consumesPower = true;
            hasPower = true;
            outputsPower = true;

            consumePowerBuffered(0);
        }};

        //endregion
        unitCargoLoader = new UnitCargoLoader("unit-cargo-loader"){{
            unitType = FrostUnits.serpieDrone;
            requirements(Category.distribution, with(Items.copper, 80, Items.lead, 35, Items.graphite, 50, Items.silicon, 40));

            size = 3;
            buildTime = 60f * 8f;
            envEnabled = 1;
            envDisabled = scorching + underwater;

            consumePower(8f / 60f);

            consumeLiquid(Liquids.water, 10f / 60f);

            itemCapacity = 200;
        }};

        unitCargoUnloadPoint = new UnitCargoUnloadPoint("unit-cargo-unload-point"){{
            requirements(Category.distribution, with(Items.graphite, 25, Items.silicon, 35));

            size = 2;
            envEnabled = 1;
            envDisabled = scorching + underwater;

            itemCapacity = 100;
        }};


        //Region environment

        grating = new Floor("grating"){{
            variants = 0;
            solid = false;
        }};

        plating = new Floor("plating"){{
            variants = 0;
            solid = false;
            blendGroup = grating;
        }};

        platingCross = new Floor("plating-cross"){{
            variants = 0;
            solid = false;
            blendGroup = grating;
        }};

        socket = new Floor("socket"){{
            variants = 0;
            solid = false;
        }};

        powerSocket = new PowerPlugFloor("power-socket"){{
            lightColor = ModPal.glowYellow.cpy().a(0.06f);
            secondaryLightColor = lightColor.cpy().lerp(Color.white, 0.15f).a(0.35f);
            glowColor = ModPal.glowYellow;
            effect = Fxf.powerSpark;
            chance = 0.006f;
            blendGroup = socket;
            overrideMapColor = ModPal.glowYellow;
        }};

        powerSocketLarge = new PowerPlugFloor("power-socket-large"){{
            lightColor = ModPal.glowYellow.cpy().a(0.11f);
            secondaryLightColor = lightColor.cpy().lerp(Color.white, 0.15f).a(0.35f);
            glowColor = ModPal.glowYellow;
            lightRadius = 52;
            secondaryLightRadius = 21;
            effect = Fxf.powerSpark;
            chance = 0.006f;
            blendGroup = socket;
            overrideMapColor = ModPal.glowYellow;
        }};

        liquidSocket = new GlowingFloor("liquid-socket"){{
            lightColor = ModPal.glowMagenta.cpy().a(0.06f);
            secondaryLightColor = lightColor.cpy().lerp(Color.white, 0.15f).a(0.35f);
            glowColor = ModPal.glowMagenta;
            effect = Fxf.powerSpark;
            chance = 0.006f;
            blendGroup = socket;
            overrideMapColor = ModPal.glowMagenta;
        }};

        liquidSocketLarge = new GlowingFloor("liquid-socket-large"){{
            lightColor = ModPal.glowMagenta.cpy().a(0.11f);
            secondaryLightColor = lightColor.cpy().lerp(Color.white, 0.15f).a(0.35f);
            glowColor = ModPal.glowMagenta;
            lightRadius = 52;
            secondaryLightRadius = 21;
            effect = Fxf.powerSpark;
            chance = 0.006f;
            blendGroup = socket;
            overrideMapColor = ModPal.glowMagenta;
        }};

        itemSocket = new GlowingFloor("item-socket"){{
            lightColor = ModPal.glowCyan.cpy().a(0.06f);
            secondaryLightColor = lightColor.cpy().lerp(Color.white, 0.15f).a(0.35f);
            glowColor = ModPal.glowCyan;
            effect = Fxf.powerSpark;
            chance = 0.006f;
            blendGroup = socket;
            overrideMapColor = ModPal.glowCyan;
        }};

        itemSocketLarge = new GlowingFloor("item-socket-large"){{
            lightColor = ModPal.glowCyan.cpy().a(0.11f);
            secondaryLightColor = lightColor.cpy().lerp(Color.white, 0.15f).a(0.35f);
            glowColor = ModPal.glowCyan;
            lightRadius = 52;
            secondaryLightRadius = 21;
            effect = Fxf.powerSpark;
            chance = 0.006f;
            blendGroup = socket;
            overrideMapColor = ModPal.glowCyan;
        }};

        maficFloor = new Floor("mafic-floor");

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

        sulphuricIce = new Floor("sulphuric-ice"){{
            variants = 4;
            speedMultiplier = 1.15f;
            dragMultiplier = 0.75f;
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

        enclosureWall = new StaticWall("enclosure-wall");


        maficStone = new StaticWall("mafic-stone");

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
            variants = 3;
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

        maficBoulder = new Prop("mafic-boulder"){{
            variants = 2;
        }};

        //endregion

        //region defense

        stoneWall = new CrumblingWall("stone-wall"){{
            health = 280;
            requirements(Category.defense, with(FrostItems.stone, 6, FrostItems.rust, 4));
            variants = 4;
            plating = 8;
            armor = 3;
            destroyEffect = new Effect(210, n -> {

                n.scaled(48, e -> {
                    Draw.color(Color.gray);
                    Draw.alpha(0.9F);

                    Angles.randLenVectors(e.id, 4, 35 * e.fin(), e.rotation, 180, (x,y) -> {
                        Fill.circle(e.x + x, e.y + y, 8 * e.fout());
                    });

                    Angles.randLenVectors(e.id, 3, 10 * e.fin(), e.rotation, 180, (x,y) -> {
                        Fill.rect(e.x + x, e.y + y, 8 * e.fout(), 8 * e.fout(), 45);
                    });

                    Draw.color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
                    Lines.stroke(1.5f * e.fout());
                    Angles.randLenVectors(e.id + 1, 8, 1 + 23 * e.finpow(), (x, y) -> {
                        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1.0F + e.fout() * 3.0F);
                    });
                });


                n.scaled(15, i -> {
                    Draw.color(Color.gray);
                    Lines.stroke(i.fout() * 1);
                    Lines.circle(n.x, n.y, 3 + i.fin() * 10);
                });

                Draw.z(Layer.debris);
                Draw.color(Pal.redSpark, Pal.darkPyraFlame, Pal.darkMetal, n.fin(Interp.pow4));
                Angles.randLenVectors(n.id, 5, 26 * n.fin(Interps.fadePow), n.rotation, 180, (x, y) -> {
                    Fill.rect(n.x + x, n.y + y, 4 * n.fout(), 8 * n.fout(), 45 + Angles.angle(x,y));
                });
            });
        }};

        reflectiveWall = new ReflectiveWall("reflective-wall"){{
            requirements(Category.effect, with());
        }};

        thermalLandmine = new ThermalMine("thermal-landmine"){{
            requirements(Category.effect, with(Items.graphite, 10, Items.silicon, 15, Items.pyratite, 15));
            health = 55;
            tileDamage = 0.75f;
            warmupSpeed = 0.04f;
            warmDownSpeed = 0.15f;

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
                        armorMultiplier = new float[]{
                                1
                        };
                        costs = new ItemStack[][]{
                                with(Items.graphite, 5, Items.lead, 10),
                                with(Items.metaglass, 7),
                                with(Items.titanium, 10, Items.graphite, 15)
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
                                with(Items.coal, 5),
                                with(Items.pyratite, 10),
                                with(Items.coal, 10, Items.pyratite, 25)
                        };
                    }},
                    new UpgradeEntry(FrostUpgrades.alwaysArmed){{
                        costs = new ItemStack[][]{
                                with(Items.silicon, 3)
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

        lightningMine = new LightningMine("lightning-mine"){{
            requirements(Category.defense, with(Items.silicon, 25, Items.surgeAlloy, 5));

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
                        armorMultiplier = new float[]{
                                1
                        };
                        costs = new ItemStack[][]{
                                with(Items.graphite, 5, Items.lead, 10),
                                with(Items.metaglass, 7),
                                with(Items.titanium, 10, Items.graphite, 15)
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
                                with(Items.silicon, 5),
                                with(Items.surgeAlloy, 10),
                                with(Items.graphite, 10, Items.surgeAlloy, 25)
                        };
                    }},
                    new UpgradeEntry(FrostUpgrades.alwaysArmed){{
                        costs = new ItemStack[][]{
                                with(Items.silicon, 3)
                        };
                    }}
            );
        }};
        //endregion

        //region power

        powerPlug = new PowerPlug("power-plug"){{
            requirements(Category.power, with(FrostItems.rust, 30));
            category = Category.power;
            consumePowerBuffered(200);
            maxExchanged = 5;
            drawer = new DrawMulti(new DrawBlock[]{
                    new DrawDefault(),
                    new DrawGlowRegion(){{
                        color = Pal.powerLight;
                    }},
                    new DrawGenerateLight(),
                    new DrawConstantLight()
            });
        }};

        powerPlugLarge = new PowerPlug("power-plug-large"){{
            requirements(Category.power, with(FrostItems.rust, 30));
            size = 3;
            consumePowerBuffered(2500);
            maxExchanged = 40;
            lightRadius = 52;
            drawer = new DrawMulti(new DrawBlock[]{
                    new DrawDefault(),
                    new DrawGlowRegion(){{
                        color = Pal.powerLight;
                    }},
                    new DrawGlowRegion("-top-glow"){{
                        color = Color.white;
                        glowScale = 25;
                        glowIntensity = 0.25f;
                        alpha = 0.35f;
                    }},
                    new DrawGenerateLight(){{
                        baseRadius = 84;
                    }},
                    new DrawConstantLight()
            });
        }};

        //endregion

        //region distribution
        itemPlug = new PlugBlock("item-plug"){{
            requirements(Category.distribution, with(FrostItems.rust, 30));
            validFloors.add(itemSocket, itemSocketLarge);
        }};
        //endregion

        //region fluids
        liquidPlug = new PlugBlock("liquid-plug"){{
            requirements(Category.liquid, with(FrostItems.rust, 30));
            validFloors.add(liquidSocket, liquidSocketLarge);
        }};
        //endregion

        //region storage

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

            squareSprite = false;

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

        //endregion

        //region turrets

        pyroclast = new MinRangeTurret("pyroclast"){{
            requirements(Category.turret, with());
            size = 3;
            health = 153 * size * size;
            reload = 235;
            minRange = 160;
            range = 350;
            velocityRnd = 0.05f;
            recoil = 6;
            rotateSpeed = 2.5f;
            cooldownTime = 75;
            shootY = -8;
            squareSprite = false;
            drawer = new DrawTurret("elevated-"){{
                Color heatc = Pal.turretHeat;
                heatColor = heatc;
                liquidDraw = Liquids.oil;
                liquidCapacity = 300;
                parts.addAll(
                    new RegionPart("-hinges"){{
                        progress = PartProgress.recoil;
                        under = true;
                    }},
                    new RegionPart("-barrel"){{
                        progress = new AccelPartProgress(0,3.25f,-1.2f,0,6,11,PartProgress.recoil.inv().mul(0.5f));
                        moveY = -9;
                        heatColor = Color.valueOf("f03b0e");
                        mirror = false;
                        layerOffset = -0.00001f;
                        outlineLayerOffset = -0.00002f;
                    }},
                    new RegionPart("-top"){{

                    }},
                    new EffectPart(){{
                        progress = PartProgress.heat;
                        effect = new Effect(35f, (e) -> {
                            Draw.color(Pal.gray, Color.darkGray, e.fin());
                            Draw.alpha(e.fout() * 2);
                            Angles.randLenVectors(e.id, 2, e.finpow() * 22, e.rotation, 35, (x, y) -> {
                                Fill.circle(e.x + x, e.y + y, e.fin() * 3.5f);
                            });
                        });

                        mirror = true;
                        rotation = 225;
                        x = 24/4;
                        y = -30/4;
                        effectChance = 1;
                    }}
                );
            }};
            outlineColor = Pal.darkOutline;

            shake = 1.2f;

            ammo(
                    FrostItems.thermite,
                    new BouncyBulletType(3.5f, 10, NAME + "-napalm-canister"){{
                        frontColor = backColor = Color.white;
                        lifetime = 17.5f;
                        drag = 0.016f;
                        despawnEffect = Fx.none;
                        width = 8;
                        height = 8;
                        shrinkX = 1f;
                        shrinkY = 1f;
                        status = FrostStatusEffects.napalm;
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
                        shake = 1.7f;

                        float[] layers = new float[]{visualHeightMax, visualHeightMin};

                        hitEffect = new Effect(850, e -> {
                            float h = Math.max(((Math3D.HeightHolder) e.data).height, 0);
                            e.scaled(25, e1 -> {
                                DrawUtils.parallaxOffset(e.x, e.y, h, Tmp.v1);
                                float ox = Tmp.v1.x, oy = Tmp.v1.y;
                                Draw.z(visualHeightMax);
                                Lines.stroke(e1.fout() * 0.65f);
                                color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
                                Lines.circle(ox, oy, e1.finpow() * 35);

                                Lines.stroke(e1.fout() * 3);
                                Angles.randLenVectors(e.id + 1, (int) (Mathf.randomSeed(e.id, 3) + 5), e1.fin() * 54 + 6, e.rotation, 54, (x, y) -> {
                                    DrawUtils.parallaxOffset(e.x + x * 0.3f, e.y + y * 0.3f, h, Tmp.v1);
                                    DrawUtils.parallaxOffset(e.x + x, e.y + y, h, Tmp.v2);

                                    color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
                                    Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
                                    Draw.color(Color.black);
                                });
                            });

                            float smokeH = Math.max((((Math3D.HeightHolder) e.data).height), 0) + DrawUtils.smokeDrift * e.lifetime * e.fin();
                            float visibility = smokeH/visualHeightRange;
                            float radius = 5 * e.fout(Interp.pow4);

                            color(Color.gray);
                            Draw.alpha(e.fout());
                            Draw.z(visualHeightMax);
                            Angles.randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 3) + 5), e.fin() * 54 + 6, (x, y) -> {
                                DrawUtils.speckOffset(e.x + x,e.y + y, smokeH, e.fin() * e.lifetime, DrawUtils.smokeWeight, Tmp.v1);
                                float ox = Tmp.v1.x, oy = Tmp.v1.y;
                                Draw.alpha(visibility * e.fout() * 0.23f);
                                Draw.z(layers[0]);
                                Fill.circle(ox, oy, radius);
                                Draw.alpha((1 - visibility) * e.fout() * 0.23f);
                                Draw.z(layers[1]);
                                Fill.circle(ox, oy, radius);
                            });
                        });

                        trailEffect = new Effect(125, e -> {
                            float h = (float) e.data;
                            float radius = 8.8f * h;
                            Draw.color(Liquids.oil.color);
                            Angles.randLenVectors(e.id + 1, (int) (Mathf.randomSeed(e.id, 3) + 1), e.fin() * 12, e.rotation, 35, (x, y) -> {
                                DrawUtils.speckOffset(e.x + x, e.y + y, h, e.fin() * e.lifetime, DrawUtils.smokeWeight, Tmp.v1);
                                float ox = Tmp.v1.x, oy = Tmp.v1.y;
                                float visibility = h/visualHeightRange;
                                Draw.alpha(visibility * e.fout() * 0.73f);
                                Draw.z(layers[0]);
                                Fill.circle(ox, oy, radius);
                                Draw.alpha((1-visibility) * e.fout() * 0.73f);
                                Draw.z(layers[1]);
                                Fill.circle(ox, oy, radius);
                            });
                        });
                        trailChance = 0.35f;
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
                            trailEffect = Fxf.emberTrailHeight;
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

        cryonis = new ItemTurret("cryonis"){{
            requirements(Category.turret, with(FrostItems.stone, 50, FrostItems.rust, 80, FrostItems.aluminium, 120));
            size = 3;
            health = 153 * size * size;
            reload = 420;
            range = 315;
            shootY = -3.5f;
            recoil = 2.5f;
            minWarmup = 0.8f;
            shootWarmupSpeed = 0.045f;
            warmupMaintainTime = 30;
            rotateSpeed = 3.5f;
            ammoPerShot = 55;
            maxAmmo = 135;
            cooldownTime = 75;
            coolant = consume(new ConsumeLiquid(Liquids.nitrogen, 0.015f));
            coolantMultiplier = 1.8f;
            outlineColor = Pal.darkOutline;
            shootSound = Sounds.cannon;
            soundPitchMax = 3;
            soundPitchMin = 2.6f;
            shoot.firstShotDelay = 45;
            moveWhileCharging = true;
            ammo(
                    FrostItems.ice,
                    new MissileBulletType(4.6f, 1){{
                        ammoMultiplier = 1;
                        spawnUnit = new MissileUnitType("cryonis-shard") {
                            {
                                shootEffect = new MultiEffect(Fx.shootSmall, new Effect(45, e -> {
                                    e.scaled(8, e1 -> {
                                        Lines.stroke(e1.fout() * 1.7f);
                                        Lines.circle(e.x, e.y, e1.fin() * 25 + 4);
                                    });
                                    Draw.color(Color.white, ModPal.ice, e.fout());
                                    Angles.randLenVectors(e.id, 8, 85 * e.finpow(), e.rotation, 35, (x, y) -> {
                                        Fill.square(e.x + x, e.y + y, e.fout() * 3, 45);
                                    });
                                }));
                                speed = 4.2F;
                                drag = 0.05f;
                                maxRange = 6;
                                lifetime = 57;
                                outlineColor = ModPal.darkBlue;
                                engineSize = 2;
                                engineOffset = 0;
                                rotateSpeed = 0;
                                missileAccelTime = 0;
                                trailColor = ModPal.ice;
                                trailLength = 3;
                                trailWidth = 15;
                                lowAltitude = true;
                                drawCell = false;
                                loopSound = Sounds.none;
                                deathSound = Sounds.none;
                                health = 120;
                                armor = 10;
                                deathExplosionEffect  = new MultiEffect(Fx.pulverize, new Effect(45, e -> {
                                    e.scaled(15, e1 -> {
                                        Lines.stroke(3 * e1.fout());
                                        Lines.circle(e.x, e.y, e1.fin() * 25 + 4);
                                    });
                                    Draw.color(Color.white, ModPal.ice, e.fout());
                                    Angles.randLenVectors(e.id, 8, 35 * e.finpow(), e.rotation, 180, (x, y) -> {
                                        Fill.square(e.x + x, e.y + y, e.fout() * 3, 45);
                                    });
                                }));
                                lightRadius = 0;


                                weapons.add(new Weapon() {{
                                        soundPitchMax = 1.8f;
                                        soundPitchMin = 2.3f;
                                        shootSound = Sounds.plasmaboom;
                                        shootCone = 360;
                                        mirror = false;
                                        reload = 1;
                                        rotate = false;
                                        shootOnDeath = true;
                                        x = y = shootX = shootY = 0;
                                        bullet = new ExplosionBulletType(470, 12){{
                                            fragLifeMin = 0.7f;
                                            fragLifeMax = 1f;
                                            hitEffect = Fx.none;
                                            shootEffect = Fx.none;
                                            status = StatusEffects.freezing;
                                            statusDuration = 165;
                                            fragBullets = 7;
                                            fragRandomSpread = 10;
                                            fragSpread = 15;
                                            knockback = 12;
                                            fragBullet = new MissileBulletType(){{
                                                spawnUnit = new MissileUnitType("cryonis-shard-frag"){{
                                                    speed = 8.6F;
                                                    drag = 0.05f;
                                                    maxRange = 6;
                                                    lifetime = 9;
                                                    outlineColor = ModPal.darkBlue;
                                                    engineSize = 0.7f;
                                                    engineOffset = 0;
                                                    rotateSpeed = 0;
                                                    missileAccelTime = 0;
                                                    trailColor = ModPal.ice;
                                                    trailLength = 3;
                                                    trailWidth = 15;
                                                    lowAltitude = true;
                                                    drawCell = false;
                                                    loopSound = Sounds.none;
                                                    deathSound = Sounds.none;
                                                    health = 15;
                                                    armor = 3;
                                                    deathExplosionEffect  = new MultiEffect(Fx.pulverize, new Effect(28, e -> {
                                                        e.scaled(8, e1 -> {
                                                            Lines.stroke(2 * e1.fout());
                                                            Lines.circle(e.x, e.y, e1.fin() * 17 + 2);
                                                        });
                                                        Draw.color(Color.white, ModPal.ice, e.fout());
                                                        Angles.randLenVectors(e.id, 3, 23 * e.finpow(), e.rotation, 180, (x, y) -> {
                                                            Fill.square(e.x + x, e.y + y, e.fout() * 3, 45);
                                                        });
                                                    }));
                                                    lightRadius = 0;
                                                    weapons.add(new Weapon() {{
                                                        soundPitchMax = 1.8f;
                                                        soundPitchMin = 2.3f;
                                                        shootSound = Sounds.splash;
                                                        shootCone = 360;
                                                        mirror = false;
                                                        reload = 1;
                                                        shootOnDeath = true;
                                                        x = y = shootX = shootY = 0;
                                                        bullet = new ExplosionBulletType(75, 5){{
                                                            fragLifeMin = 0.7f;
                                                            fragLifeMax = 1f;
                                                            hitEffect = Fx.none;
                                                            shootEffect = Fx.none;
                                                            status = StatusEffects.freezing;
                                                            statusDuration = 45;
                                                            puddleLiquid = Liquids.water;
                                                            puddles = 2;
                                                            puddleAmount = 15;
                                                            knockback = 4;
                                                        }};
                                                    }});
                                                }};
                                            }};
                                        }};
                                    }
                                });
                            }
                        };
                    }}
            );
            Seq<DrawPart.PartMove> shardMoves = Seq.with(
                    new DrawPart.PartMove(DrawPart.PartProgress.warmup, 0, -3, 0),
                    new DrawPart.PartMove(new AccelPartProgress(1, 0.9f, -0.095f, 0, 6, 20, DrawPart.PartProgress.charge.inv().clamp()), 0, -5, 0)
            );
            drawer = new DrawTurret("elevated-") {{
                parts.addAll(
                    new RegionPart("-shard") {{
                        progress = PartProgress.smoothReload.curve(Interp.pow2In).add(p -> Mathf.zero(PartProgress.charge.get(p)) ? 0 : -1).clamp();
                        colorTo = new Color(1, 1, 1, 0);
                        color = Color.white;
                        mixColorTo = Pal.accent;
                        mixColor = new Color(1, 1, 1, 0);
                        layerOffset = -0.01f;
                        y = 3;
                        under = true;
                        outline = false;
                        moves.addAll(shardMoves);
                    }}, new RegionPart("-shard-cover"){{
                            progress = PartProgress.warmup.mul(2).add(PartProgress.reload.curve(Interp.pow2In)).clamp();
                            color = Color.white;
                            colorTo = new Color(1,1,1,0);
                            layerOffset = -0.01f;
                            y = 3;
                            under = true;
                            outline = false;
                            moves.addAll(shardMoves);
                    }},
                    new EffectPart(){{
                        progress = PartProgress.smoothReload.curve(Interp.pow2In).add(p -> Mathf.zero(PartProgress.charge.get(p)) ? 0 : -1).clamp().mul(p -> Mathf.zero(PartProgress.reload.inv().add(-0.1f).clamp().get(p)) ? 0 : 1);
                        moves.addAll(shardMoves);
                        effectChance = 1;
                        y = 3;
                        effect = new Effect(35, (e) -> {
                            Draw.color(ModPal.ice);
                            Draw.alpha(e.fin());
                            Angles.randLenVectors(e.id, 5, 3 + e.foutpow() * 2, e.rotation, 75, (x, y) -> {
                                Fill.circle(e.x + x, e.y + y, e.fout() * 2);
                            });
                        });
                        rotation = 0;
                    }},
                    new RegionPart("-barrel") {{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup;
                            heatColor = Color.red;
                            moveRot = -14;
                            moveX = 1;
                            moveY = -1.2f;
                            mirror = true;
                            moves.add(new PartMove(PartProgress.reload.inv().add(PartProgress.charge.inv().mul(PartProgress.warmup)).compress(0, 1).add(p -> Mathf.zero(PartProgress.charge.get(p)) ? 0 : 1), 0, 0, -11));
                        }},
                    new RegionPart("-blade"){{
                        mirror = true;
                        progress = PartProgress.warmup;
                        moveRot = -8;
                        moveY = -0.75f;
                        moveX = 1;
                        heatProgress = PartProgress.warmup;
                        heatColor = ModPal.ice;
                    }},
                    new RegionPart("-mid"){{
                        moveY = -1.5f;
                        progress = PartProgress.warmup;
                        moves.add(new PartMove(PartProgress.recoil, 0, -1.5f, 0));
                        heatProgress = PartProgress.heat.add(-0.4f).add((p) -> {
                            return Mathf.sin(8.0F, 0.6f) * p.smoothReload * p.warmup;
                        });
                    }});
                };
            };

        }};

        rivulet = new LiquidTurret("rivulet"){{
            requirements(Category.turret, with(FrostItems.stone, 20, FrostItems.rust, 35, FrostItems.ferricPanels, 60, FrostItems.aluminium, 25));
            size = 2;

            shootWarmupSpeed = 0.1f;
            minWarmup = 0.35f;
            shoot.shotDelay = 7.5f;
            shoot.shots = 3;
            reload = 12.5f;
            shootY = 3;
            shootEffect = Fx.shootLiquid;
            recoil = 0;

            ammo(Liquids.water, new RicochetBulletType(5, 5, "circle"){{
                chargeEffect = new Effect(40, e -> {
                    color(Liquids.water.gasColor);
                    alpha(Mathf.clamp(e.foutpow() * 2f));

                    randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 6) + 12), e.finpow() * 45, e.rotation, 40, (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, e.finpow() * 1.5f);
                    });
                });

                frontColor = hitColor = Liquids.water.color;

                shrinkX = shrinkY = 0;
                width = height = 4;
                trailLength = 8;
                trailWidth = 2;
                trailColor = Liquids.water.color;
                keepVelocity = false;
                lifetime = 15;
                knockback = 4;
                pierce = true;
                pierceBuilding = true;
                pierceArmor = true;
                status = StatusEffects.wet;
                statusDuration = 180;
                hitEffect = Fx.none;
                despawnEffect = Fx.none;
                fragOnHit = true;
                trailRotation = true;
                bounceEffect = trailEffect = new Effect(45, e -> {
                    color(Liquids.water.color);
                    alpha(Mathf.clamp(e.fslope() * e.fslope() * 2f));
                    Color color = Draw.getColor();

                    randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 3) + 6), e.finpow() * 45, e.rotation, 5 + 25 * e.fin(), (x, y) -> {
                        Building b = Vars.world.buildWorld(e.x + x, e.y + y);
                        if (b != null)
                            alpha(color.a * (b.dst2(e.x + x, e.y + y) / b.hitSize() / b.hitSize() / 2 + 0.5f));
                        Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f);
                        Draw.color(color);
                    });
                });
                trailChance = 1;
                fragSpread = 3;
                fragBullets = 3;
                fragRandomSpread = 15;
                fragBullet = intervalBullet = new LiquidBulletType(Liquids.water) {{
                    speed = 3;
                    lifetime = 25;
                    orbSize = 2;
                    drag = 0.05f;
                }};
                intervalBullets = 1;
                intervalDelay = 1;
                intervalRandomSpread = 15;
            }});

            outlineColor = ModPal.quiteDarkOutline;

            drawer = new DrawTurret("elevated-"){{
                parts.addAll(
                    new RegionPart("-turbine-blade"){{
                        layerOffset = 0.03f;
                        outline = false;
                        moves.add(new PartMove(p -> Mathf.mod(Time.time/25, 1), 0, 0, 360));
                        x = -8/4;
                        y = -17/4;
                    }}
                );
            }};
        }};

        /*
        ((DrawTurret) ((ItemTurret) Blocks.smite).drawer).parts.each(pe -> {
            if(pe instanceof RegionPart part) part.moves.add(new DrawPart.PartMove(p -> Mathf.mod(Time.time / 45, 1), 0, 0, 360));
        });

         */

        //endregion

        //region light
        solarReflector = new SolarReflector("solar-reflector"){{
            requirements(Category.effect, with());
            data = new LightBeams.ColorData(2.4f, 2f, 1.6f);
            drawer = new DrawMulti(
                    new DrawUpgradePart(
                            baseRock,
                            new String[]{
                                    baseReflector
                            },
                            FrostUpgrades.improvedBase
                    ),
                    new ReflectorDrawer(name, Layer.blockOver,  Layers.light + 0.1f)
            );

            entries.add(
                new UpgradeEntry(FrostUpgrades.improvedBase){{
                    healthMultiplier = new float[]{
                            3.5f
                    };
                    costs = new ItemStack[][]{
                            with(FrostItems.ferricPanels, 5)
                    };
                }}
            );
        }};

        //region crafting

        coreSiphon = new CoreSiphon("core-siphon"){{
            requirements(Category.production, with());
            liquidPadding = 6;
            size = 7;
            liquidCapacity = 1000;
            boost = consumeLiquid(Liquids.water, 0.05F);
            heatColor = new Color(ModPal.heat).a(0.35f);
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

        //endregion

    }
}