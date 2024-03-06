package main.io;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import ent.anno.Annotations;
import main.world.systems.heat.TileHeatControl;
import mindustry.gen.Healthc;

@Annotations.TypeIOHandler
public class ModTypeIO {

    public static TileHeatControl.EntityHeatState readHeat(Reads read){
        TileHeatControl.EntityHeatState state = new TileHeatControl.EntityHeatState();
        state.energy = read.f();
        state.mass = read.f();
        return state;
    }

    public static void writeHeat(Writes write, TileHeatControl.EntityHeatState state){
        write.f(state.energy);
        write.f(state.mass);
    }
}
