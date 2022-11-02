package frostscape.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.Seq;
import frostscape.entities.ability.MoveArmorAbility;
import frostscape.entities.ability.MoveDamageLineAbility;
import frostscape.entities.bullet.FrostBulletType;
import frostscape.type.HollusUnitType;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.abilities.MoveLightningAbility;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Weapon;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static frostscape.Frostscape.NAME;

public class FrostUnits {
    public static HollusUnitType
    sunspot, javelin;

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
                            Angles.randLenVectors(e.id, (int) (Mathf.randomSeed(e.id) * 8 + 2), e.fin(Interp.pow4) * 145, e.rotation, 15, (x1, y1) -> {
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
            accel = 0.023f;
            drag = 0.005f;
            rotateSpeed = 3;
            hitSize = 15;

            engines.add(new ActivationEngine(0, -56/4, 5.5f, -90, 0.45f, 1, 0.6f, 2.25f));
            abilities.add(
                    new MoveLightningAbility(0, 0, 0, 0, 0.6f, 2.25f, Color.white, name + "-glow"),
                    new MoveDamageLineAbility(65, 40/4, 0.45f, 20/4, 0.6f, 2.25f, 0, false, true, Fx.generatespark)
            );

            weapons.add(
                new Weapon(NAME + "-javelin-mounts-under"){
                    {
                        reload = 135;
                        rotate = true;
                        top = false;
                        alternate = false;
                        layerOffset = -1;
                        x = 40 / 4;
                        y = 0;
                        recoil = 4.25f;
                        shootX = 0;
                        shootY = 40 / 4;
                        range = 50;
                        shootCone = 15;
                        rotateSpeed = 1.5f;
                        rotationLimit = 20;
                        bullet = new ContinuousFlameBulletType() {
                            {
                                this.damage = 35;
                                this.length = 55;
                                this.knockback = -1.0F;
                                this.pierceCap = 1;
                                this.buildingDamageMultiplier = 0.3F;
                                this.colors = new Color[]{Color.valueOf("eb7abe").a(0.55F), Color.valueOf("e189f5").a(0.7F), Color.valueOf("907ef7").a(0.8F), Color.valueOf("91a4ff"), Color.white};
                            }
                        };
                        parentizeEffects = false;
                        continuous = alwaysContinuous = true;
                    }}
            );
        }};
    }
}
