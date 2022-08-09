package frostscape.content;

import arc.graphics.Color;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import frostscape.world.blocks.defense.MinRangeTurret;
import frostscape.world.blocks.drill.CoreSiphon;
import frostscape.world.blocks.environment.CrackedBlock;
import frostscape.world.blocks.environment.SteamVentProp;
import mindustry.content.*;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Sounds;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.DrawHeatInput;
import mindustry.world.draw.DrawHeatOutput;

public class FrostBlocks {
    public static Floor frostStone, frostSnow, andesiteFloor, volcanicAndesiteFloor, paileanFloor;
    public static CrackedBlock crackedAndesiteFloor, fracturedAndesiteFloor;
    public static StaticWall frostWall, volcanicAndesiteWall, magnetiteAndesite;
    public static SteamVentProp frostVent;

    public static CoreSiphon coreSiphon;
    public static ItemTurret pyroclast;

    public static void load(){
        frostStone = new Floor("frost-stone"){{
            variants = 4;
        }};

        frostSnow = new Floor("frost-snow"){{
            variants = 2;
        }};

        andesiteFloor = new Floor("andesite-floor"){{
            variants = 3;
        }};

        volcanicAndesiteFloor = new Floor("volcanic-andesite-floor"){{
            variants = 3;
        }};

        crackedAndesiteFloor = new CrackedBlock("cracked-andesite-floor"){{
            variants = 5;
            blendGroup = volcanicAndesiteFloor;
            minBlinkTime = 60 * 5;
            maxBlinkTime = 60 * 9;
        }};

        fracturedAndesiteFloor = new CrackedBlock("fractured-andesite-floor"){{
            variants = 3;
            blendGroup = volcanicAndesiteFloor;
            minBlinkTime = 60 * 3;
            maxBlinkTime = 60 * 6;
        }};

        paileanFloor = new Floor("pailean-floor"){{
            variants = 2;
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

        coreSiphon = new CoreSiphon("core-siphon"){{
            requirements(Category.production, ItemStack.with());
            liquidPadding = 6;
            this.size = 7;
            envEnabled ^= 2;
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
            minRange = 120;
            range = 242;
            velocityRnd = 0.05f;
            ammo(
                    Items.pyratite, FrostBullets.pyraNapalm
            );
            consumeLiquids(new LiquidStack(Liquids.oil, 1.35f));

            shoot = new ShootSpread(){{
                shotDelay = 5;
                shots = 3;
                inaccuracy = 15;
                spread = 1;
            }};
            shootSound = Sounds.bang;
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
    }
}
