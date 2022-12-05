package frostscape.content;

import arc.graphics.g2d.*;
import arc.math.*;
import frostscape.entities.bullet.BouncyBulletType;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.graphics.Pal;

public class FrostBullets {
    //placeholder used in MoveDamageLineAbility, do not modify!
    public static RailBulletType placeholder1;

    public static BouncyBulletType pyraNapalm, pyraGel;

    public static void load(){

        placeholder1 = new RailBulletType(){{
            trailEffect = Fx.none;
        }};

        pyraGel = new BouncyBulletType(2f, 10, "shell"){{
            lifetime = 150;
            minLife = 48f;
            hitEffect = Fx.blastExplosion;
            despawnEffect = Fx.blastExplosion;
            width = 16;
            height = 16;
            shrinkX = 0.4f;
            shrinkY = 0.7f;
            status = StatusEffects.burning;
            statusDuration = 12f * 60f;
            frontColor = Pal.lightishOrange;
            backColor = Pal.lightOrange;
            gravity = 0.0155f;
            startingLift = 0.15f;
            bounceShake = 0.7f;
            bounceEfficiency = 0.95f;
            bounceForce = 0;
            hitShake = 3.2f;
            hittable = true;
            bounceEffect = new MultiEffect(){{
                effects = new Effect[]{
                        Fx.unitLandSmall,
                        Fx.fireHit
                };
            }};
            fragBullets = 5;
            fragSpread = 2;
            fragLifeMin = 0.7f;
            fragLifeMax = 1;
            incendAmount = 5;
            incendChance = 1;
            bounceIncend = 2;
            bounceIncendChance = 1;
            puddleLiquid = Liquids.oil;
            puddleAmount = 6;
            splashDamage = 55;
            splashDamageRadius = 16;

            fragBullet = new BasicBulletType(2.6f, 18){{
                width = 10f;
                height = 12f;
                frontColor = Pal.lightishOrange;
                backColor = Pal.lightOrange;
                status = FrostStatusEffects.napalm;
                hitEffect = new MultiEffect(Fx.hitBulletSmall, Fx.fireHit);

                ammoMultiplier = 5;

                splashDamage = 12f;
                splashDamageRadius = 22f;

                incendAmount = 1;
                incendChance = 1;
                lifetime = 60f;
                fragBullets = 2;
                fragBullet = new LiquidBulletType(Liquids.oil){{
                    speed = 0.25f;
                    puddleAmount = 3;
                }};
            }};
            scaleLife = true;
            //Todo: fix calcMinLife
            //calcMinLife();
        }};

        pyraNapalm = new BouncyBulletType(2.5f, 10, "shell"){{
            lifetime = 120;
            drag = 0.006f;
            minLife = 55f;
            hitEffect = Fx.blastExplosion;
            despawnEffect = Fx.blastExplosion;
            width = 16;
            height = 16;
            shrinkX = 0.4f;
            shrinkY = 0.7f;
            status = StatusEffects.burning;
            statusDuration = 12f * 60f;
            frontColor = Pal.lightishOrange;
            backColor = Pal.lightOrange;
            gravity = 0.005f;
            startingLift = 0.15f;
            bounceShake = 0.7f;
            bounceEfficiency = 0.65f;
            bounceForce = 10;
            bounceCap = 4;
            hitShake = 3.2f;
            hittable = true;
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
            hitEffect = new MultiEffect(){{
                effects = new Effect[]{
                        new Effect(35, e -> {
                            Draw.color(Pal.lightPyraFlame);

                            Lines.stroke(e.fout() * 0.65f);
                            Lines.circle(e.x, e.y, e.finpow() * 35);
                        }),

                        new Effect(135, e -> {
                            Angles.randLenVectors(e.id, (int) (Mathf.randomSeed(e.id, 3) + 5), e.fin() * 54 + 6, (x, y) -> {
                                Draw.color(Pal.darkPyraFlame);
                                Fill.circle(e.x + x, e.y + y, 5 * e.fout(Interp.pow4));
                            });
                        })
                };
            }};
            fragBullets = 3;
            fragBullet = pyraGel;
            fragSpread = 20;
            fragRandomSpread = 5;
            fragLifeMin = 0.2f;
            fragLifeMax = 0.4f;
            incendAmount = 5;
            incendChance = 1;
            bounceIncend = 2;
            bounceIncendChance = 1;
            puddleLiquid = Liquids.oil;
            puddleAmount = 6;
            splashDamage = 55;
            splashDamageRadius = 16;
            scaleLife = true;
            //Todo: fix calcMinLife
            //calcMinLife();
        }};
    }
}
