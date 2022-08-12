package frostscape.world.upgrades;

import arc.struct.*;
import arc.struct.IntMap.Entry;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.type.upgrade.Upgrade;
import mindustry.game.Team;
import mindustry.io.SaveFileReader.CustomChunk;
import mindustry.io.SaveVersion;

import java.io.*;
import java.util.Iterator;

public class UpgradeHandler implements CustomChunk {

    private static Seq<UpgradeData> s1 = new Seq<UpgradeData>();
    public static Seq<Upgrade> upgrades = new Seq<Upgrade>();
    public static IntMap<Seq<UpgradeData>> teamMap = new IntMap<Seq<UpgradeData>>();

    public UpgradeHandler(){
        SaveVersion.addCustomChunk("frostscape-UH", this);
    }

    public void unlock(Team team, Upgrade upgrade){
        getData(team, upgrade).state = 1;
    }

    public UpgradeData getData(Team team, Upgrade upgrade){
        s1 = teamMap.get(team.id);
        if(s1 == null) {
            s1 = new Seq<>();
            teamMap.put(team.id, s1);
            UpgradeData data = new UpgradeData(upgrade, 0);
            s1.add(data);
            return data;
        }
        UpgradeData data = s1.find(udata -> udata.upgrade == upgrade);
        if(data == null){
            data = new UpgradeData(upgrade, 0);
            s1.add(data);
        }
        return data;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Writes write = new Writes(dataOutput);
        write.i(teamMap.size);
        for (Entry<Seq<UpgradeData>> next: teamMap.entries()){

            write.i(next.key);
            write.i(next.value.size);
            next.value.each(data -> {
                write.str(data.upgrade.name);
                write.i(data.state);
            });
        }
    }

    @Override
    public void read(DataInput dataInput) throws IOException {
        teamMap.clear();

        Reads read = new Reads(dataInput);
        int size = read.i();
        for (int i = 0; i < size; i++) {
            int team = read.i();
            int listSize = read.i();
            Seq<UpgradeData> upgradeSeq = new Seq<>();

            for (int j = 0; j < listSize; j++) {
                String name = read.str();
                int state = read.i();
                UpgradeData data = new UpgradeData(upgrades.find(u -> u.name.equals(name)), state);
                if(data.upgrade != null) upgradeSeq.add(data);
            }
            teamMap.put(team, upgradeSeq);
        }
    }

    @Override
    public boolean shouldWrite() {
        return !teamMap.isEmpty();
    }

    public class UpgradeData{
        public Upgrade upgrade;
        public int state;

        public UpgradeData(Upgrade upgrade, int state){
            this.upgrade = upgrade;
            this.state = state;
        }
    }
}
