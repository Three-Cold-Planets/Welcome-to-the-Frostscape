package main.mods;

import main.util.StatUtils;
import main.world.meta.stat.FrostStatCats;
import mindustry.Vars;

public class BlockCompatibility {
    public static void load(){
        Vars.content.blocks().each(b -> {
            if(b.synthetic() || b.stats.toMap().containsKey(FrostStatCats.scanning)) return;
            StatUtils.addScanningStats(b);
        });
    }
}
