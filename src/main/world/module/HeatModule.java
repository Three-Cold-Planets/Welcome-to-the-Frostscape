package main.world.module;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import main.world.systems.heat.EntityHeatState;
import main.world.systems.heat.HeatControl;
import mindustry.io.TypeIO;
import mindustry.world.modules.BlockModule;
import mindustry.world.modules.ItemModule;

public class HeatModule extends BlockModule {

    public Seq<EntityHeatState> heat = new Seq<>();

    @Override
    public void write(Writes write) {
        write.i(heat.size);
        heat.each(state -> {
            write.f(state.temperature);
            write.f(state.mass);
        });
    }

    @Override
    public void read(Reads read, boolean legacy) {
        heat.clear();
        int size = read.i();
        for (int i = 0; i < size; i++) {
            EntityHeatState state = new EntityHeatState();
            state.temperature = read.f();
            state.mass = read.f();
            heat.add(state);
        }
    }
}
