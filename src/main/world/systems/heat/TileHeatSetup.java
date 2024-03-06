package main.world.systems.heat;

public interface TileHeatSetup {
    void setupGrid(HeatControl heat);
    void update(HeatControl heat);

    //Called to start the simulation
    void initialize(HeatControl heat);
}
