package main.world.module;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import main.world.systems.heat.EntityHeatState;
import main.world.systems.heat.HeatControl;
import mindustry.gen.UnitEntity;
import mindustry.io.TypeIO;
import mindustry.world.modules.BlockModule;
import mindustry.world.modules.ItemModule;

import static main.world.systems.heat.HeatControl.ambientTemperature;

public class HeatModule extends BlockModule {

    public EntityHeatState base;

    public EntityHeatState[] states;

    public static void setup(HeatModule module, BlockHeatModule blockModule){
        module.base = new EntityHeatState(){{
            mass = blockModule.mass;
            material = blockModule.material;
            temperature = ambientTemperature;
        }};

        EntityHeatState[] states = new EntityHeatState[blockModule.entries.length];

        for (int i = 0; i < blockModule.entries.length; i++) {
            BlockHeatModule.PartEntry entry = blockModule.entries[i];
            EntityHeatState state = new EntityHeatState();
            states[i] = state;
            state.mass = entry.mass;
            state.material = entry.material;
            state.temperature = ambientTemperature;
        }

        module.states = states;
        Log.info("setup states!");
        Log.info(module.states);
    }

    public void finalizeEnergy(){
        base.addEnergy(base.flow);
        base.lastFlow = base.flow;
        base.flow = 0;
        for (EntityHeatState state : states) {
            state.addEnergy(state.flow);
            state.lastFlow = base.flow;
            state.flow = 0;
        }
    }

    @Override
    public void write(Writes write) {
        /*
        write.i(states.length);
        for (EntityHeatState state : states) {
            write.f(state.temperature);
            write.f(state.mass);
        }

         */
    }

    @Override
    public void read(Reads read, boolean legacy) {
        /*
        int length = read.i();
        states = new EntityHeatState[]{};
        for (int i = 0; i < length; i++) {
            EntityHeatState state = new EntityHeatState();
            state.temperature = read.f();
            state.mass = read.f();
            states[i] = state;
        }

         */
    }
}
