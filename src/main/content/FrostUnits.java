package main.content;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import main.ai.types.FixedFlyingAI;
import main.entities.ability.MoveArmorAbility;
import main.entities.ability.MoveDamageLineAbility;
import main.entities.bullet.FrostBulletType;
import main.entities.bullet.RicochetBulletType;
import main.entities.part.LightPart;
import main.graphics.ModPal;
import main.type.HollusUnitType;
import main.type.weapon.ThrustSwingWeapon;
import main.type.weapon.VelocityWeapon;
import mindustry.Vars;
import mindustry.ai.types.CargoAI;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static main.Frostscape.NAME;
import static mindustry.content.Fx.circleColorSpark;

public class FrostUnits {

    public static UnitType serpieDrone;

    public static HollusUnitType
    sunspot, javelin, stalagmite, cord, hearth, hearthDefend, hearthAttack;

    public static HollusUnitType
    upgradeDrone;

    public static void load(){

        RicochetBulletType bouncy = new RicochetBulletType(6, 6, "bullet"){{
            drag = 0.015f;
            bounciness = 1f;
            width = 4;
            height = 8;
            shrinkX = 0;
            shrinkY = 0;
            lifetime = 35;
            knockback = 0.75f;
            hitEffect = bounceEffect = Fx.none;
            despawnEffect = Fx.hitBulletColor;
            trailColor = Pal.suppress;
            trailWidth = 1f;
            trailLength = 4;
            bounceSame = true;
            removeAfterPierce = false;
            pierceCap = 2;
            status = FrostStatusEffects.conflexInternal;
            statusDuration = 10;
            frontColor = Color.white;
            backColor = Pal.suppress;
        }};

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
            armor = 5;
            speed = 0.85f;
            constructor = MechUnit::create;
            hitSize = 11;
            families.add(Families.specialist);
            weapons.add(
                    new Weapon(NAME + "-cord-weapon"){{
                        //I hate how hacky this is, but it's easier for me to do this since im in the IDE already
                        load = () -> region = outlineRegion = Core.atlas.find("clear");
                        x = 6.325f;
                        y = 1.125f;
                        minWarmup = 0.95f;
                        shootWarmupSpeed = 0.05f;
                        shootSound = Sounds.dullExplosion;
                        reload = 65;
                        alternate = true;
                        top = false;
                        cooldownTime = 75;
                        DrawPart.PartMove mover = new DrawPart.PartMove(DrawPart.PartProgress.recoil, 0.2f, -0.12f, 20);

                        parts.addAll(
                            new RegionPart("-bottom"){{
                                moves.add(mover);
                                under = true;
                                outline = false;
                                heatLayerOffset = 0;
                                heatLight = true;
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
                                heatLight = true;
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


                        bullet = new BasicBulletType(5.75f, 15){
                            @Override
                            public void init(){
                                super.init();
                                range = 16 * 8;
                            }

                            {
                            collidesGround = true;
                            drag = 0.06f;
                            shootEffect = Fx.explosion;
                            hitEffect = despawnEffect = Fx.hitBulletColor;
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
                            statusDuration = 45;
                            frontColor = Color.white;
                            backColor = Pal.suppress;
                        }};
                    }}
            );
        }};

        sunspot = new HollusUnitType("sunspot"){{
            maxRange = 150;
            range = 10;
            families.add(Families.hunter);
            hitSize = 70/8;
            constructor = UnitEntity::create;
            flying = true;
            speed = 3;
            rotateSpeed = 3;
            accel = 0.038f;
            drag = 0.028f;
            engineSize = 0.0F;
            faceTarget = true;
            circleTarget = true;
            omniMovement = false;
            engines.clear();
            aiController = () -> new FixedFlyingAI();
            setEnginesMirror(
                        new ActivationEngine(24/4, -30/4, 3.5f, 15 - 90, 0.45f, 1, 1, 3.5f)
            );
            abilities.add(
                    new MoveDamageLineAbility(9, 40/4, 0.85f, 6/4, 1, 4.5f, 0, true, true, Fx.colorSpark, Fx.hitLancer),
                    new MoveArmorAbility(1.2f, 5, 0.6f, true, name + "-glow",Layer.flyingUnit + 0.1f)
            );

            deathExplosionEffect = new Effect(45, e -> {});
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
                            color(Color.white);
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
                }},
                new Weapon() {{
                    shootOnDeath = true;
                    shootCone = 180;
                    reload = 60;
                    ejectEffect = Fx.none;
                    shootSound = Sounds.none;
                    x = shootY = 0.0f;
                    mirror = false;
                    noAttack = true;
                    controllable = false;
                    rotate = false;
                    inaccuracy = 180;
                    bullet = new BulletType(){{
                        instantDisappear = true;
                        fragBullets = 1;
                        float rad = 40;
                        fragSpread = fragRandomSpread = 0;
                        fragBullet = new BasicBulletType(5, 0, NAME + "-sunspot-tip") {
                            @Override
                            public void draw(Bullet b) {
                                Draw.rect(this.frontRegion, b.x, b.y, width, height, b.fout() * b.fout() * 360 * 2.5f);
                                Draw.z(Layer.effect);

                                stroke((0.7f + Mathf.absin(10, 0.7f)) * b.fin() * 1.6f, ModPal.hunter);

                                float progress = b.fin(Interp.smooth);

                                for (int i = 0; i < 6; i++) {
                                    float rot = i * 360f / 6 - 360 * progress;
                                    Lines.arc(b.x, b.y, rad * progress + 3f, 0.08f + b.fin() * 0.06f, rot);
                                }

                                float fastProgress = Mathf.clamp(progress * 3 - 2);

                                Draw.blend(Blending.additive);
                                Fill.light(b.x, b.y, 20, rad, Tmp.c1.set(Color.white).lerp(ModPal.hunter, fastProgress).a(fastProgress * 0.2f), Tmp.c2.set(ModPal.hunter).a(0));
                                Draw.blend();
                            }

                            public void createFrags(Bullet b, float x, float y) {
                                if (this.fragBullet != null && (this.fragOnAbsorb || !b.absorbed)) {
                                    for(int i = 0; i < this.fragBullets; ++i) {
                                        float a = b.rotation();
                                        this.fragBullet.create(b, x, y, a, Mathf.random(this.fragVelocityMin, this.fragVelocityMax), Mathf.random(this.fragLifeMin, this.fragLifeMax));
                                    }
                                }
                            }

                            {
                                homingPower = 0.055f;
                                homingRange = rad * 2;
                                layer = Layer.groundUnit + 0.1f;
                                drag = 0.045f;
                                collideTerrain = true;
                                collidesTiles = true;
                                collides = true;
                                collidesAir = false;
                                hitSound = Sounds.spark;
                                rangeOverride = 0.0F;
                                shootEffect = Fx.none;
                                lifetime = 95;
                                shrinkX = shrinkY = 0;
                                splashDamageRadius = 55.0F;
                                splashDamage = 5.0F;
                                spin = 0.85f;
                                hittable = false;
                                collidesAir = true;
                                fragBullets = 1;
                                fragBullet = new EmpBulletType() {
                                    {
                                    damage = 35;
                                    instantDisappear = true;
                                    despawnHit = true;
                                    radius = rad;
                                    hitPowerEffect = circleColorSpark;
                                    hitEffect = new Effect(50f, 100f, e -> {
                                        e.scaled(7f, b -> {
                                            color(ModPal.hunter, b.fout());
                                            Draw.blend(Blending.additive);
                                            Fill.light(b.x, b.y, 20, rad, Tmp.c1.set(ModPal.hunter).a(b.fout()), Tmp.c2.set(Tmp.c1).a(b.fin() * b.fslope()));
                                            Draw.blend();
                                        });

                                        color(ModPal.hunter);
                                        stroke(e.fout() * 3f);
                                        Lines.circle(e.x, e.y, radius);

                                        Fill.circle(e.x, e.y, 12f * e.fout());
                                        color();
                                        Fill.circle(e.x, e.y, 6f * e.fout());
                                        Drawf.light(e.x, e.y, radius * 1.6f, ModPal.hunter, e.fout());
                                    });
                                    hitColor = ModPal.hunter;
                                    status = FrostStatusEffects.conflexInternal;
                                    statusDuration = 6.5f * 60;
                                }};
                            }
                        };
                    }};
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

            trailLength = 15;
            trailScl = 3;
            engines.add(new ActivationEngine(0, -56/4, 5.5f, -90, 0.45f, 1, 0.6f, 2.25f));
            abilities.add(
                    new MoveDamageLineAbility(65, 40/4, 0.45f, 20/4, 0.6f, 2.25f, 0, false, true, Fx.generatespark, Fx.sparkShoot),
                    new MoveArmorAbility(0.6f, 2.25f, 2.3f, false, name + "-glow", Layer.flyingUnit + 0.1f)
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
                            intervalDelay = 5;
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
                                    moves.add(new PartMove(p -> p.smoothReload, 0f, 1.5f, 0f));
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
