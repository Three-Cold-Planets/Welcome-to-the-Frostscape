package main.world.environment;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import main.world.blocks.environment.DataBlock;
import main.world.blocks.environment.EnvironmentData;
import mindustry.io.SaveFileReader.CustomChunk;
import mindustry.world.Tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloorDataHandler implements CustomChunk {

    public FloorDataHandler(){

    }

    public static Seq<DataBlock> blocks = new Seq<>();

    public ObjectMap<Tile, DataHolders> data = new ObjectMap<>();

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Writes write = new Writes(dataOutput);
        write.i(data.size);
        data.each((tile, holders) -> {
            if(holders.block == null) write.bool(false);
            else {
                write.bool(true);
                holders.block.write(holders.blockd, write);
            }
            if(holders.overlay == null) write.bool(false);
            else {
                write.bool(true);
                holders.overlay.write(holders.overlayd, write);
            }
            if(holders.floor == null) write.bool(false);
            else {
                write.bool(true);
                holders.floor.write(holders.floord, write);
            }
        });
    }

    @Override
    public void read(DataInput dataInput) throws IOException {
        Reads read = new Reads(dataInput);
        int length = read.i();
        for (int i = 0; i < length; i++) {

        }
    }

    public class DataHolders{
        public EnvironmentData blockd, overlayd, floord;
        public DataBlock block, overlay, floor;
    }
}
