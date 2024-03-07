package main.io;

import arc.util.io.Reads;
import arc.util.io.Writes;
import ent.anno.Annotations;
import main.world.module.HeatModule;
import main.world.systems.heat.EntityHeatState;
import main.world.systems.heat.HeatControl;

@Annotations.TypeIOHandler
public class ModTypeIO {

    public static HeatModule readHeat(Reads read){
        HeatModule state = new HeatModule();
        state.read(read);
        return state;
    }

    public static void writeHeat(Writes write, HeatModule state){
        state.write(write);
    }
}
