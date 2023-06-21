package main.world.meta.stat;

import mindustry.world.meta.Stat;

import static main.world.meta.stat.FrostStatCats.family;

public class FrostStats {
    public static Stat
        familyName = new Stat("familyName", family),
        familyLink = new Stat("familyLink", family),
        envCategory = new Stat("envcategory", FrostStatCats.scanning),

        shieldDamageMultiplier = new Stat("shieldDamageMultiplier");
    ;
}
