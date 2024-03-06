package main.world.systems.heat;

import arc.Core;
import arc.math.Mathf;
import arc.struct.*;
import arc.util.Log;
import main.world.systems.heat.TileHeatSetup;
import mindustry.gen.Call;
import mindustry.gen.Icon;
import mindustry.io.SaveFileReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

//Class which stores and controls the heat in both blocks and floors and their flow.
public class TileHeatControl implements SaveFileReader.CustomChunk {

    static TileHeatControl instance;

    public static TileHeatControl get() {
        if (instance == null) instance = new TileHeatControl();
        return instance;
    }

    public TileHeatControl(){

    }

    public static TileHeatSetup setup;

    static HeatState tmpS1, tmpS2;

    static GridTile tmpT1, tmpT2, tmpT3;

    //Ambient Temperature in celsius
    public static float ambientTemperature = 303.15f,
    //How conductive the atmosphere is
    envTempChange = 0.3f;

    public static MaterialPreset tmpMP1 = new MaterialPreset(), tmpMP2 = new MaterialPreset();

    public static MaterialPreset defaultFloor = new MaterialPreset(0.12f, 1),
            defaultBlock = new MaterialPreset(0.07f, 3),
            defaultAir = new MaterialPreset(0.4f, 0.6f),
            vacuum = new MaterialPreset(0, 0);

    public static float simulationSpeed = 1;
    public static boolean enabled;

    public boolean gridLoaded = false;

    private static final ArrayList<MaterialPreset> presetList = new ArrayList<>();


    //List storing all grid state chunks
    public static Seq<Chunk> gridChunks;

    //List of all grid states
    public static Seq<GridTile> gridTiles;

    //Sequence storing all non-grid tile states.
    public static Seq<HeatState> entityStates;

    public HeatRunnerThread heatThread;

    //Width, height and size of the world
    public int w, h, s,
    //Width and height of the chunk sections
    chunkW, chunkH;

    //Chunk size. DO NOT CHANGE UNLESS YOU KNOW WHAT YOU'RE DOING
    public int chunkSize = 16;

    public void setupThread(){
        heatThread = new TileHeatControl.HeatRunnerThread();
        heatThread.setPriority(Thread.NORM_PRIORITY - 1);
        heatThread.setDaemon(true);
        heatThread.start();
        Log.info("Started Heat Threat");
    }

    public void start(int width, int height){
        Log.info("Starting Heat!");
        initializeValues();
        createGrid(width, height);
        setup.setupGrid(this);
        gridLoaded = true;
    }

    public GridTile getTile(int x, int y){
        if(x < 0 || y < 0 || x >= w || y >= h) return null;
        return gridTiles.get(x + y * w);
    }

    @Deprecated
    public GridTile getTile(int index){
        return gridTiles.get(index);
    }
    public void createGrid(int w, int h){
        this.w = w;
        this.h = h;

        s = w * h;
        gridChunks = new Seq<>(true, s);
        gridTiles = new Seq<>(true);
        entityStates = new Seq<>(false);

        //How many chunks can be fit horizontally and vertically
        chunkW = Mathf.ceil(((float) w)/chunkSize);
        chunkH = Mathf.ceil(((float) h)/chunkSize);

        //Set up chunks. Some will bee less than optimally sized, due to hitting the map border.
        for (int y = 0; y < chunkH; y++) {
            for (int x = 0; x < chunkW; x++) {

                int width = x == chunkW - 1 ? chunkSize - Mathf.mod(w, chunkSize) : chunkSize;
                int height = y == chunkH - 1 ? chunkSize - Mathf.mod(w, chunkSize) : chunkSize;

                Chunk current = new Chunk(x * chunkSize, y * chunkSize, width, height, true);
                gridChunks.add(current);
            }
        }
        Log.info("Chunks created!");

        //Setup chunks + neighbours.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                //Get current chunk
                int index = x/chunkSize + y/chunkSize * chunkW;
                Chunk current = gridChunks.get(index);
                tmpT1 = new GridTile(current, x, y);
                gridTiles.add(tmpT1);
                current.tiles.add(tmpT1);
            }
        }

        //Setup neighbours
        for (int i = 0; i < s; i++) {
            GridTile current = gridTiles.get(i);

            tmpT1 = getTile(current.x - 1, current.y);
            if(tmpT1 != null) {
                current.adjacent.add(tmpT1);
                tmpT1.adjacent.add(current);
            }

            tmpT1 = getTile(current.x, current.y - 1);
            if(tmpT1 == null) continue;
            current.adjacent.add(tmpT1);
            tmpT1.adjacent.add(current);
        }
        Log.info("Grid created!");
    }

    public void tick(){
        //Log.info("Ticking");
        updateFlow();
        setup.update(this);
        finalizeEnergy();
    }

    //Note that ambient heat is factored in after flow calculations
    public void updateFlow()
    {
        gridChunks.each(Chunk::update);
        //entityStates.each(GridHeatState::updateState);
    }

    public void finalizeEnergy()
    {
        gridTiles.each(GridTile::finalizeEnergy);
        //entityStates.each(GridHeatState::finalizeEnergy);
    }

    //Note that this ignores surface area. That logic should be implemented in the object calling this
    public static float calculateFlow(float mass1, float mass2, float temp1, float temp2, MaterialPreset preset1, MaterialPreset preset2){

        //Debug.Log("Going from tile: " + tile1 + " to " + tile2);

        //Don't transfer heat if either masses are below 1 g. Obviously this should never happen but eh
        if (mass1 + mass2 < 2) {
            return 0;
        }

        float tempretureDif = temp2 - temp1;

        //Don't bother calculating if tempreture difference is less than 1 celcius
        if (Math.abs(tempretureDif) < 1f) {
            return 0;
        }

        float geomThermalConductivity = Mathf.sqrt(preset1.thermalConductivity * preset2.thermalConductivity);
        float flowAmount = geomThermalConductivity * tempretureDif * simulationSpeed;

        //Don't bother using if energy flow is less than 0.1 units
        if (Math.abs(flowAmount) < 0.1f) return 0;

        //Cap change of energy to 1/5 of the temp difference changed per tick
        float maxTempDif = Math.min(Math.abs(tempretureDif * mass1 * preset1.specificHeatCapacity),
                Math.abs(tempretureDif * mass2 * preset2.specificHeatCapacity))/5;

        return Mathf.clamp(flowAmount, -maxTempDif, maxTempDif);
    }

    //Note that this assumes all blocks of air are uniformly sized and in constant contact with the atmosphere for the full duration between heat ticks.
    // Accounting for this would add unnecessary bloat to this relatively simple method.
    public static float calculateFlowAtmosphere(float mass, float temp, MaterialPreset preset){
        
        float tempretureDif = ambientTemperature - temp;
        float geomThermalConductivity = Mathf.sqrt(preset.thermalConductivity * envTempChange);
        float flowAmount = geomThermalConductivity * tempretureDif * simulationSpeed;
        
        //Cap change of energy to 1/5 of the temp difference changed per tick based only on the mass interacting with the atmosphere.
        float maxTempDif = Math.abs(tempretureDif/5 * mass * preset.specificHeatCapacity);

        
        return Mathf.clamp(flowAmount, -maxTempDif, maxTempDif);
    }
    
    public void initializeValues(){

    }
    public static float kelvins(HeatState state){
        return kelvins(state.energy, state.mass, state.material.specificHeatCapacity);
    }

    public static float celsius(HeatState state){
        return kelvins(state) - 273.15f;
    }
    public static float kelvins(float energy, float mass, float SPH){
        return energy/(mass*SPH);
    }

    public static float celsius(float energy, float mass, float SPH){
        return kelvins(energy, mass, SPH) - 273.15f;
    }

    /**
     * Handles an exchange of heat between two grid tiles, of which the first parameter is the origin.
     */
    public static void handleExchange(HeatState state1, HeatState state2){
        float flow = calculateFlow(state1.mass, state2.mass, kelvins(state1), kelvins(state2), state1.material, state2.material);

        //Flow calculations are reused for both tiles, keeping with conservation of energy, and avoiding double ups on calculation.
        state1.flow += flow;
        state2.flow -= flow;
    }

    @Override
    public void write(DataOutput stream) throws IOException {
        /*
        stream.writeBoolean(enabled);
        if(!enabled) return;
        for (int i = 0; i < s; i++) {
            tmpS1 = gridStates.get(s);
            stream.writeFloat(tmpS1.energy);
            stream.writeFloat(tmpS1.mass);
        }
         */
    }

    public void writeState(DataOutput stream, GridHeatState state){
    }

    @Override
    public void read(DataInput stream) throws IOException {
        /*
        enabled = stream.readBoolean();
        if(!enabled) return;
        for (int i = 0; i < s * 2; i++) {
            tmpS1.energy = stream.readFloat();
            tmpS1.mass = stream.readFloat();
        }
         */
    }

    public static abstract class HeatState{
        public HeatState(){
            enabled = false;
            material = vacuum;
        }
        public float energy, mass, flow, lastFlow;

        public MaterialPreset material;

        public abstract void finalizeEnergy();

        public void setStats(float energy, float mass, MaterialPreset material){
            this.energy = energy;
            this.mass = mass;
            this.material = material;
        }
    }

    public static class EntityHeatState extends HeatState{

        @Override
        public void finalizeEnergy() {
            energy += flow;
            //For debugging purposes
            lastFlow = flow;
            flow = 0;
        }
    }

    public static class GridHeatState extends HeatState {

        public GridHeatState(){
            super();
        }

        //If false, flow going from and to this state will not be calculated, however other functionality is untouched.
        public boolean enabled;
        public Chunk chunk;

        @Override
        public void finalizeEnergy(){
            energy += flow;
            chunk.totalFlow += flow;
            //For debugging purposes
            lastFlow = flow;
            flow = 0;
        }
    }

    /**
     * A class that stores energy, mass and energy. Position data + size handled by the chunk indexes
     * NOTE THAT WHEN ADDING TO THE STATE'S ENERGY, USE FLOW INSTEAD OF ENERGY.
     */

    public static class GridTile {

        public int x, y;
        /**
         * Having enabled be on both GridTile and GridHeatState allows more flexible arrangements of tiles
         */
        public boolean enabled;

        public boolean shielded;

        /**
         * If the tile has a solid block on it. If false, the floor exchanges temperature directly with the air
         * Note that having a disabled block will prevent floor and air states from exchanging
         */
        public boolean solid;

        public GridHeatState floor;
        public GridHeatState block;
        public GridHeatState air;
        public Chunk owner;

        //All cardinally adjacent tiles.
        public Seq<GridTile> adjacent,

        //Updates queued to happen. Removed when another tile on the list updates this. No updates are queued for tiles in disabled chunks.
        updates;

        public GridTile(Chunk owner, int x, int y){
            this.x = x;
            this.y = y;
            enabled = true;
            adjacent = new Seq();
            updates = new Seq();

            floor = new GridHeatState();
            block = new GridHeatState();
            air = new GridHeatState();
            this.owner = floor.chunk = block.chunk = air.chunk = owner;
        }

        public GridHeatState top(){
            return solid ? block : floor;
        }
        public void finalizeEnergy(){
            floor.finalizeEnergy();
            block.finalizeEnergy();
            air.finalizeEnergy();
            updates.set(adjacent);
        }
    }

    public static class MaterialPreset{
        public float
        //How conductive the material is. More Thermal Conductivity means more flow of heat between the tile and it's neighbours.
        // Conductivity works on averages, so something with almost no conductivity next to something with high conductivity will still conduct heat.
        thermalConductivity,

        //How much energy it takes to raise one unit of mass by one kelvin. This one is self-explanatory.
        specificHeatCapacity;

        public MaterialPreset(){

        }

        public MaterialPreset(float thermalConductivity, float specificHeatCapacity){
            this.thermalConductivity = thermalConductivity;
            this.specificHeatCapacity = specificHeatCapacity;
        }
    }

    /**
     * An area of the grid which can be disabled/enabled, and updated.
     */
    public static class Chunk{

        public int x, y, width, height;
        public Seq<GridTile> tiles;

        public int size;
        public boolean enabled;
        public int disabledCounter;

        public float totalFlow;


        public Chunk(int x, int y, int width, int height, boolean enabled){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            tiles = new Seq<>();
            this.enabled = enabled;
            disabledCounter = 0;
        }

        /**
         * Updates grid state flows for their right and top neighbours, going from bottom left to top right.
         */
        public void update(){

            //If chunk is disabled, blocks inside won't update neighbours, but neighbours can still update them
            if(!enabled) return;

            for (GridTile tile: tiles) {
                //In cases where no exchange should be possible - especially in space - skip all flow calculations
                //This lets tiles outside disabled chunks handle neighbours properly
                if(!tile.enabled) continue;

                //Do adjacency calculation for each layer of the tile
                GridHeatState floor = tile.floor;
                GridHeatState block = tile.block;
                GridHeatState air = tile.air;

                GridHeatState top = tile.solid ? block : floor;

                tile.updates.each(target -> {
                    if(!target.enabled) return;

                    //It's either this check, or queueing updates in *all* GridTiles
                    if(target.updates.contains(tile)) target.updates.remove(tile);

                    if(floor.enabled && target.floor.enabled) handleExchange(floor, target.floor);
                    if(block.enabled && target.block.enabled) handleExchange(block, target.block);
                    if(air.enabled && target.air.enabled) handleExchange(air, target.air);
                });
                tile.updates.clear();

                if(air.enabled) air.flow += calculateFlowAtmosphere(air.mass, kelvins(air), air.material);

                if(top.enabled && air.enabled && !tile.shielded){
                    handleExchange(top, air);
                }
            }
        }
    }

    //Coppied from Xelo, yoinky~
    //I swear I  half understand how it works
    public class HeatRunnerThread extends Thread {
        boolean terminate, doStep;
        public int currentTime;
        public float targetTime;
        final Object waitSync = new Object();

        @Override
        public void run() {
            super.run();
            Log.info("--- Heat runner thread started");
            try {
                while (!terminate) {
                    while (!doStep) {
                        Thread.sleep(16);
                        if (Core.app.isDisposed()) {
                            Log.info(" >>>>> Thread terminated due to app"); // we have to busy wait bc theres no hook for app termination
                            return;
                        }
                    }

                    while (currentTime < targetTime) {
                        tick();
                        synchronized (waitSync) {
                            currentTime++;
                        }
                    }
                    doStep = false;
                }
            }
            catch(
            InterruptedException e)
            {
                terminate = true;
                Log.info(" >>>>> Thread terminated");
                return;
            }catch(Exception e){
                Log.debug(e);
                Log.debug(Arrays.asList(e.getStackTrace()).toString());
                Call.sendChatMessage(e.toString());
                Log.info(" >>>>> Thread terminated");
                return;
            }
        }

        public void updateTime(float delta){
            targetTime += delta;
            if(targetTime > currentTime){
                doStep = true;
            }
        }
    };
}
