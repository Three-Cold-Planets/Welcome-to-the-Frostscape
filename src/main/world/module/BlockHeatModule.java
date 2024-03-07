package main.world.module;

import arc.math.geom.Point2;
import arc.struct.Seq;
import main.world.HeatPart;
import main.world.systems.heat.HeatControl;

public class BlockHeatModule {
    //Material and mass values for all hull tiles
    public HeatControl.MaterialPreset material;
    public float mass = -1;

    //Heat parts to set up HeatState interactions in the building.
    public Seq<HeatPart> parts;

    //Note: For more dynamic interactions, do *NOT* use the index maps. Use a custom Building instead

    //Index map of how each HeatState interacts with the environment, relative to a building's bottom left corner
    public transient Point2[][] envUpdates;

    //Index map of how each HeatState interacts.
    public transient int[][] internalUpdates;
}
