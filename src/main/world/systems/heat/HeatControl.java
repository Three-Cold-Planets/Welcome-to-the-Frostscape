package main.world.systems.heat;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.*;
import arc.util.Log;
import mindustry.gen.Call;
import mindustry.io.SaveFileReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

//Class which stores and controls the heat in both blocks and floors and their flow.
public class HeatControl implements SaveFileReader.CustomChunk {

    static HeatControl instance;

    public static HeatControl get() {
        if (instance == null) instance = new HeatControl();
        return instance;
    }

    public HeatControl(){

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

    public static boolean gridLoaded = false;

    private static final ArrayList<MaterialPreset> presetList = new ArrayList<>();


    //List storing all grid state chunks
    public static Chunk[] gridChunks;

    //List of all grid states
    public static GridTile[] gridTiles;

    //Sequence storing all non-grid tile states.
    public static Seq<HeatState> entityStates;

    public static HeatRunnerThread heatThread;

    //Width, height and size of the world
    public static int width, height, s,
    //Width and height of the chunk sections
    chunkW, chunkH;

    //Chunk size. DO NOT CHANGE UNLESS YOU KNOW WHAT YOU'RE DOING
    public static int chunkSize = 16;

    public static float minFlow = 0.1f;

    public static int disabledTimer = 10;

    public static void setupThread(){
        heatThread = new HeatControl.HeatRunnerThread();
        heatThread.setPriority(Thread.NORM_PRIORITY - 1);
        heatThread.setDaemon(true);
        heatThread.start();
        Log.info("Started Heat Threat");
    }

    public static void start(int width, int height){
        Log.info("Starting Heat!");
        createGrid(width, height);
        setup.setupGrid(get());
        gridLoaded = true;
    }

    public static GridTile getTile(int x, int y){
        if(x < 0 || y < 0 || x >= width || y >= height) return null;
        return gridTiles[x + y * width];
    }

    @Deprecated
    public static GridTile getTile(int index){
        return gridTiles[index];
    }
    public static void createGrid(int w, int h){
        width = w;
        height = h;

        s = w * h;
        gridTiles = new GridTile[s];
        entityStates = new Seq<>(false);

        //How many chunks can be fit horizontally and vertically
        chunkW = Mathf.ceil(((float) w)/chunkSize);
        chunkH = Mathf.ceil(((float) h)/chunkSize);

        gridChunks = new Chunk[chunkW * chunkH];

        //Set up chunks. Some will bee less than optimally sized, due to hitting the map border.
        for (int y = 0; y < chunkH; y++) {
            for (int x = 0; x < chunkW; x++) {

                int width = x == chunkW - 1 ? chunkSize - Mathf.mod(w, chunkSize) : chunkSize;
                int height = y == chunkH - 1 ? chunkSize - Mathf.mod(w, chunkSize) : chunkSize;

                Chunk current = new Chunk(x * chunkSize, y * chunkSize, width, height, EnableState.ENABLED);
                gridChunks[x + y * chunkW] = current;
            }
        }
        Log.info("Chunks created!");

        //Setup chunks + neighbours.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                //Get current chunk
                int chunkIndex = x/chunkSize + y/chunkSize * chunkW;

                Chunk current = gridChunks[chunkIndex];
                gridTiles[x + y * w] = tmpT1 = new GridTile(current, x, y);
                current.tiles.add(tmpT1);
            }
        }

        //Setup neighbours
        for (int i = 0; i < s; i++) {
            GridTile current = gridTiles[i];
            for (int j = 0; j < 4; j++) {
                GridTile tile = getTile(current.x + Geometry.d4(j).x, current.y + Geometry.d4(j).y);
                if(tile != null) current.adjacent.add(tile);
            }
        }
        Log.info("Grid created!");
    }

    public static void tick(){
        //Log.info("Ticking");
        updateFlow();
        setup.update(get());
        finalizeEnergy();
    }

    //Note that ambient heat is factored in after flow calculations
    public static void updateFlow()
    {
        for (Chunk gridChunk : gridChunks) {
            gridChunk.update();
            gridChunk.tiles.each(t -> {
                if(t.air.enabled) t.air.flow += calculateFlowAtmosphere(t.air.mass, kelvins(t.air), t.air.material);
            });
        }
        //entityStates.each(GridHeatState::updateState);
    }

    public static void finalizeEnergy()
    {
        for (Chunk chunk : gridChunks) {
            chunk.finalizeEnergy();
        }
    }

    //Note that this ignores surface area. That logic should be implemented in the object calling this
    public static float calculateFlow(float mass1, float mass2, float temp1, float temp2, MaterialPreset preset1, MaterialPreset preset2){

        //Debug.Log("Going from tile: " + tile1 + " to " + tile2);

        //Don't transfer heat if either masses are below 1 g. Obviously this should never happen but eh
        if (mass1 + mass2 < 2) {
            return 0;
        }

        //Take the average temperature difference. This prevents flowback loops
        float tempretureDif = (temp2 - temp1)/2;

        //Don't bother calculating if tempreture difference is less than 1 celcius
        if (Math.abs(tempretureDif) < 1f) {
            return 0;
        }

        float geomThermalConductivity = Mathf.sqrt(preset1.thermalConductivity * preset2.thermalConductivity);

        //Half the energy to prevent weird flowback loops
        float flowAmount = geomThermalConductivity * tempretureDif * simulationSpeed;

        //Don't bother using if energy flow is less than 0.1 units
        if (Math.abs(flowAmount) < minFlow) return 0;

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
    public static float kelvins(HeatState state){
        return state.temperature;
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
        public float temperature, mass, flow, lastFlow;

        public MaterialPreset material;

        public void setStats(float mass, MaterialPreset material){
            this.mass = mass;
            this.material = material;
        }

        public void setEnergy(float energy){
            temperature = kelvins(energy, mass, material.specificHeatCapacity);
        }

        public void addEnergy(float energy){
            temperature += kelvins(energy, mass, material.specificHeatCapacity);
        }
    }

    public static class GridHeatState extends HeatState {

        public GridHeatState(){
            super();
        }

        //If false, flow going from and to this state will not be calculated, however other functionality is untouched.
        public boolean enabled;

        public void finalizeEnergy(){
            addEnergy(flow);
            lastFlow = flow;
            flow = 0;
        }
    }

    /**
     * A class that stores energy, mass and energy. Position data + size handled by the chunk indexes
     * NOTE THAT WHEN ADDING TO THE STATE'S ENERGY, USE FLOW INSTEAD OF ENERGY.
     * Adjacent must be set externally
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
        public transient Seq<GridTile> adjacent;

        //Updated this tick. Used to prevent doubleups on calculations of flow
        public boolean updated;
        public GridTile(Chunk owner, int x, int y){
            this.x = x;
            this.y = y;
            enabled = true;

            floor = new GridHeatState();
            block = new GridHeatState();
            air = new GridHeatState();
            this.owner = owner;
            adjacent = new Seq<>();
        }
        public GridHeatState top(){
            return solid ? block : floor;
        }

        public void finalizeEnergy(){
            floor.finalizeEnergy();
            block.finalizeEnergy();
            air.finalizeEnergy();

            updated = false;
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
        public EnableState state;

        //If a chunk should force enable
        public int disabledCounter;

        public Chunk(int x, int y, int width, int height, EnableState enabled){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            tiles = new Seq<>();
            this.state = enabled;
            disabledCounter = 0;
        }

        /**
         * Updates grid state flows for their right and top neighbours, going from bottom left to top right.
         */
        public void update(){

            if(!state.force) state = disabledCounter < disabledTimer ? EnableState.ENABLED : EnableState.DISABLED;

            //If chunk is disabled, blocks inside won't update neighbours, but neighbours can still update them
            if(!state.enabled) return;

            for (GridTile tile: tiles) {
                //In cases where no exchange should be possible - especially in space - skip all flow calculations
                //This lets tiles outside disabled chunks handle neighbours properly
                if(!tile.enabled) continue;

                //Do adjacency calculation for each layer of the tile
                GridHeatState floor = tile.floor;
                GridHeatState block = tile.block;
                GridHeatState air = tile.air;

                GridHeatState top = tile.solid ? block : floor;

                tile.adjacent.each(target -> {
                    if(!target.enabled || target.updated) return;

                    if(floor.enabled && target.floor.enabled) handleExchange(floor, target.floor);
                    if(block.enabled && target.block.enabled) handleExchange(block, target.block);
                    if(air.enabled && target.air.enabled) handleExchange(air, target.air);
                });

                tile.updated = true;

                if(top.enabled && air.enabled && !tile.shielded){
                    handleExchange(top, air);
                }
            }
        }

        //The logic of having to ping pong between state and chunk owner was too hard to follow
        public void finalizeEnergy(){
            float highestFlow = 0;
            for(GridTile tile: tiles){
                highestFlow = Math.max(Math.max(highestFlow, tile.floor.flow), Math.max(tile.block.flow, tile.air.flow));
                tile.finalizeEnergy();
            }

            disabledCounter++;

            if(highestFlow > minFlow) {
                disabledCounter = 0;
            }
        }
    }

    //Coppied from Xelo, yoinky~
    //I swear I  half understand how it works
    public static class HeatRunnerThread extends Thread {
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

    public enum EnableState{
        FORCE_ENABLE(true, true), ENABLED(true, false), DISABLED(false, false), FORCE_DISABLE(false, true);
        public final boolean enabled;
        public final boolean force;

        EnableState(boolean enabled, boolean force){
            this.enabled = enabled;
            this.force = force;
        }
    }
}
