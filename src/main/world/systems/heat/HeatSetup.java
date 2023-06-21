package main.world.systems.heat;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.graphics.Layer;

import static mindustry.Vars.state;

//Example of a setup class
public class HeatSetup extends TileHeatSetup{
    TileHeatControl heat;
    @Override
    void setupGrid(TileHeatControl heat) {
        for (int i = 0; i < heat.s; i++) {
            boolean solid = Vars.world.tile(i % heat.w, (int) Math.floor(i/heat.w)).solid();
            heat.setTileValues(i, heat.ambientTemperature * (solid ? 10 * TileHeatControl.defaultBlock.specificHeatCapacity : 1 * TileHeatControl.defaultFloor.specificHeatCapacity), solid ? 10 : 1, solid ? TileHeatControl.defaultBlock : TileHeatControl.defaultFloor);
        }
    }

    @Override
    void update(TileHeatControl heat) {
        Tmp.v1.set(Vars.player);
        int index =(int) (Math.floor(Tmp.v1.x/8) + Math.floor(Tmp.v1.y/8) * heat.w);
        if(index >= heat.s || index < 0) return;
        heat.energyValues[index] += 75;
    }

    @Override
    void initialize(TileHeatControl heat) {

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

        /*
        Events.run(EventType.Trigger.draw, () -> {
            if(state.isGame() && heat.gridLoaded) Draw.draw(Layer.power + 1, heatOverlay::draw);
        });

         */
    }

    public void updateTerrain(int x, int y){
        boolean solid = Vars.world.tile(x, y).solid();
        int index = x + y * heat.w;
        heat.massValues[index] = solid ? 10 : 1;
        heat.tilePropertyAssociations.put(index, solid ? TileHeatControl.defaultBlock : TileHeatControl.defaultFloor);
    };

    public void updateBuildTerrain(Building b){
        boolean solid = b.tile.solid();
        int index = b.tile.x + b.tile.y * heat.w;
        heat.massValues[index] = solid ? 10 : 1;
        heat.tilePropertyAssociations.put(index, solid ? TileHeatControl.defaultBlock : TileHeatControl.defaultFloor);
    };
}
