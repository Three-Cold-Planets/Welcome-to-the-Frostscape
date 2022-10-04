package frostscape.content;

import frostscape.type.upgrade.Upgrade;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;

public class FrostUpgrades {

    public static Upgrade improvedWelding, INVINCIBLE, wheeeez,
            //Landmine-related upgrades
            improvedBase, improvedPayload
            ;
    public static void load(){
        improvedWelding = new Upgrade("improved-welding"){{

        }};

        INVINCIBLE = new Upgrade("INVINCIBLE"){{

        }};
        wheeeez = new Upgrade("wheeeez"){{

        }};

        improvedBase = new Upgrade("improved-base-landmine"){{

        }};

        improvedPayload = new Upgrade("improved-payload-landmine"){{

        }};
    }
}
