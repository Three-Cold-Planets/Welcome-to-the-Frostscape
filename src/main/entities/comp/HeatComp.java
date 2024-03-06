package main.entities.comp;

import arc.struct.Seq;
import arc.util.Log;
import ent.anno.Annotations;
import main.world.systems.heat.TileHeatControl;
import mindustry.gen.Buildingc;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;

@Annotations.EntityComponent
public abstract class HeatComp implements Entityc {
    public Seq<TileHeatControl.EntityHeatState> heats;


}
