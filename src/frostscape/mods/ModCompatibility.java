package frostscape.mods;

import arc.files.Fi;
import arc.files.ZipFi;
import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.Mods.ModMeta;

import static frostscape.Frostscape.VERSION;

public class ModCompatibility {
    //...What the fuck
    public void start(){
        Fi[] files = Vars.modDirectory.list();
        for (int i = 0; i < files.length; i++) {
            Fi file = files[i];

            ModMeta meta = null;

            Fi zip = file.isDirectory() ? file : new ZipFi(file);

            if(zip.list().length == 1 && zip.list()[0].isDirectory()){
                zip = zip.list()[0];
            }

            meta = Vars.mods.findMeta(zip);
            if(!(meta == null) && Float.valueOf(meta.minGameVersion) >= VERSION) return;

            ConfigMeta config = findConfig(zip);
            if(config != null) Log.info("MY TIME MACHINE WORKSS YESSSSSSSSSSSSSSSSSSS");
        }
    }

    ConfigMeta findConfig(Fi zip){
        return null;
    }

    public static class ConfigMeta{

    }
}
