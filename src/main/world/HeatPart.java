package main.world;


import main.world.systems.heat.HeatControl;

//Represents a single "Part" on the building
public class HeatPart{
    //The material that this part is made from
    public HeatControl.MaterialPreset preset;

    //Mass of the part. Only affects newly constructed buildings.
    public float mass;
}