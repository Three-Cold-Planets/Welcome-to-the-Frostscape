package main.world.systems.heat;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Tile;


import static main.Frostscape.heatOverlay;
import static main.world.systems.heat.TileHeatControl.*;
import static mindustry.Vars.state;

//Example of a setup class
public class ExampleHeatSetup extends TileHeatSetup{
    TileHeatControl heat;
    @Override
    public void setupGrid(TileHeatControl heat) {
        for (int i = 0; i < heat.s; i++) {
            int x = i % heat.w,y = (int) Math.floor(i/heat.w);
            TileHeatControl.GridTile tile = heat.getTile(x, y);
            boolean solid = Vars.world.tile(x, y).solid();

            tile.floor.setStats(heat.ambientTemperature * defaultFloor.specificHeatCapacity, 4, defaultBlock);
            tile.floor.enabled = true;
            tile.block.setStats(heat.ambientTemperature * defaultBlock.specificHeatCapacity, 20, defaultBlock);
            if(solid){
                tile.block.enabled = true;
                tile.solid = true;
            }
            tile.air.setStats(heat.ambientTemperature, 1, defaultAir);
            tile.air.enabled = true;
        }
        Log.info("Grid finalized!");
    }

    @Override
    public void update(TileHeatControl heat) {
        Tile current = Vars.player.tileOn();
        if(current == null) return;
        GridTile tile = heat.getTile(current.x, current.y);
        if(tile != null) {
            tile.top().flow += 750;
        }
        Log.info("Engine go ", current.x, current.y);
    }

    @Override
    public void initialize(TileHeatControl heat) {

        this.heat = heat;

        Events.run(EventType.Trigger.update, () -> {
            if (state.isGame() && !state.isPaused()) {
                    if (heat.heatThread == null) {
                        heat.setupThread();
                    }
                    heat.heatThread.updateTime(Time.delta * 0.25f);
                }
        });

        Events.on(EventType.TileChangeEvent.class, event -> {
            if(heat.gridLoaded){
                if(event.tile.build != null){
                    updateBuildTerrain(event.tile.build);
                }else{
                    updateTerrain(event.tile.x, event.tile.y);
                }
            }
        });

        //on tile removed
        Events.on(EventType.TilePreChangeEvent.class, event -> {
            if(heat.gridLoaded){
                if(event.tile.build != null){
                    updateBuildTerrain(event.tile.build);
                }else{
                    updateTerrain(event.tile.x, event.tile.y);
                }
            }
        });

        Events.run(EventType.Trigger.draw, () -> {
            if(state.isGame() && heat.gridLoaded) Draw.draw(Layer.power + 1, heatOverlay::draw);
        });
    }

    public void updateTerrain(int x, int y){
        boolean solid = Vars.world.tile(x, y).solid();
        GridTile tile = heat.getTile(x, y);
        tile.solid = solid;
        tile.block.enabled = solid;
    };

    public void updateBuildTerrain(Building b){
        if(b.block.size == 1){
            updateTerrain(b.tile.x, b.tile.y);
        }else{
            int offset = (b.block.size - 1) / 2;
            for(int y = b.tile.y - offset; y < b.tile.y - offset + b.block.size; y++){
                for(int x = b.tile.x - offset; x < b.tile.x - offset + b.block.size; x++){
                    updateTerrain(x, y);
                }
            }
        }
    };
}
