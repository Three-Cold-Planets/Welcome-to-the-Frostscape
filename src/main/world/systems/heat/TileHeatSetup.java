package main.world.systems.heat;

public abstract class TileHeatSetup {
    public abstract void setupGrid(TileHeatControl heat);
    public abstract void update(TileHeatControl heat);

    //Called to start the simulation
    public abstract void initialize(TileHeatControl heat);
}
