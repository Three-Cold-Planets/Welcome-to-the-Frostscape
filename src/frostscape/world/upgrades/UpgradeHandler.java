package frostscape.world.upgrades;

import arc.struct.*;
import arc.struct.IntMap.Entry;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.type.upgrade.Upgrade;
import frostscape.type.upgrade.Upgradeable;
import frostscape.type.upgrade.UpgradeableBuilding;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.io.SaveFileReader.CustomChunk;
import mindustry.io.SaveVersion;

import java.io.*;
import java.util.Iterator;

public class UpgradeHandler implements CustomChunk{

    public static Seq<Upgrade> upgrades = new Seq<Upgrade>();

    public UpgradeHandler(){
        SaveVersion.addCustomChunk("frostscape-UH", this);
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
