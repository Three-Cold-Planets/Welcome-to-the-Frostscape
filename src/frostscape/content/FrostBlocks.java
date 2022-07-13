package frostscape.content;

import arc.struct.Seq;
import frostscape.world.blocks.defense.MinRangeTurret;
import mindustry.content.*;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Sounds;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.SteamVent;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.StatValues;

import java.lang.reflect.Array;

public class FrostBlocks {
    public static ItemTurret pyroclast;
    public static SteamVent frostVent;

    public static void load(){
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

        frostVent = new SteamVent("frost-vent"){{
            variants = 1;
            attributes.set(Attribute.steam, 1f);
        }};
    }
}
