package frostscape.world.upgrades;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.type.upgrade.Upgrade;
import mindustry.game.Team;
import mindustry.io.SaveFileReader.CustomChunk;
import mindustry.io.SaveVersion;

import java.io.*;

public class UpgradeHandler implements CustomChunk {

    public static Seq<Upgrade> upgrades = new Seq<>();
    public static ObjectMap<Team, Seq<Upgrade>> teamMap = new ObjectMap<>();

    public UpgradeHandler(){
        SaveVersion.addCustomChunk("frostscape-UH", this);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Writes write = new Writes(dataOutput);
        write.i(teamMap.size);
        teamMap.each((team, list) -> {
            write.i(team.id);
            write.i(list.size);
            list.each(upgrade -> write.str(upgrade.name));
        });
    }

    @Override
    public void read(DataInput dataInput) throws IOException {
        teamMap.clear();

        Reads read = new Reads(dataInput);
        int size = read.i();
        for (int i = 0; i < size; i++) {
            int team = read.i();
            int listSize = read.i();
            Seq<Upgrade> upgradeSeq = new Seq<>();

            for (int j = 0; j < listSize; j++) {
                String name = read.str();
                Upgrade upgrade = upgrades.find(u -> u.name.equals(name));
                if(upgrade != null) upgradeSeq.add(upgrade);
            }
            teamMap.put(Team.get(team), upgradeSeq);
        }
    }

    @Override
    public boolean shouldWrite() {
        return !teamMap.isEmpty();
    }
}
