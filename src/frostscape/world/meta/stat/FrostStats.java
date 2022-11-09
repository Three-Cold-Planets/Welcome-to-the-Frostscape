package frostscape.world.meta.stat;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.Stats;

import static frostscape.world.meta.stat.FrostStatCats.family;

public class FrostStats {
    public static Stat
        familyName = new Stat("familyName", family),
        familyLink = new Stat("familyLink", family),
        envCategory = new Stat("envcategory", FrostStatCats.scanning)
    ;
}
