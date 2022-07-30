package frostscape.content;

import arc.math.geom.Point2;
import frostscape.world.blocks.defense.MinRangeTurret;
import frostscape.world.blocks.environment.SteamVentProp;
import mindustry.content.*;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Sounds;
import mindustry.io.SaveIO;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.*;

public class FrostBlocks {
    public static Floor frostStone, frostSnow, andesiteFloor, volcanicAndesiteFloor, paileanFloor;
    public static StaticWall frostWall, volcanicAndesiteWall, magnetiteAndesite;
    public static SteamVentProp frostVent;
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

        pyroclast = new MinRangeTurret("pyroclast"){{
            requirements(Category.turret, ItemStack.with());
            size = 3;
            health = 54 * size * size;
            reload = 235;
            minRange = 120;
            range = 242;
            velocityRnd = 0.05f;
            ammo(
                    Items.pyratite, FrostBullets.pyraGel
            );
            consumeLiquids(new LiquidStack(Liquids.oil, 1.35f));

            shoot = new ShootSpread(){{
                shotDelay = 25;
                shots = 3;
                inaccuracy = 5;
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
