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

    private static Seq<Upgrade> s1 = new Seq<Upgrade>();
    public static Seq<Upgrade> upgrades = new Seq<Upgrade>();
    public static IntMap<Seq<UpgradeDataHolder>> teamMap = new IntMap<Seq<UpgradeDataHolder>>();

    public UpgradeHandler(){
        SaveVersion.addCustomChunk("frostscape-UH", this);
    }

    public void unlock(Team team, Upgrade upgrade){
        s1 = teamMap.get(team.id);
        if(s1 == null) {
            s1 = new Seq<>();
            teamMap.put(team.id, new Seq<>());
            s1.add(upgrade);
        }
        s1.find()
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Writes write = new Writes(dataOutput);
        write.i(teamMap.size);
        for (Entry<Seq<UpgradeDataHolder>> next: teamMap.entries()){

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
            Seq<UpgradeDataHolder> upgradeSeq = new Seq<>();

            for (int j = 0; j < listSize; j++) {
                String name = read.str();
                int state = read.i();
                UpgradeDataHolder data = new UpgradeDataHolder(upgrades.find(u -> u.name.equals(name)), state);
                if(data.upgrade != null) upgradeSeq.add(data);
            }
            teamMap.put(team, upgradeSeq);
        }
    }

    @Override
    public boolean shouldWrite() {
        return !teamMap.isEmpty();
    }

    public class UpgradeDataHolder{
        public Upgrade upgrade;
        public int state;

        public UpgradeDataHolder(Upgrade upgrade, int state){
            this.upgrade = upgrade;
            this.state = state;
        }
    }
}
