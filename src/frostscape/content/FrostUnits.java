package frostscape.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.*;
import frostscape.entities.ability.MoveDamageLineAbility;
import frostscape.entities.bullet.FrostBulletType;
import frostscape.type.HollusUnitType;
import frostscape.type.HollusUnitType.ActivationEngine;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.abilities.MoveLightningAbility;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class FrostUnits {
    public static UnitType
    sunspot;

    public static void load(){
        sunspot = new HollusUnitType("sunspot"){{
            family = Families.hunter;
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
                    new MoveDamageLineAbility(15, 40/4, 0.85f, 6/4, 1, 4.5f, 0, Fx.sparkShoot)
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
                    shootCone = 120;
                    shootStatus = FrostStatusEffects.engineBoost;
                    shootStatusDuration = 65;
                }}
            );
        }};
    }
}
