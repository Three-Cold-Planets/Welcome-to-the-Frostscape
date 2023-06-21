package main.world.systems.upgrades;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.io.SaveFileReader.CustomChunk;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class UpgradeHandler implements CustomChunk{

    public static Seq<Upgrade> upgrades = new Seq<Upgrade>();

    public static UpgradeHandler instance;

    public static UpgradeHandler get(){
        if(instance == null) return instance = new UpgradeHandler();
        return instance;
    }

    //Makes upgrades instant
    public boolean instantUpgrades = true;

    @Override
    public void write(DataOutput stream) throws IOException {
        Writes write = new Writes(stream);
        write.bool(instantUpgrades);
    }

    @Override
    public void read(DataInput stream) throws IOException {
        Reads reads = new Reads(stream);
        instantUpgrades = reads.bool();
    }
}
