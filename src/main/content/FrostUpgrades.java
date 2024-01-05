package main.content;

import main.world.systems.upgrades.Upgrade;

public class FrostUpgrades {

    public static Upgrade improvedWelding, INVINCIBLE, wheeeez,
            //Landmine-related upgrades
            improvedBase, improvedPayload, alwaysArmed;
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

        //Allows landmines to trigger regardless of enabled state or team of unit
        alwaysArmed = new Upgrade("always-armed-landmine"){{

        }};
    }
}
