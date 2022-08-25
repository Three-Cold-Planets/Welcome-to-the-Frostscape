package frostscape.world.research;

import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.io.SaveFileReader.CustomChunk;
import mindustry.io.SaveVersion;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ResearchHandler implements CustomChunk {

    public static Seq<ResearchData> researches = new Seq<ResearchData>();
    private static Seq<ResearchData> s1 = new Seq<ResearchData>();
    public static Seq<ResearchType> types = new Seq<ResearchType>();
    public static IntMap<Seq<ResearchData>> teamMap = new IntMap<Seq<ResearchData>>();

    public ResearchHandler(){
        SaveVersion.addCustomChunk("frostscape-RH", this);
    }

    public void unlock(Team team, ResearchType type){
        getData(team, type).unlocked = true;
    }

    public ResearchData getData(Team team, ResearchType type){
        s1 = teamMap.get(team.id);
        if(s1 == null) {
            s1 = new Seq<>();
            teamMap.put(team.id, s1);
            ResearchData data = new ResearchData(type, false);
            s1.add(data);
            return data;
        }
        ResearchData data = s1.find(udata -> udata.type == type);
        if(data == null){
            data = new ResearchData(type, false);
            s1.add(data);
        }
        return data;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Writes write = new Writes(dataOutput);
        write.i(teamMap.size);
        for (IntMap.Entry<Seq<ResearchData>> next: teamMap.entries()){

            write.i(next.key);
            write.i(next.value.size);
            next.value.each(data -> {
                write.str(data.type.name);
                write.bool(data.unlocked);
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
            Seq<ResearchData> upgradeSeq = new Seq<>();

            for (int j = 0; j < listSize; j++) {
                String name = read.str();
                boolean unlocked = read.bool();
                ResearchData data = new ResearchData(types.find(u -> u.name.equals(name)), unlocked);
                if(data.type != null) upgradeSeq.add(data);
            }
            teamMap.put(team, upgradeSeq);
        }
    }

    @Override
    public boolean shouldWrite() {
        return !teamMap.isEmpty();
    }

    public class ResearchData{
        public ResearchType type;
        public float progress;
        public boolean unlocked = false;

        public ResearchData(ResearchType type, boolean unlocked){
            this.type = type;
            this.unlocked = unlocked;
        }
    }

    public static class ResearchType{
        public String name;
        public float researchTime;
        public ResearchType previous;
        public ResearchType(String name, float researchTime){
            this.name = Vars.content.transformName(name);
            this.researchTime = researchTime;
        }
    }
}
