package main.content;

import arc.graphics.Color;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import main.graphics.ModPal;
import main.util.StatUtils;
import main.world.meta.Family;
import mindustry.content.UnitTypes;
import mindustry.ctype.UnlockableContent;

public class Families {
    public static Family assault, hunter, specialist;

    public static void load(){
        assault = new Family("assault", Color.valueOf("ffa465"));
        assault.members.addAll(UnitTypes.dagger, UnitTypes.mace, UnitTypes.fortress, UnitTypes.scepter, UnitTypes.reign, UnitTypes.flare, UnitTypes.horizon, UnitTypes.zenith, UnitTypes.antumbra, UnitTypes.eclipse, UnitTypes.stell, UnitTypes.locus, UnitTypes.precept, UnitTypes.vanquish, UnitTypes.conquer);
        hunter = new Family("hunter", ModPal.hunter);
        specialist = new Family("specialist", ModPal.specialist);
        specialist.members.addAll(UnitTypes.crawler, UnitTypes.atrax, UnitTypes.spiroct, UnitTypes.arkyid, UnitTypes.toxopid, UnitTypes.elude, UnitTypes.avert, UnitTypes.obviate, UnitTypes.quell, UnitTypes.disrupt);


        ObjectMap<UnlockableContent, Seq<Family>> familiesMap = new ObjectMap();
        Family.all.each(f -> {
            f.members.each(u -> {
                if(familiesMap.containsKey(u)) familiesMap.get(u).add(f);
                else familiesMap.put(u,Seq.with(f));
            });
        });

        familiesMap.each((u, families) -> {
            u.stats.useCategories = true;
            StatUtils.addFamilyStats(u.stats, families);
        });
    }
}
