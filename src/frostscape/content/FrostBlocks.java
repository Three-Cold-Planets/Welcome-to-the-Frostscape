package frostscape.content;

import frostscape.world.blocks.defense.MinRangeTurret;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.StatValues;

public class FrostBlocks {
    public static ItemTurret pyroclast;

    public static void load(){
        pyroclast = new MinRangeTurret("pyroclast"){{
            requirements(Category.turret, ItemStack.with());
            size = 3;
            health = 54 * size * size;
            reload = 100;
            minRange = 120;
            range = 242;
            ammo(
                    Items.pyratite, FrostBullets.pyraNapalm
            );
            consumeLiquids(new LiquidStack(Liquids.oil, 1.35f));
        }};
    }
}
