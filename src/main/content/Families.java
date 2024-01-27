package main.content;

import arc.graphics.Color;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import main.graphics.ModPal;
import main.util.StatUtils;
import main.world.meta.Family;
import mindustry.content.UnitTypes;
import mindustry.ctype.UnlockableContent;
import mindustry.graphics.Pal;

public class Families {
    public static Family assault, support, specialist, hunter, swarm, gelid;

    public static void load(){
        assault = new Family("assault", Color.valueOf("ffa465"));
        support = new Family("support", Pal.heal);
        specialist = new Family("specialist", ModPal.specialist);
        hunter = new Family("hunter", ModPal.hunter);
        swarm = new Family("swarm", ModPal.swarm);
        gelid = new Family("gelid", ModPal.gelid);


        assault.members.addAll(UnitTypes.dagger, UnitTypes.mace, UnitTypes.fortress, UnitTypes.scepter, UnitTypes.reign, UnitTypes.flare, UnitTypes.horizon, UnitTypes.zenith, UnitTypes.antumbra, UnitTypes.eclipse, UnitTypes.stell, UnitTypes.locus, UnitTypes.precept, UnitTypes.vanquish, UnitTypes.conquer, UnitTypes.merui, UnitTypes.cleroi, UnitTypes.anthicus, UnitTypes.tecta, UnitTypes.collaris);
        support.members.addAll(UnitTypes.nova, UnitTypes.pulsar, UnitTypes.quasar, UnitTypes.quad, UnitTypes.oct, UnitTypes.retusa, UnitTypes.oxynoe, UnitTypes.cyerce, UnitTypes.aegires, UnitTypes.navanax);
        specialist.members.addAll(UnitTypes.crawler, UnitTypes.atrax, UnitTypes.spiroct, UnitTypes.arkyid, UnitTypes.toxopid, UnitTypes.elude, UnitTypes.avert, UnitTypes.obviate, UnitTypes.quell, UnitTypes.disrupt);

        Family.all.each(f -> f.members.add(UnitTypes.alpha));

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
