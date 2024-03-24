package main.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Tmp;
import ent.anno.Annotations;
import main.ai.types.ArtilleryAI;
import main.entities.BaseBulletType;
import main.entities.ability.MoveArmorAbility;
import main.entities.ability.MoveDamageLineAbility;
import main.entities.ability.RamDamageAbility;
import main.entities.bullet.ChainLightningBulletType;
import main.entities.bullet.FrostBulletType;
import main.entities.bullet.RicochetBulletType;
import main.entities.part.LightPart;
import main.gen.EntityRegistry;
import main.gen.PortaLaserUnit;
import main.gen.PortaLaserc;
import main.graphics.ModPal;
import main.type.HollusTankUnitType;
import main.type.HollusUnitType;
import main.type.weapons.PointDefenseMissileWeapon;
import main.type.weapons.ThrustSwingWeapon;
import main.type.weapons.VelocityWeapon;
import mindustry.Vars;
import mindustry.ai.types.CargoAI;
import mindustry.ai.types.CommandAI;
import mindustry.ai.types.GroundAI;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.abilities.ShieldArcAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.type.weapons.PointDefenseWeapon;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.BlockFlag;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static main.Frostscape.NAME;

public class FrostUnits {

    public static UnitType serpieDrone,
    sunspot, javelin, stalagmite, cord, ghoul, manta, andon;

    public static @Annotations.EntityDef({Unitc.class, PortaLaserc.class}) UnitType upgradeDrone;


    public static void load(){
        EntityRegistry.register();

        RicochetBulletType bouncy = new RicochetBulletType(6, 8, "bullet"){{
            drag = 0.015f;
            bounciness = 1f;
            width = 4;
            height = 8;
            shrinkX = 0;
            shrinkY = 0;
            lifetime = 35;
            knockback = 0.75f;
            hitEffect = bounceEffect = despawnEffect = Fx.hitBulletColor;
            trailColor = Pal.suppress;
            trailWidth = 1f;
            trailLength = 4;
            bounceSame = true;
            bounceCap = 5;
            pierceCap = 2;
            status = FrostStatusEffects.conflexInternal;
            statusDuration = 15;
            frontColor = Color.white;
            backColor = Pal.suppress;
        }};

        upgradeDrone = EntityRegistry.content("upgrade-drone", PortaLaserUnit.class, name -> new UnitType(name){{

        }});

        serpieDrone = new UnitType("serpie-drone") {
            {
                controller = (u) -> new CargoAI();
                isEnemy = false;
                allowedInPayloads = false;
                logicControllable = false;
                playerControllable = false;
                envDisabled = 0;
                payloadCapacity = 0.0F;
                lowAltitude = false;
                flying = true;
                outlineColor = ModPal.quiteDarkOutline;
                drag = 0.06F;
                speed = 3.5F;
                rotateSpeed = 9.0F;
                accel = 0.1F;
                itemCapacity = 100;
                health = 200.0F;
                hitSize = 11.0F;
                engineSize = 2.3F;
                engineOffset = 6.5F;
                hidden = true;
                setEnginesMirror(new UnitType.UnitEngine[]{new UnitType.UnitEngine(6.0F, -6.0F, 2.3F, 315.0F)});
                constructor = BuildingTetherPayloadUnit::create;
            }
        };

        /*
        hearth = new HollusUnitType("hearth"){{

        }};

        hearthDefend = new HollusUnitType("hearth-defend"){{
            aiController = DroneAI::new;
        }};
        */

        cord = new HollusUnitType("cord"){{
            lightOpacity = 0.15f;
            lightRadius = 45;
            health = 560;
            armor = 8;
            speed = 0.85f;
            constructor = MechUnit::create;
            hitSize = 11;
            families.add(Families.specialist);
            deathSound = Sounds.dullExplosion;
            deathExplosionEffect = new Effect(40, e -> {
                color(Pal.sapBullet);

                e.scaled(6, i -> {
                    stroke(3f * i.fout());
                    Lines.circle(e.x, e.y, 3f + i.fin() * 15);
                });

                e.scaled(15, e1 -> {
                    color(Color.gray);

                    randLenVectors(e1.id, 5, 2f + 25 * e1.finpow(), (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, e1.fout() * 4f + 0.5f);
                    });

                    color(Pal.sapBulletBack);
                    stroke(e.fout());

                    randLenVectors(e1.id + 1, 3, 1f + 15 * e1.finpow(), (x, y) -> {
                        lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e1.fout() * 3f);
                    });

                    Drawf.light(e.x, e.y, 65, Pal.sapBulletBack, 0.8f * e1.fout());
                });

                color(Pal.darkerMetal);

                randLenVectors(e.id, 5, 3f + e.fin() * 8f, (x, y) -> {
                    Fill.square(e.x + x, e.y + y, e.fout() * 2f + 0.5f, 45 + e.fin() * 15);
                });
            });

            weapons.add(
                new Weapon(){{
                    shootOnDeath = true;
                    shootCone = 180;
                    reload = 60;
                    ejectEffect = Fx.none;
                    shootSound = Sounds.none;
                    x = y = shootY = 0.0f;
                    mirror = false;
                    noAttack = true;
                    controllable = false;
                    rotate = false;

                    shoot = new ShootSpread(){{
                           shots = 4;
                    }};
                    inaccuracy = 30;
                    bullet = new BombBulletType(){{
                        //Separate shoot effect to help angle the sparks
                        shootEffect = new Effect(15, e -> {
                            color(Color.white, Pal.lightOrange, e.fin());
                            stroke(0.5f + e.fout());

                            randLenVectors(e.id, 5, e.fin() * 15f, e.rotation, 15, (x, y) -> {
                                float ang = Mathf.angle(x, y);
                                lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f);
                            });
                        });
                        instantDisappear = true;
                        speed = 5;
                        fragBullets = 3;
                        fragRandomSpread = 30;
                        fragBullet = bouncy;
                        despawnEffect = hitEffect = Fx.none;
                    }};
                }},
                new Weapon(NAME + "-cord-weapon"){{
                    x = 6.325f;
                    y = 1.125f;
                    minWarmup = 0.95f;
                    shootWarmupSpeed = 0.05f;
                    shootSound = Sounds.artillery;
                    reload = 65;
                    alternate = true;
                    top = false;
                    cooldownTime = 75;
                    shake = 1.75f;
                    DrawPart.PartMove mover = new DrawPart.PartMove(DrawPart.PartProgress.recoil, 0.2f, -0.12f, 20);

                    parts.addAll(
                        new RegionPart("-bottom"){{
                            moves.add(mover);
                            under = true;
                            outline = false;
                            heatLayerOffset = 0;
                        }},
                        new RegionPart("-cover"){{
                            moves.add(mover);
                            moves.add(new PartMove(PartProgress.warmup, 1.15f, 1f, -40));
                            under = true;
                            outline = false;
                        }},
                        new RegionPart("-body"){{
                            moves.add(mover);
                            under = true;
                        }},
                        new RegionPart("-glow"){{
                            moves.add(mover);
                            outline = false;
                            progress = PartProgress.warmup.add(-0.5f).mul(2).clamp();
                            color = Color.clear;
                            colorTo = ModPal.specialist;
                            blending = Blending.additive;
                        }},
                        new LightPart(){{
                            x = 1;
                            y = 1;
                            progress = growProgress = PartProgress.warmup.mul(1.25f).clamp().curve(Interp.smoother);

                            moves.add(mover);
                            radius = 15;
                            length = 25;
                            stroke = 10;
                            ocapacity = 0.4f;
                        }}
                    );


                    bullet = new BasicBulletType(5.75f, 45){
                        @Override
                        public void init(){
                            super.init();
                            range = 16 * 8;
                        }

                        {
                        collidesGround = true;
                        drag = 0.06f;
                        shootEffect = Fx.explosion;
                        hitSound = despawnSound = Sounds.dullExplosion;
                        hitEffect = despawnEffect = new Effect(25, e -> {
                            color(Pal.sapBullet);

                            e.scaled(6, i -> {
                                stroke(3f * i.fout());
                                Lines.circle(e.x, e.y, 3f + i.fin() * 15);
                            });

                            color(Color.gray);

                            randLenVectors(e.id, 5, 2f + 25 * e.finpow(), (x, y) -> {
                                Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f);
                            });

                            color(Pal.sapBulletBack);
                            stroke(e.fout());

                            randLenVectors(e.id + 1, 3, 1f + 15 * e.finpow(), (x, y) -> {
                                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f);
                            });

                            Drawf.light(e.x, e.y, 45, Pal.sapBulletBack, 0.8f * e.fout());
                        });
                        width = 14;
                        height = 12;
                        lifetime = 15;
                        fragBullet = bouncy;
                        fragLifeMin = 0.5f;
                        fragLifeMax = 1;
                        fragVelocityMax = 1;
                        fragVelocityMin = 0.5f;
                        fragBullets = 5;
                        fragRandomSpread = 15;
                        fragSpread = 2.5f;
                        status = FrostStatusEffects.conflexInternal;
                        statusDuration = 75;
                        frontColor = Color.white;
                        backColor = Pal.suppress;
                    }};
                }}
            );
        }};

        ghoul = new HollusTankUnitType("ghoul"){{
            families.add(Families.swarm);
            health = 1220;
            armor = 11;
            rotateSpeed = 1.6f;
            speed = 0.9f;
            constructor = TankUnit::create;
            hitSize = 16;

            treadPullOffset = 2;
            treadRects = new Rect[]{new Rect(11 - 43, 14 - 43, 14, 65)};

            deathExplosionEffect = new Effect(26, e -> {

                Draw.color(ModPal.swarm, Color.white, e.fin());

                e.scaled(12, e1 -> {
                    Lines.stroke(e1.fout() * 4.2f);
                    Lines.circle(e.x, e.y, e.finpow() * 44);
                });


                stroke(1.5f * e.fout());

                randLenVectors(e.id, 9, e.fin() * 38, e.rotation, 360, (x, y) -> {
                    float ang = Mathf.angle(x, y);
                    lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f);
                });

                randLenVectors(e.id + 1, 5, e.fin() * 20 + 4, e.rotation, 360, (x, y) -> {
                    Fill.square(e.x + x, e.y + y, e.fout() * 3, 45 + e.fin() * (x + y));
                });

                Drawf.light(e.x, e.y, 65, ModPal.swarm, 0.8f * e.fout());
            });
            weapons.addAll(new Weapon(NAME + "-ghoul-shotgun"){{
                x = y = 0;
                mirror = false;
                reload = 50;
                shootCone = 15;
                rotate = true;
                rotateSpeed = 8;
                shootSound = Sounds.shootAltLong;
                shoot = new ShootSpread(){{
                    shots = 7;
                    spread = 5;
                }};
                inaccuracy = 2;
                velocityRnd = 0.37f;
                bullet = new BasicBulletType(9.4f, 35, "bullet"){{
                    width = height = 12;
                    frontColor = Color.gray;
                    hitColor = backColor = trailColor = Color.valueOf("ea8878");
                    frontColor = Pal.redLight;
                    trailWidth = 3;
                    trailLength = 3;
                    hitEffect = despawnEffect = Fx.hitSquaresColor;
                    drag = 0.12f;
                    lifetime = 13;
                    recoil = 0.05f;
                    knockback = 1.15f;
                    splashDamage = 15;
                    splashDamageRadius = 8;
                }};
            }},
            new Weapon(){{
                shootOnDeath = true;
                shootCone = 180;
                reload = 60;
                ejectEffect = Fx.none;
                shootSound = Sounds.none;
                x = y = shootY = 0.0f;
                mirror = false;
                noAttack = true;
                controllable = false;
                rotate = false;

                inaccuracy = 30;

                bullet = new BulletType() {
                    int radius = 50;
                    @Override
                    public void draw(Bullet b) {
                        Draw.z(Layer.effect);
                        Draw.alpha(b.fin());
                        Draw.color(Color.white);
                        Fill.circle(b.x, b.y, 4 * b.fout());
                        Draw.color(ModPal.swarm, Color.white, b.fin() * 0.5f);
                        Fill.circle(b.x, b.y, 8 * b.foutpow());

                        stroke((0.7f + Mathf.absin(10, 0.7f)) * b.fin() * 1.6f, ModPal.swarm);

                        float progress = b.fin(Interp.smooth);

                        for (int i = 0; i < 6; i++) {
                            float rot = i * 360f / 6 - 360 * progress;
                            Lines.arc(b.x, b.y, radius * progress + 3f, 0.08f + b.fin() * 0.06f, rot);
                        }

                        float fastProgress = Mathf.clamp(progress * 3 - 2);

                        Draw.blend(Blending.additive);
                        Fill.light(b.x, b.y, 20, radius, Tmp.c1.set(Color.white).lerp(ModPal.swarm, fastProgress).a(fastProgress * 0.2f), Tmp.c2.set(ModPal.swarm).a(0));
                        Draw.blend();
                    }
                    {
                    lifetime = 75;
                    speed = 0;
                    splashDamage = 450;
                    splashDamageRadius = 30;
                    shootEffect = despawnEffect = Fx.none;
                    hitEffect = new Effect(50f, 100f, e -> {
                        e.scaled(7f, b -> {
                            color(ModPal.swarm, b.fout());
                            Draw.blend(Blending.additive);
                            Fill.light(b.x, b.y, 20, radius, Tmp.c1.set(ModPal.swarm).a(b.fout()), Tmp.c2.set(Tmp.c1).a(b.fin() * b.fslope()));
                            Draw.blend();
                        });

                        color(ModPal.swarm);
                        stroke(e.fout() * 3f);
                        Lines.circle(e.x, e.y, radius);

                        Fill.circle(e.x, e.y, 12f * e.fout());
                        color();
                        Fill.circle(e.x, e.y, 6f * e.fout());
                        Drawf.light(e.x, e.y, radius * 1.6f, ModPal.swarm, e.fout());
                    });
                    hitSound = Sounds.spark;
                    fragBullet = new ChainLightningBulletType(){{
                        range = 50;
                        collidesTeam = true;
                        lightningColor = ModPal.swarm;
                        chainLightning = 2;
                        damage = 150;
                        distanceDamageFalloff = 2.3f;
                        jumpDamageFactor = 0.85f;
                    }};
                    fragBullets = 3;
                }};
            }});

            //treadRects = new Rect[]{new Rect(12 - 32f, 7 - 32f, 14, 51)};
        }};

        manta = new HollusTankUnitType("manta"){{
            lightOpacity = 0.15f;
            lightRadius = 65;
            constructor = TankUnit::create;
            rotateMoveFirst = false;
            health = 650;
            armor = 4;
            drag = 0.085f;
            rotateSpeed = 5.5f;
            accel = 0.075f;
            speed = 2.7f;
            treadFrames = 14;
            hitSize = 15;
            treadRects = new Rect[]{
                    new Rect(6 - 36, 25 - 45, 11, 16),
                    new Rect(4-36, 54-45, 12, 28)
            };
            weapons.add(new Weapon(NAME + "-manta-weapon"){{
                bullet = new BaseBulletType(9, 7, "bullet"){{
                    lifetime = 20;
                    width = 5;
                    height = 9;
                    pierce = pierceBuilding = true;
                    pierceCap = 2;
                    hitEffect = Fx.hitBulletSmall;
                    shrinkX = 0;
                    homingPower = 0.015f;
                    homingDelay = 3;
                    trailLength = 4;
                    trailWidth = 1f;
                }};
                top = true;
                mirror = false;
                rotate = true;
                rotateSpeed = 7.85f;
                x = 0;
                y = -13/4;
                recoil = 0;
                recoils = 2;
                reload = 7;
                smoothReloadSpeed = 0.25f;
                inaccuracy = 6;
                velocityRnd = 0.3f;
                shootWarmupSpeed = 0.025f;
                linearWarmup = true;
                minWarmup = 0.85f;
                shoot = new ShootAlternate(){{

                }};
                parts.addAll(
                    new RegionPart("-base"){{
                        progress = PartProgress.warmup.compress(0, minWarmup/2).clamp().curve(Interp.smoother);
                        mirror = true;
                        moveX = 2;
                        moveY = -1.5f;
                    }}
                );
                for (int i = 0; i < 2; i++) {
                    int j = i;
                    int sign = i == 0 ? -1 : 1;
                    parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : "r")){{
                        progress = PartProgress.recoil;
                        moveY = -0.5f;
                        recoilIndex = j;
                        moves.add(new PartMove(PartProgress.warmup.compress(0, minWarmup).clamp().curve(Interp.smoother), 1 * sign, -0.5f, 0));
                        heatProgress = PartProgress.warmup;
                        children.add(new LightPart(){{
                            progress = growProgress = PartProgress.warmup.mul(1.25f).clamp().curve(Interp.smoother);
                            length = 85;
                            radius = 35;
                            stroke = 25;
                            ocapacity = 0.35f;
                        }});
                    }});
                    parts.add(new RegionPart("-side-" + (i == 0 ? "l" : "r")){{
                        progress = PartProgress.recoil;
                        moveY = -0.25f;
                        recoilIndex = j;
                        moves.add(new PartMove(PartProgress.warmup.compress(0, minWarmup).clamp().curve(Interp.smoother), 1 * sign, -0.5f, 0));
                    }});
                }

                parts.add(new RegionPart("-connector"){{
                    progress = PartProgress.warmup.compress(minWarmup/2, minWarmup).clamp().curve(Interp.smoother);
                    color = Color.clear;
                    colorTo = Color.white;
                    mixColor = Pal.accent;
                    mixColorTo = Color.clear;
                }});

                parts.each(p -> {
                    RegionPart reg = (RegionPart) p;
                    reg.layerOffset = 0.01f;
                });
            }});

            abilities.add(new RamDamageAbility(60, 8, 0, 33/4, 0.75f, 4.1f, 0, 0.75f, 30, true, false, Fx.none, Fx.hitBulletSmall));
            parts.add(new LightPart(){{
                x = 1.5f;
                y = 4;
                mirror = true;
                radius = 1;
                length = 55;
                stroke = 35;
                rotation = -10;
                ocapacity = 0.35f;
                progress = growProgress = PartProgress.constant(1);
                moves.add(new PartMove(p -> Mathf.sin(60, 0.5f) + 0.5f, 0, 0, 20));
            }});
        }};

        andon = new HollusUnitType("andon"){{
            families.add(Families.gelid);
            constructor = LegsUnit::create;
            targetAir = false;

            hitSize = 32;
            health = 3400;
            armor = 9;
            speed = 0.65f;
            rotateSpeed = 3.5f;

            lockLegBase = true;
            legContinuousMove = true;
            legGroupSize = 3;
            legStraightness = 0.4f;
            baseLegStraightness = 0.5f;
            legMaxLength = 1.45f;

            legCount = 6;
            legLength = 18f;
            legForwardScl = 0.45f;
            legMoveSpace = 2.1f;
            legLengthScl = 0.85f;
            rippleScale = 2f;
            stepShake = 0.5f;
            legExtension = -5f;
            legBaseOffset = 8f;
            drownTimeMultiplier = 2.5f;

            hovering = true;

            targetFlags = new BlockFlag[]{BlockFlag.hasFogRadius};

            weapons.addAll(
                new Weapon(name + "-blaster"){{
                    parts.add(
                            new RegionPart("-barrel"){{
                                progress = PartProgress.recoil.curve(Interp.pow2In);
                                moveY = -4;
                                under = true;
                                layerOffset = -0.01f;
                                moves.add(new PartMove(PartProgress.warmup.curve(Interp.pow2InInverse), 0, 10, 0));
                            }},
                            new RegionPart("-mandible"){{
                                progress = PartProgress.warmup.compress(0, 0.5f).curve(Interp.pow2InInverse);
                                x = 31/4;
                                y = 53/4;
                                mirror = true;
                                under = true;
                                layerOffset = -0.01f;
                                moveRot = -35;
                                moveY = -4;
                                moveX = -1;
                            }});
                    x = y = 0;
                    mirror = false;
                    minWarmup = 0.8f;
                    shootWarmupSpeed = 0.025f;
                    recoil = 0;
                    shootY = 17;
                    shootStatus = StatusEffects.slow;
                    shootStatusDuration = 130;

                    reload = 115;
                    shootSound = Sounds.artillery;
                    shake = 4;
                    //Modified titan bullet, will change in the future
                    bullet = new BaseBulletType(2.5f, 350, "shell"){{
                        hitEffect = new MultiEffect(Fx.titanExplosion, Fx.titanSmoke);
                        collidesAir = false;
                        despawnEffect = Fx.none;
                        knockback = 2f;
                        lifetime = 140f;
                        height = 19f;
                        width = 17f;
                        splashDamageRadius = 65f;
                        splashDamage = 350f;
                        scaledSplashDamage = true;
                        backColor = hitColor = trailColor = Color.valueOf("ea8878").lerp(Pal.redLight, 0.5f);
                        frontColor = Color.white;
                        ammoMultiplier = 1f;
                        hitSound = Sounds.titanExplosion;

                        status = StatusEffects.blasted;

                        trailLength = 32;
                        trailWidth = 3.35f;
                        trailSinScl = 2.5f;
                        trailSinMag = 0.5f;
                        trailEffect = Fx.none;
                        despawnShake = 7f;

                        shootEffect = Fx.shootTitan;
                        smokeEffect = Fx.shootSmokeTitan;

                        trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                        shrinkX = 0.2f;
                        shrinkY = 0.1f;
                        buildingDamageMultiplier = 0.3f;
                    }};
                }},
                new PointDefenseMissileWeapon(name + "-pod-launcher") {{
                    parts.addAll(
                            new RegionPart("-blade"){{
                                progress = PartProgress.warmup.curve(Interp.pow2In);
                                moveY = -2;
                                moveX = 1;
                                moveRot = -15;
                                mirror = true;
                                under = true;
                            }}
                    );
                    layerOffset = 0.01f;

                    x = 9;
                    y = -7.5f;
                    speed = 1.5f;
                    lifetime = 120;
                    reload = 85;
                    rotate = true;
                    rotateSpeed = 5;
                    shootCone = 15;
                    targetInterval = 20;
                    targetSwitchInterval = 1;

                    top = true;
                    shootWarmupSpeed = 0.05f;
                    minWarmup = 0.4f;
                    useAttackRange = false;

                    bullet = new MissileBulletType(){{
                        shootEffect = new Effect(130, e -> {
                            Draw.color(Color.gray);
                            Angles.randLenVectors(e.id, 10, e.finpow() * 45, e.rotation + 180, 30, (x, y) -> {
                                Fill.circle(e.x + x, e.y + y, e.foutpow() * 3);
                            });
                        });
                        spawnUnit = new MissileUnitType("andon-pod"){{
                            rotateSpeed = 0;
                            missileAccelTime = 35;
                            speed = 2.5f;
                            lifetime = 3.2f * 60;
                            lowAltitude = true;

                            abilities.add(new MoveEffectAbility(){{
                                effect = new Effect(15, e -> {
                                    color(Color.white);
                                    randLenVectors(e.id, 4, e.fin() * 15, e.rotation, 45, (x1, y1) -> {
                                        Lines.lineAngle(e.x + x1, e.y + y1, Mathf.angle(x1, y1), 4 * e.fout());
                                    });
                                });
                                rotation = 180;
                                rotateEffect = true;
                                y = -3f;
                                interval = 3f;
                            }});

                            parts.addAll(
                                    new ShapePart(){{
                                        progress = PartProgress.life.compress(0, missileAccelTime/lifetime);
                                        radius = 0;
                                        radiusTo = 3;
                                        sides = 20;
                                        layer = Layer.effect;
                                        color = ModPal.gelid;
                                    }}
                            );

                            abilities.add(new ShieldArcAbility(){{
                                regen = 0;
                                angle = 140;
                                radius = 5;
                                health = 350;
                                whenShooting = false;
                            }});
                            weapons.add(
                                new PointDefenseWeapon(){{
                                    x = 0;
                                    y = 0;
                                    mirror = false;
                                    reload = 8f;

                                    targetInterval = 9f;
                                    targetSwitchInterval = 12f;
                                    recoil = 0.5f;

                                    bullet = new BulletType(){{
                                        shootSound = Sounds.lasershoot;
                                        shootEffect = Fx.sparkShoot;
                                        hitEffect = Fx.pointHit;
                                        maxRange = 100f;
                                        damage = 38f;
                                    }};
                                }}
                            );
                        }};
                    }};
                }}
            );

            aiController = () -> new ArtilleryAI();
        }};

        sunspot = new HollusUnitType("sunspot"){{
            health = 350;
            maxRange = 150;
            range = 1;
            families.add(Families.hunter);
            hitSize = 11;
            constructor = UnitEntity::create;
            flying = true;
            targetGround = false;
            speed = 4.5f;
            rotateSpeed = 3;
            accel = 0.028f;
            drag = 0.032f;
            engineSize = 0.0F;
            faceTarget = true;
            circleTarget = false;
            omniMovement = false;
            engines.clear();

            float rad = 40;

            setEnginesMirror(
                        new ActivationEngine(32/4, -30/4, 3f, 45 - 90, 0.25f, 1, 1.5f, 7.5f)
            );
            abilities.add(
                    new MoveDamageLineAbility(9, 40/4, 0.85f, 0, 6/4, 1, 4.5f, 0, 0, false, true, Fx.generatespark, Fx.hitLancer, name + "-glow"),
                    new MoveArmorAbility(3.2f, 7, 1.6f, false, name + "-armor",Layer.flyingUnit + 0.1f)
            );

            deathExplosionEffect = new Effect(45, e -> {});
            weapons.addAll(
                new Weapon("none"){{
                    x = 32/4;
                    y = -30/4;
                    shootY = 0;
                    reload = 3;
                    //Note: The range of this is effectively the targeting range of the unit
                    bullet = new FrostBulletType(){{
                        instantDisappear = true;
                        overrideRange = true;
                        speed = 1;
                        range = 150;
                        parentizeEffects = false;
                        shootEffect = new Effect(10, e -> {
                            color(Color.white);
                            randLenVectors(e.id, (int) (Mathf.randomSeed(e.id) * 8 + 2), e.fin() * 15, e.rotation, 45, (x1, y1) -> {
                                Lines.lineAngle(e.x + x1, e.y + y1, Mathf.angle(x1, y1), 4 * e.fout());
                            });
                        });
                        despawnEffect = Fx.none;
                    }};
                    baseRotation = 225;
                    rotationLimit = 0;
                    shootSound = Sounds.none;
                    rotate = false;
                    alternate = false;
                    alwaysShooting = true;
                    controllable = false;
                    minShootVelocity = 3.5f;
                    shootCone = 180;
                    shootStatus = FrostStatusEffects.engineBoost;
                    shootStatusDuration = 75;
                }}
            );
        }};

        javelin = new HollusUnitType("javelin"){{
            families = Seq.with(Families.hunter, Families.assault);
            constructor = UnitEntity::create;
            flying = true;
            speed = 4;
            health = 900;
            armor = 6;
            accel = 0.023f;
            drag = 0.005f;
            rotateSpeed = 3.5f;
            hitSize = 20;
            range = 15;
            maxRange = 55;

            trailLength = 15;
            trailScl = 3;
            engines.add(new ActivationEngine(0, -56/4, 5.5f, -90, 0.45f, 1, 0.6f, 2.25f));
            abilities.add(
                    new MoveDamageLineAbility(65, 40/4, 0.45f, 0, 20/4, 0.6f, 2.25f, 0, false, true, Fx.generatespark, Fx.sparkShoot),
                    new MoveArmorAbility(0.6f, 2.25f, 2.3f, false, name + "-glow", Layer.flyingUnit + 0.1f)
            );
            alwaysShootWhenMoving = true;

            weapons.add(
                new Weapon(NAME + "-flamethrower"){
                    {
                        reload = 135;
                        rotate = true;
                        top = false;
                        alternate = false;
                        shootSound = Sounds.minebeam;
                        layerOffset = -1;
                        x = 40 / 4;
                        int asdf = Vars.tilesize;
                        y = 0;
                        recoil = 4.25f;
                        shootX = 0;
                        shootY = 24 / 4;
                        shootCone = 15;
                        rotateSpeed = 1.5f;
                        rotationLimit = 25;
                        bullet = new ContinuousFlameBulletType() {{
                            recoil = 0.01f;
                            this.damage = 35;
                            this.length = 55;
                            width = 3;
                            this.knockback = 1.0F;
                            this.pierceCap = 1;
                            this.buildingDamageMultiplier = 0.3F;
                            drawFlare = false;
                            this.colors = new Color[]{Color.valueOf("eb7abe").a(0.55F), Color.valueOf("e189f5").a(0.7F), Color.valueOf("907ef7").a(0.8F), Color.valueOf("91a4ff"), Color.white};
                        }};
                        shootStatus = StatusEffects.slow;
                        shootStatusDuration = 15;
                        parentizeEffects = false;
                        continuous = alwaysContinuous = true;
                        predictTarget= false;
                    }}
            );
        }};

        stalagmite = new HollusUnitType("stalagmite"){{
            health = 750;
            armor = 6;
            families.add(Families.hunter);
            constructor = UnitWaterMove::create;
            accel = 0.055f;
            drag = 0.165f;
            speed = 2.75f;
            trailLength = 23;
            waveTrailX = 7f;
            waveTrailY = -6f;
            trailScl = 1.9f;
            hitSize = 96/8;
            lowAltitude = true;

            alwaysShootWhenMoving = true;
            useEngineElevation = false;
            canBoost = true;
            engineSize = 0;

            immunities.add(StatusEffects.corroded);

            weapons.addAll(
                    new VelocityWeapon(name + "-blaster") {{
                        reload = 120;
                        recoilTime = 130;
                        x = 0;
                        y = 2;
                        shootX = -6/4f;
                        shootY = 15/4f;
                        from = 0.55f;
                        to = 1.45f;
                        threshold = 0.6f;
                        target = 2.25f;
                        rotate = true;
                        alternate = false;
                        mirror = false;
                        rotateSpeed = 3.5f;
                        shootSound = Sounds.shootAlt;
                        chargeSound = Sounds.spray;
                        cooldownTime = 350f;

                        shoot = new ShootBarrel(){{
                            barrels = new float[]{
                                    0,0,0,
                                    13/4f, 7/4f, 0
                            };
                            firstShotDelay = 40;
                            shotDelay = 7/2;
                            shots = 6;
                        }};

                        parentizeEffects = true;
                        shootStatus = StatusEffects.slow;
                        shootStatusDuration = 82;

                        bullet = new RicochetBulletType(5, 5, "circle"){{
                            chargeEffect = new Effect(40, e -> {
                                color(Liquids.water.gasColor);
                                alpha(Mathf.clamp(e.foutpow() * 2f));

                                randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 6) + 12), e.finpow() * 45, e.rotation, 40, (x, y) -> {
                                    Fill.circle(e.x + x, e.y + y, e.finpow() * 1.5f);
                                });
                            });

                            frontColor = Liquids.water.color;
                            shrinkX = shrinkY = 0;
                            width = height = 4;
                            trailLength = 8;
                            trailWidth = 2;
                            trailColor = Liquids.water.color;
                            keepVelocity = false;
                            lifetime = 25;
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
                                    if(b != null) alpha(color.a * (b.dst2(e.x + x, e.y + y)/b.hitSize()/b.hitSize()/2 + 0.5f));
                                    Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f);
                                    Draw.color(color);
                                });
                            });
                            trailChance = 1;
                            fragSpread = 3;
                            fragBullets = 3;
                            fragBullet = intervalBullet = new LiquidBulletType(Liquids.water){{
                                speed = 3;
                                lifetime = 25;
                                orbSize = 2;
                                drag = 0.05f;
                            }};
                            intervalBullets = 1;
                            //intervalDelay = 5;
                        }};

                        parts.addAll(new RegionPart("-cartridge"){{
                            layerOffset = 0.01f;
                            under = true;
                            outline = true;
                            moves.add(new PartMove(PartProgress.smoothReload.inv().add(-0.75f).mul(4).clamp().inv(), -2, 0, 0));
                            children.add(new RegionPart("-cartridge-liquid"){{
                                under = true;
                                progress = PartProgress.smoothReload.inv();
                                color = Color.clear;
                                colorTo = Liquids.water.color;
                                layerOffset = 0.01f;
                                outline = false;
                            }});
                        }});
                        parts.add(new RegionPart("-body-front"){{
                            layerOffset = 0.01f;
                            under = true;
                            moves.add(new PartMove(PartProgress.smoothReload.inv().add(-0.5f).mul(2).add(PartProgress.recoil.inv()).clamp(), 0, -2, 0));
                        }});
                        parts.add(new RegionPart("-body"){{
                            layerOffset = 0.01f;
                            under = true;
                            outline = true;
                        }});
                        parts.addAll(new RegionPart("-liquid"){{
                            layerOffset = 0.01f;
                            under = true;
                            progress = PartProgress.smoothReload.inv();
                            color = Color.clear;
                            colorTo = Liquids.water.color;
                        }});
                    }},
                    new ThrustSwingWeapon(name + "-thruster"){{
                        reload = 25;
                        x = 16/4;
                        y = -32/4;
                        shootCone = 180;
                        shootSound = Sounds.minebeam;
                        from = 0;
                        to = 1;
                        threshold = 0.6f;
                        target = 2.225f;
                        rotate = true;
                        rotateSpeed = 1;
                        baseRotation = 225;
                        rotationLimit = 50;
                        targetingBounds = 90;
                        rotateClockwise = true;
                        alternate = false;
                        shootStatusDuration = 10;

                        recoil = 2f;
                        shootY = 1f;
                        bullet = new ContinuousFlameBulletType() {{
                            recoil = -0.15f;
                            this.damage = 35;
                            this.length = 8.5f;
                            width = 2;
                            this.knockback = 1.0F;
                            this.pierceCap = 1;
                            this.buildingDamageMultiplier = 0.3F;
                            drawFlare = false;
                            this.colors = new Color[]{Color.valueOf("eb7abe").a(0.55F), Color.valueOf("e189f5").a(0.7F), Color.valueOf("907ef7").a(0.8F), Color.valueOf("91a4ff"), Color.white};
                        }};
                        parentizeEffects = false;
                        continuous = alwaysContinuous = true;

                        parts = Seq.with(
                                new RegionPart("-front"){{
                                    under = true;
                                }},
                                new RegionPart("-top"){{
                                    under = true;
                                    moves.add(new PartMove(PartProgress.smoothReload, 0f, 1.5f, 0f));
                                }}
                        );
                    }}
            );
        }};

        UnitTypes.alpha.weapons.each(w -> {
            w.reload = 15;
            w.bullet = bouncy;
            w.inaccuracy = 5;
        });
    }
}
