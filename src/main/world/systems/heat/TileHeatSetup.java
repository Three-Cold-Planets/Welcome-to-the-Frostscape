package main.world.systems.heat;

public abstract class TileHeatSetup {
    abstract void setupGrid(TileHeatControl heat);
    abstract void update(TileHeatControl heat);

    //Called to start the simulation
    abstract void initialize(TileHeatControl heat);
}
