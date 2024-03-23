package main.entities.comp;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import ent.anno.Annotations;
import main.world.module.HeatModule;
import main.world.systems.heat.EntityHeatState;
import main.world.systems.heat.HeatControl;
import mindustry.gen.Buildingc;
import mindustry.gen.Entityc;

@Annotations.EntityComponent
public abstract class HeatComp implements Buildingc {
    public HeatModule heat;
}
