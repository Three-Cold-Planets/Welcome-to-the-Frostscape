package frostscape.content;

import frostscape.type.upgrade.Upgrade;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;

public class FrostUpgrades {

    public static Upgrade improvedWelding, INVINCIBLE;
    public static void load(){
        improvedWelding = new Upgrade("improved-welding", ItemStack.with()){{
            healthMultiplier = new float[]{
                    1.15f
            };
        }};

        INVINCIBLE = new Upgrade("INVINCIBLE", ItemStack.with()){{
            healthMultiplier = new float[]{
                    1000000
            };
        }};
    }
}
