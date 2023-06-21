package main.world.environment;

import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import main.world.blocks.environment.DataBlock;
import main.world.blocks.environment.EnvironmentData;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.graphics.BlockRenderer;
import mindustry.io.SaveFileReader.CustomChunk;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor.UpdateRenderState;

import java.io.*;
import java.lang.reflect.Field;

public class FloorDataHandler implements CustomChunk {

    public FloorDataHandler(){
        Events.run(ClientLoadEvent.class, () -> {
            Field floors = null;
            try {
                floors = BlockRenderer.class.getDeclaredField("updateFloors");
            } catch (NoSuchFieldException e) {
                Log.err(e);
            }
            floors.setAccessible(true);
            try {
                updateFloors = (Seq<UpdateRenderState>) floors.get(Vars.renderer.blocks);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public static Seq<UpdateRenderState> updateFloors;
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
