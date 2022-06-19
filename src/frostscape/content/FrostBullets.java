package frostscape.content;

import frostscape.entities.BouncyBulletType;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.graphics.Pal;

public class FrostBullets {
    public static BouncyBulletType pyraNapalm;

    public static void load(){
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
            maxBounces = 4;
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
                status = StatusEffects.burning;
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

        UnitTypes.fortress.weapons.each(w -> w.bullet = pyraNapalm);
    }
}
