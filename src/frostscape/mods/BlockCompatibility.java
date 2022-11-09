package frostscape.mods;

import arc.util.Log;
import frostscape.util.StatUtils;
import frostscape.world.meta.stat.FrostStatCats;
import frostscape.world.meta.stat.FrostStats;
import mindustry.Vars;
import mindustry.world.Block;
import mindustry.world.meta.StatCat;

public class BlockCompatibility {
    public static void load(){
        Vars.content.blocks().each(b -> {
            if(b.synthetic() || b.stats.toMap().containsKey(FrostStatCats.scanning)) return;
            StatUtils.addScanningStats(b);
        });
    }
}
