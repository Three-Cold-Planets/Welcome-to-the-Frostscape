package frostscape.content;

import frostscape.type.upgrade.Upgrade;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;

public class FrostUpgrades {

    public static Upgrade improvedWelding, INVINCIBLE,
            //Landmine-related upgrades
            improvedBase, improvedPayload
            ;
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

        improvedBase = new Upgrade("improved-base-landmine", ItemStack.with()){{
            healthMultiplier = new float[]{
                    1.1f,
                    1.5f,
                    2.0f
            };
            speedMultiplier = new float[]{
                    1,
                    0.85f,
                    0.65f
            };
            stacks = 3;
        }};

        improvedPayload = new Upgrade("improved-payload-landmine", ItemStack.with()){{
            damageMultiplier = new float[]{
                    1.2f,
                    1.5f,
                    1.8f
            };
            rangeMultiplier = new float[]{
                    1.5f,
                    2.1f,
                    2.8f
            };
            stacks = 3;
        }};
    }
}
