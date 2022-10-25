package frostscape.world.blocks.core;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import frostscape.Frostscape;
import frostscape.world.UpgradesType;
import frostscape.world.research.ResearchHandler.ResearchType;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.world.Tile;

import java.nio.ByteBuffer;

public class FrostscapeCore extends BaseCore implements UpgradesType {
    //Bytebuffer used in sending player data as an array of bytes
    static final ByteBuffer b = ByteBuffer.wrap(new byte[4]);
    protected static boolean canSpawn = false, building = false;
    public float warmupSpeed = 0.12f, warmDownSpeed = 0.08f;
    public float constructTime = 150;
    public float targetScale = Scl.scl(1);
    public float cameraMoveSpeed = 0.01f;

    public Seq<UnitEntry> units = Seq.with();
    public UnitEntry defaultEntry;

    public FrostscapeCore(String name) {
        super(name);
        config(Byte[].class, (build, bytes) -> {
            FrostscapeCoreBuild core = ((FrostscapeCoreBuild) build);
            int id = ByteBuffer.wrap(new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]}, 0, 4).getInt();
            Player spawn = Groups.player.getByID(id);
            if(spawn != null) {
                if(!core.que.contains(spawn)) core.que.add(spawn);
                else {
                    switch (bytes[4]) {
                        case 1:
                            core.que.remove(spawn);
                        case 2:
                            core.togglePause(spawn);
                    }
                }
            };
        });
    }


    public class FrostscapeCoreBuild extends BaseCoreBuild {

        public float warmup = 0;
        public Seq<Player> que = Seq.with();
        public Vec2 constructPos = new Vec2(0, 16);
        public float progress = 0;
        //used for saving
        public int current = -1;

        public IntSeq paused = IntSeq.with();

        public UnitEntry entry = null;

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            setEntry(defaultEntry);
            return super.init(tile, team, shouldAdd, rotation);
        }


        @Override
        public void updateTile(){
            super.updateTile();
            warmup = Mathf.lerpDelta(warmup, building ? 1 : 0, building ? warmupSpeed : warmDownSpeed);
            updateSpawning();
        }

        public void updateSpawning(){
            building = false;
            if(!entry.isValid(team)) entry = units.find(e -> e.isValid(team));
            if(que.size > 0 && entry != null) {
                building = true;
                progress += Time.delta * warmup;
                constructPos.set(x, y).add(0, size * 16 + 8);
                Player p = que.get(0);
                canSpawn = true;
                if (!(p.unit() == null || p.unit().isNull())){
                    que.remove(p);
                    return;
                }
                if(paused.contains(p.id)){
                    if(que.size > 1) {
                        que.remove(p);
                        que.add(p);
                    }
                    return;
                }
                //No.
                if(false && p == Vars.player){
                    Vars.renderer.setScale(targetScale);
                    Core.camera.position.lerp(x, y, Time.delta * cameraMoveSpeed);
                }
                if(progress >= constructTime && canSpawn) {
                    progress = 0;
                    Unit coreUnit = entry.type.spawn(team, constructPos.x, constructPos.y);
                    coreUnit.rotation += 90;
                    coreUnit.spawnedByCore = true;
                    que.remove(0);
                    Call.unitControl(p, coreUnit);
                }
            }
        }

        @Override
        public float warmup() {
            return warmup;
        }

        public float progressf(){
            return progress/constructTime;
        }

        public void setEntry(int entry){
            setEntry(units.get(entry));
        }

        public void setEntry(UnitEntry entry){
            this.entry = entry;
            if(entry == null) {
                current = -1;
                return;
            }
            current = units.indexOf(entry);
        }

        public void respawn(byte playerId, byte interrupt){
            configure(new Byte[]{playerId, interrupt});
        }

        public void pause(byte playerId){

            b.clear();
            b.put(playerId);
            b.hasArray();
            byte[] out = b.array();

            configure(new Byte[]{out[0], out[1], out[2], out[3], 2});
        }

        public void togglePause(Player player){
            que.remove(player);
            if(paused.contains(player.id)) {
                paused.removeValue(player.id);
                if(!que.contains(player)) que.add(player);
                return;
            }
            paused.add(player.id);
            if(que.contains(player)) que.remove(player);
        }

        public void requestSpawn(Player player){

            b.clear();
            b.putInt(player.id);
            b.hasArray();
            byte[] out = b.array();

            configure(new Byte[]{out[0], out[1], out[2], out[3], 3});
        }
    }

    public class UnitEntry{
        public Boolf<Team> locked;
        public Boolf<Team> unlocked;
        public float constructionTime;
        public UnitType type;

        public UnitEntry(Boolf<Team> unlockedBy, Boolf<Team> lockedBy, float constructionTime, UnitType type){
            this.unlocked = unlockedBy;
            this.locked = lockedBy;
            this.constructionTime = constructionTime;
            this.type = type;
        }

        public boolean isValid(Team team){
            return (locked == null || !locked.get(team)) && (unlocked == null || unlocked.get(team));
        }
    }
    public class ResearchedLockedCond implements Boolf<Team>{
        public ResearchType research;
        public ResearchedLockedCond(ResearchType research){
            this.research = research;
        }
        @Override
        public boolean get(Team team) {
            return Frostscape.research.getData(team, research).unlocked;
        }
    }
}