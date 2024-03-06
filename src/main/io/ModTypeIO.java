package main.io;

import arc.util.io.Reads;
import arc.util.io.Writes;
import ent.anno.Annotations;
import main.world.systems.heat.EntityHeatState;
import main.world.systems.heat.HeatControl;

@Annotations.TypeIOHandler
public class ModTypeIO {

    public static EntityHeatState readHeat(Reads read){
        EntityHeatState state = new EntityHeatState();
        state.temperature = read.f();
        state.mass = read.f();
        return state;
    }

    public static void writeHeat(Writes write, EntityHeatState state){
        write.f(state.temperature);
        write.f(state.mass);
    }
}
