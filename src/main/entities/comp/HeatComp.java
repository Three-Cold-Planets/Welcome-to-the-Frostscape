package main.entities.comp;

import arc.struct.Seq;
import ent.anno.Annotations;
import main.world.systems.heat.EntityHeatState;
import main.world.systems.heat.HeatControl;
import mindustry.gen.Entityc;

@Annotations.EntityComponent
public abstract class HeatComp implements Entityc {
    public Seq<EntityHeatState> heats;


}
