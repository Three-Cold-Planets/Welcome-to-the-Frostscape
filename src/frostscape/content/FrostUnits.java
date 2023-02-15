package frostscape.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import frostscape.Frostscape;
import frostscape.entities.BaseBulletType;
import frostscape.entities.ability.MoveArmorAbility;
import frostscape.entities.ability.MoveDamageLineAbility;
import frostscape.entities.bullet.FrostBulletType;
import frostscape.entities.part.TestRegionPart;
import frostscape.graphics.trail.SmokeTrail;
import frostscape.type.HollusUnitType;
import frostscape.type.weapon.SwingWeapon;
import frostscape.type.weapon.VelocityWeapon;
import frostscape.util.DrawUtils;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.abilities.MoveLightningAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.part.RegionPart;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Liquid;
import mindustry.type.Weapon;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static frostscape.Frostscape.NAME;

public class FrostUnits {
    public static HollusUnitType
    sunspot, javelin, stalagmite;

    public static HollusUnitType
    upgradeDrone;

    public static void load(){
        sunspot = new HollusUnitType("sunspot"){{
            maxRange = 150;
            range = 10;
            families = Seq.with(Families.hunter);
            hitSize = 70/8;
            constructor = UnitEntity::create;
            flying = true;
            speed = 3;
            rotateSpeed = 3;
            accel = 0.038f;
            drag = 0.028f;
            faceTarget = true;
            circleTarget = true;
            omniMovement = false;
            engines.clear();
            setEnginesMirror(
                        new ActivationEngine(24/4, -32/4, 3.5f, 15 - 90, 0.45f, 1, 1, 3.5f)
            );
            abilities.add(
                    new MoveLightningAbility(0, 0, 0, 0, 2, 4.5f, Color.white, name + "-glow"),
                    new MoveDamageLineAbility(9, 40/4, 0.85f, 6/4, 1, 4.5f, 0, false, true, Fx.sparkShoot),
                    new MoveArmorAbility(1.2f, 5, 0.6f, Layer.flyingUnit + 0.1f)
            );

            weapons.add(
                new Weapon("none"){{
                    x = 24/4;
                    y = -32/4;
                    reload = 10;
                    //Note: The range of this is effectively the targeting range of the unit
                    bullet = new FrostBulletType(){{
                        instantDisappear = true;
                        overrideRange = true;
                        speed = 1;
                        range = 150;
                        recoil = 0.5f;
                        shootEffect = new Effect(12, e -> {
                            Draw.color(Color.white);
                            randLenVectors(e.id, (int) (Mathf.randomSeed(e.id) * 8 + 2), e.fin(Interp.pow4) * 145, e.rotation, 15, (x1, y1) -> {
                                Lines.lineAngle(e.x, e.y, Mathf.angle(x1, y1), e.fout(Interp.pow4) * 8);
                            });
                        });
                        despawnEffect = Fx.none;
                    }};
                    baseRotation = 180;
                    shootSound = Sounds.none;
                    rotate = false;
                    alternate = false;
                    shootCone = 180;
                    shootStatus = FrostStatusEffects.engineBoost;
                    shootStatusDuration = 35;
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
            hitSize = 15;
            range = 15;
            maxRange = 55;

            engines.add(new ActivationEngine(0, -56/4, 5.5f, -90, 0.45f, 1, 0.6f, 2.25f));
            abilities.add(
                    new MoveLightningAbility(0, 0, 0, 0, 0.6f, 2.25f, Color.white, name + "-glow"),
                    new MoveDamageLineAbility(65, 40/4, 0.45f, 20/4, 0.6f, 2.25f, 0, false, true, Fx.generatespark),
                    new MoveArmorAbility(0.6f, 2.25f, 2.3f, Layer.flyingUnit + 0.1f)
            );

            weapons.add(
                new Weapon(NAME + "-javelin-mounts-under"){
                    {
                        reload = 135;
                        rotate = true;
                        top = false;
                        alternate = false;
                        shootSound = Sounds.minebeam;
                        layerOffset = -1;
                        x = 40 / 4;
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
                    }}
            );
        }};

        stalagmite = new HollusUnitType("stalagmite"){{
            health = 750;
            armor = 6;
            families = Seq.with(Families.hunter);
            constructor = UnitWaterMove::create;
            accel = 0.055f;
            drag = 0.165f;
            speed = 2.75f;
            trailLength = 23;
            waveTrailX = 7f;
            waveTrailY = -6f;
            trailScl = 1.9f;
            hitSize = 96/8;

            alwaysShootWhenMoving = true;
            useEngineElevation = false;

            immunities.add(StatusEffects.corroded);

            weapons.addAll(
                    new VelocityWeapon(name + "-blaster") {{
                        reload = 120;
                        recoilTime = 65;
                        x = 24/4;
                        shootX = -1;
                        shootY = 4.25f;
                        from = 0.55f;
                        to = 1.45f;
                        threshold = 0.6f;
                        target = 2.25f;
                        rotate = true;
                        alternate = false;
                        rotateSpeed = 1.5f;
                        rotationLimit = 120;
                        shootSound = Sounds.shootAlt;
                        chargeSound = Sounds.spray;
                        cooldownTime = 350f;

                        shoot.firstShotDelay = 40;
                        shoot.shotDelay = 7;
                        shoot.shots = 6;
                        parentizeEffects = true;
                        shootStatus = StatusEffects.slow;
                        shootStatusDuration = 82;

                        bullet = new RailBulletType(){{
                            chargeEffect = new Effect(40, e -> {
                                color(Liquids.water.gasColor);
                                alpha(Mathf.clamp(e.foutpow() * 2f));

                                Angles.randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 6) + 12), e.finpow() * 45, e.rotation, 40, (x, y) -> {
                                    Fill.circle(e.x + x, e.y + y, e.finpow() * 1.5f);
                                });
                            });

                            damage = 35;
                            knockback = 4;
                            pierce = false;
                            pierceBuilding = false;
                            pierceArmor = true;
                            status = StatusEffects.wet;
                            statusDuration = 180;
                            pierceEffect = new Effect(55, e -> {
                                color(Liquids.water.color);
                                alpha(Mathf.clamp(e.foutpow() * 2f));

                                Angles.randLenVectors(e.id, 4, e.finpow() * 25, e.rotation, 360, (x, y) -> {
                                    Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f);
                                });
                            });

                            pointEffect = new Effect(20, e -> {
                                color(Liquids.water.color);
                                alpha(Mathf.clamp(e.fout() * 2f));

                                Angles.randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 6) + 12), e.finpow() * 45, e.rotation, 5, (x, y) -> {
                                    Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f);
                                });
                            });
                            pointEffectSpace = 10;
                        }};

                        parts.addAll(new RegionPart("-container"){{
                            layerOffset = 0.01f;
                            under = true;
                            outline = true;
                            moves.add(new PartMove(PartProgress.smoothReload.inv().add(-0.75f).mul(4).clamp(), -2, 0, 0));
                            children.add(new RegionPart("-container-liquid"){{
                                under = true;
                                progress = PartProgress.smoothReload.inv();
                                color = Color.clear;
                                colorTo = Liquids.water.color;
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
                    new SwingWeapon(name + "-thruster"){{
                        reload = 25;
                        x = 16/4;
                        y = -32/4;
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
                        shootCone = 15;
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
                                    moves.add(new PartMove(p -> p.smoothReload, 0f, 1.5f, 0f));
                                }}
                        );
                    }}
            );
        }};
    }
}
