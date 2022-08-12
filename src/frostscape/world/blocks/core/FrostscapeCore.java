package frostscape.world.blocks.core;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.UnitType;
import mindustry.world.blocks.storage.CoreBlock;

public class FrostscapeCore extends CoreBlock {
    protected static boolean canSpawn = false, building = false;

    public float warmupSpeed = 0.12f, warmDownSpeed = 0.08f;
    public float constructTime = 150;
    public float targetScale = Scl.scl(1);
    public float cameraMoveSpeed = 0.01f;
    public TextureRegion mountRegion;

    public Seq<UnitType> unitTypes = Seq.with();

    public Seq<UnitType> playerUnitTypes(){
        return unitTypes;
    };

    public FrostscapeCore(String name) {
        super(name);
        config(Byte[].class, (build, b) -> {
            FrostscapeCoreBuild core = ((FrostscapeCoreBuild) build);
            for(byte id : b){
                Player spawn = Groups.player.getByID(id);
                if(spawn != null) {
                    if(!core.que.contains(spawn)) core.que.add(spawn);
                    else {
                        switch (b[1]) {
                            case 1:
                                core.que.remove(spawn);
                            case 2:
                                core.togglePause(spawn);
                        }
                    }
                };
            }
        });
        config(Integer.class, (build, b) -> {
            FrostscapeCoreBuild core = ((FrostscapeCoreBuild) build);
            core.setType(Vars.content.units().get(b));
        });
    }


    public class FrostscapeCoreBuild extends CoreBuild{
        public float warmup = 0;
        public Seq<Player> que = Seq.with();
        public Vec2 constructPos = new Vec2(0, 16);
        public float progress = 0;
        //used for saving
        public int typeId;

        public IntSeq paused = IntSeq.with();

        public UnitType type = UnitTypes.gamma;

        public void setType(UnitType type){
            this.type = type;
            typeId = type.id;
        }

        public void respawn(byte playerId, byte interrupt){
            configure(new Byte[]{playerId, interrupt});
        }

        public void pause(byte playerId){
            configure(new Byte[]{playerId, 2});
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
        
        @Override
        public void updateTile(){
            super.updateTile();
            building = false;
            if(que.size > 0 && type != null) {
                building = true;
                progress += Time.delta;
                constructPos.set(x, y).add(0, size * 16 + 8);
                Player p = que.get(0);
                canSpawn = true;
                if(paused.contains(p.id)){
                    canSpawn = false;
                    if(que.size > 1) {
                        que.remove(p);
                        que.add(p);
                    }
                }
                if(false && p == Vars.player){
                    Vars.renderer.setScale(targetScale);
                    Core.camera.position.lerp(x, y, Time.delta * cameraMoveSpeed);
                }
                if(progress >= constructTime && canSpawn) {
                    progress = 0;
                    Unit coreUnit = type.spawn(team, constructPos.x, constructPos.y);
                    coreUnit.spawnedByCore = true;
                    que.remove(0);
                    Call.unitControl(p, coreUnit);
                }
            }
            warmup = Mathf.lerpDelta(warmup, building ? 1 : 0, building ? warmupSpeed : warmDownSpeed);
        }

        @Override
        public float warmup() {
            return warmup;
        }

        public float progressf(){
            return progress/constructTime;
        }

        public void requestSpawn(Player player){
            configure(new Byte[]{(byte) player.id, 0});
        }
    }
}