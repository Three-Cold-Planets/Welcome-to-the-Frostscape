package frostscape.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.serialization.Json;
import frostscape.entities.ability.MoveDamageLineAbility;
import frostscape.entities.bullet.FrostBulletType;
import frostscape.type.HollusUnitType;
import frostscape.type.HollusUnitType.ActivationEngine;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.MoveLightningAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.part.DrawPart;
import mindustry.entities.pattern.ShootHelix;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.io.JsonIO;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static frostscape.Main.NAME;

public class FrostUnits {
    public static HollusUnitType
    sunspot, javelin;

    public static void load(){
        sunspot = new HollusUnitType("sunspot"){{
            families = Seq.with(Families.hunter);
            hitSize = 70/8;
            constructor = UnitEntity::create;
            flying = true;
            speed = 5;
            accel = 0.023f;
            drag = 0.015f;
            faceTarget = true;
            setEnginesMirror(
                        new ActivationEngine(24/4, -32/4, 3.5f, 15 - 90, 0.45f, 1, 1, 3.5f)
            );
            abilities.add(
                    new MoveLightningAbility(0, 0, 0, 0, 2, 4.5f, Color.white, name + "-glow"),
                    new MoveDamageLineAbility(9, 40/4, 0.85f, 6/4, 1, 4.5f, 0, Fx.sparkShoot)
            );

            weapons.add(
                new Weapon("none"){{
                    x = 24/4;
                    y = -32/4;
                    reload = 10;
                    //Note: The range of this is effectively the targeting range of the unit
                    bullet = new FrostBulletType(){{
                        instantDisappear = true;
                        speed = 1;
                        range = 550;
                        recoil = 0.5f;
                        shootEffect = new Effect(12, e -> {
                            Draw.color(Color.white);
                            Angles.randLenVectors(e.id, (int) (Mathf.randomSeed(e.id) * 3 + 2), e.fin(Interp.pow4) * 145, e.rotation, 15, (x1, y1) -> {
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
                    shootStatusDuration = 65;
                }}
            );
        }};

        javelin = new HollusUnitType("javelin"){{
            families = Seq.with(Families.hunter, Families.assault);
            constructor = UnitEntity::create;
            flying = true;
            speed = 4;
            accel = 0.023f;
            drag = 0.005f;

            engines.add(new ActivationEngine(0, -56/4, 5.5f, -90, 0.45f, 1, 0.6f, 2.25f));
            abilities.add(
                    new MoveLightningAbility(0, 0, 0, 0, 0.6f, 2.25f, Color.white, name + "-glow"),
                    new MoveDamageLineAbility(35, 40/4, 0.45f, 20/4, 0.6f, 2.25f, 0, Fx.generatespark)
            );

            weapons.add(
                new Weapon(NAME + "-javelin-mounts-under"){
                    {
                        reload = 135;
                        top = false;
                        layerOffset = -1;
                        x = 0;
                        y = 0;
                        recoil = 4.25f;
                        shootX = 32 / 4;
                        shootY = 56 / 4;
                        range = 50;
                        shootCone = 15;
                        bullet = new FrostBulletType(){{
                            speed = 6;
                            range = 120;
                            overrideRange = true;
                            instantDisappear = true;

                            spawnBullets.add(new MissileBulletType(6, 15, "missile") {
                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);

                                Lines.stroke((0.7f + Mathf.absin(10, 0.7f)) * b.fin() * 1.6f, Palf.hunter);

                                for (int i = 0; i < 6; i++) {
                                    float rot = i * 360f / 6 - 360 * b.fout(Interp.smooth);
                                    Lines.arc(b.x, b.y, 125 * b.fin(Interp.smooth) + 3f, 0.08f + b.fin() * 0.06f, rot);
                                }
                            }

                            {
                                lifetime = 110;
                                width = 5;
                                height = 8;
                                collides = false;
                                keepVelocity = false;
                                homingPower = 0;
                                drag = 0.045f;
                                lightningColor = Color.white;
                                frontColor = Color.white;
                                backColor = Pal.lancerLaser;
                                trailColor = Pal.lancerLaser;
                                fragBullets = 1;
                                fragBullet = new EmpBulletType() {{
                                    float rad = 125;
                                    damage = 35;
                                    instantDisappear = true;
                                    despawnHit = true;
                                    hitEffect = new Effect(50f, 100f, e -> {
                                        e.scaled(7f, b -> {
                                            color(Palf.hunter, b.fout());
                                            Fill.circle(e.x, e.y, rad);
                                        });

                                        color(Palf.hunter);
                                        stroke(e.fout() * 3f);
                                        Lines.circle(e.x, e.y, rad);

                                        Fill.circle(e.x, e.y, 12f * e.fout());
                                        color();
                                        Fill.circle(e.x, e.y, 6f * e.fout());
                                        Drawf.light(e.x, e.y, rad * 1.6f, Palf.hunter, e.fout());
                                    });
                                    hitPowerEffect = Fx.none;
                                    applyEffect = Fx.none;
                                    healPercent = 0;
                                    radius = rad;
                                }};

                                for (int i = 0; i < 5; i++) {
                                    final int j = i;
                                    spawnBullets.add(
                                            new MissileBulletType(6 + i * 0.6f, 10 + 5 * i, "missile") {
                                                {
                                                    hitSound = Sounds.spark;
                                                    lifetime = 55 + 10 * j;
                                                    width = 8 + j * 1.75f;
                                                    height = 10 + j * 1.75f;
                                                    keepVelocity = false;
                                                    homingPower = 0;
                                                    drag = 0.045f;
                                                    lightningDamage = 10 + 10 * j;
                                                    lightning = 1 + Mathf.floor(j / 2 * j / 2);
                                                    lightningLength = 6 + j;
                                                    lightningColor = Color.white;
                                                    hitShake = j;
                                                    frontColor = Color.white;
                                                    backColor = Pal.lancerLaser;
                                                    trailColor = Pal.lancerLaser;
                                                }
                                            }
                                    );
                                }
                            }}
                        );
                    }};
                    parentizeEffects = false;
                }}
            );
        }};
    }
}
