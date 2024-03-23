package main.world.module;

import arc.math.geom.Point2;
import arc.struct.IntFloatMap;
import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import main.world.BaseBuilding;
import main.world.HeatPart;
import main.world.systems.heat.HeatControl;
import main.world.systems.heat.HeatControl.MaterialPreset;
import mindustry.logic.TileLayer;
import mindustry.world.meta.Env;

public class BlockHeatModule {

    public float overheatTemperature, overheatDamage;

    //Material and mass values for all hull tiles
    public MaterialPreset material;
    public float mass = -1;

    public PartEntry[] entries = new PartEntry[]{};

    public static class PartEntry{
        public float mass;
        public MaterialPreset material;

        public IntFloatMap partFlowmap = new IntFloatMap();

        public Seq<ExchangeArea> tileFlowmap = new Seq<>();
    };

    public static class ExchangeArea{
        //Offsets from the block's tile
        public byte x, y,
        //Offset to the
        width, height;

        public float rate;

        public int layerBitmask;

        public boolean floorEnabled(){
            return (layerBitmask & 1) != 0;
        }

        public boolean blockEnabled(){
            return ((layerBitmask >> 1) & 1) != 0;
        }

        public boolean airEnabled(){
            return ((layerBitmask >> 2) & 1) != 0;
        }
    }
}
