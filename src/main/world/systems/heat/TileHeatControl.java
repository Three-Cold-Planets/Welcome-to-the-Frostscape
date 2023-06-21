package main.world.systems.heat;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

//Class which stores and controls the heat in both blocks and floors and their flow.
public class TileHeatControl implements SaveFileReader.CustomChunk {

    public static TileHeatControl instance;

    public static TileHeatControl get(){
        if(instance == null) return instance = new TileHeatControl();
        return instance;
    }
    public TileHeatSetup setup;

    //Ambient Temperature in celsius
    public float ambientTemperature = 303.15f,
    //Rate at which non-shielded block's energy changes toward the ambient temp in kelvins
    envTempChange = 0.01f;
    private static MaterialPreset tmpMP1 = new MaterialPreset(), tmpMP2 = new MaterialPreset();

    //
    public static MaterialPreset defaultFloor = new MaterialPreset(0.12f, 1),
            defaultBlock = new MaterialPreset(0.07f, 3),
            defaultAir = new MaterialPreset(0.4f, 0.6f);
    public float simulationSpeed = 1;
    public static boolean enabled;

    public boolean gridLoaded = false;

    private static final ArrayList<MaterialPreset> presetList = new ArrayList<>();

    //Used to match up tile indexes with their correct properties. Reinitialized every time the world is loaded.
        public final IntMap<MaterialPreset> tilePropertyAssociations = new IntMap<>();

    //The array is separated into two parts. The first bit is the floor tiles, and second bit is the block tiles. A floor tile occupies the same tile as a block tile when the index for the floor + s gets to the block
    public float[] energyValues, massValues;

    //I wanted to use a single array for this and the realized how painful that might be/itterating through it all, just want to get it working rn ;~:
    //The arrays of the array in the flowmap store data about a tile's neighbours and the flow of heat between them.
    public float[][] neighbourFlowmap;

    public HeatRunnerThread heatThread;
    public int w, h, s;

    public void setupThread(){
        heatThread = new TileHeatControl.HeatRunnerThread();
        heatThread.setPriority(Thread.NORM_PRIORITY - 1);
        heatThread.setDaemon(true);
        heatThread.start();
        Log.info("Started Heat");
    }
    public void setTileValues(int index, float energy, float mass, MaterialPreset preset){
        energyValues[index] = energy;
        massValues[index] = mass;
        tilePropertyAssociations.put(index, preset);
    }
    public static class MaterialPreset{
        public MaterialPreset(){

        }

        public MaterialPreset(float thermalConductivity, float specificHeatCapacity){
            this.thermalConductivity = thermalConductivity;
            this.specificHeatCapacity = specificHeatCapacity;
        }
        public float
                //How conductive the material is. More Thermal Conductivity means more flow of heat between the tile and it's neighbours.
                // Conductivity works on averages, so something with almost no conductivity next to something with high conductivity will still conduct heat.
                thermalConductivity,
                //How much energy it takes to raise one unit of mass one kelvin. This one is self-explanatory.
                specificHeatCapacity;
    }

    public void start(int width, int height){
        initializeValues();
        createGrid(width, height);
        setup.setupGrid(this);
        gridLoaded = true;
    }

    public float getEnergy(int x, int y, boolean floor){
        return getEnergy(x + y * w + (floor ? s : 0));
    }

    public float getEnergy(int index){
        return energyValues[index];
    }

    public float getMass(int x, int y, boolean floor){
        return getMass(x + y * w + (floor ? s : 0));
    }

    public float getMass(int index){
        return massValues[index];
    }

    public float[] neighbours(int tile){
        return neighbourFlowmap[tile];
    }
    public void createGrid(int w, int h){
        this.w = w;
        this.h = h;
        //How large a single grid is on the array
        s = w * h;
        energyValues = new float[s];
        massValues = new float[s];
        neighbourFlowmap = new float[s][8];

        //Set up neighbours
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int index = x + y * w;
                for (int i = 0; i < 4; i++) {
                    int sideIndex = index + Geometry.d4x(i) + Geometry.d4y(i) * w;
                    if(sideIndex < 0 || sideIndex >= s) {
                        neighbourFlowmap[index][i * 2] = -1;
                        continue;
                    }
                    //Set up both floor and block tiles with their respective neighbours
                    int side = i * 2;
                    int otherSide = (side + 4) % 8;

                    neighbourFlowmap[index][side] = sideIndex;
                    neighbourFlowmap[sideIndex][otherSide] = index;
                }
            }
        }
    }

    public void tick(){
        Log.info("Ticking");
        setup.update(this);
        updateFlow();
        updateEnergy();
    }

    //Note that ambient heat is factored in after flow calculations
    public void updateFlow()
    {
        //Got to loop through every tile here...
        for (int i = 0; i < s; i++) {
            //Flow values associated with the tile index are stored every second entry in the array, and start halfway through

                MaterialPreset preset = tilePropertyAssociations.get(i);
                float kelvins = kelvins(i);
                float mass = massValues[i];
                for (int j = 5; j < 9; j += 2) {
                    int side = j - 1;
                    int neighbourIndex = (int) neighbourFlowmap[i][side];
                    if(neighbourIndex == -1) continue;
                    int otherSide = (side + 4) % 8;
                    float flow = calculateFlow(mass, massValues[neighbourIndex], kelvins, kelvins(neighbourIndex), preset, tilePropertyAssociations.get(neighbourIndex));

                    neighbourFlowmap[i][side + 1] = flow;
                    neighbourFlowmap[neighbourIndex][otherSide + 1] = -flow;
                }
            }
    }

    public void updateEnergy(){
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < 8; j += 2) {

                int otherSide = (j + 4) % 8;
                //Add 1 to get the associated flow values
                energyValues[i] += neighbourFlowmap[i][j + 1];
            }
        }
    }

    public float calculateFlow(float mass1, float mass2, float temp1, float temp2, MaterialPreset preset1, MaterialPreset preset2){

        //Debug.Log("Going from tile: " + tile1 + " to " + tile2);

        //Don't transfer heat if either masses are below 1 g. Obviously this should never happen but eh
        if (mass1 + mass2 < 2) return 0;

        float tempretureDif = temp2 - temp1;

        //Debug.Log("Starting tempretures are " + tile1.energy + " and " + tile2.energy);

        //Debug.Log("Tempreture difference is: " + tempretureDif);

        //Don't bother calculating if tempreture difference is less than 1 celcius
        if (Math.abs(tempretureDif) < 1f) return 0;

        float geomThermalConductivity = Mathf.sqrt(preset1.thermalConductivity * preset2.thermalConductivity);


        float flowAmount = geomThermalConductivity * tempretureDif * simulationSpeed;

        //Debug.Log(flowAmount);

        //Don't bother using if energy flow is less than 0.1 units
        if (Math.abs(flowAmount) < 0.1f) return 0;


        //Cap change of energy to 1/5 of the temp difference changed per tick
        float maxTempDif = Math.min(Math.abs(tempretureDif/5 * mass1 * preset1.specificHeatCapacity),
                Math.abs(tempretureDif / 5 * mass2 * preset2.specificHeatCapacity));

        //Debug.Log("Flow amount is " + flowAmount + " with max of " + maxTempDif);
        //Debug.Log("Flow is " + Mathf.Clamp(flowAmount, -maxTempDif, maxTempDif));

        return Mathf.clamp(flowAmount, -maxTempDif, maxTempDif);
    }
    public void initializeValues(){

    }
    public float kelvins(int index){
        tmpMP1 = tilePropertyAssociations.get(index);
        if(tmpMP1 == null) {
            Log.info("-----");
            Log.info(index);
            Log.info(tilePropertyAssociations.size);
            Log.info("-----");
            return 0;
        }
        return kelvins(energyValues[index], massValues[index], tmpMP1.specificHeatCapacity);
    }

    public float celsius(int index){
        return kelvins(index) - 273.15f;
    }
    public float kelvins(float energy, float mass, float SPH){
        return energy/(mass*SPH);
    }

    public float celsius(float energy, float mass, float SPH){
        return kelvins(energy, mass, SPH) - 273.15f;
    }

    public float totalFlow(int index){
        float total = 0;
        for(int i = 1; i < 8; i += 2){
            total += Math.abs(neighbourFlowmap[index][i]);
        }
        return total;
    }

    @Override
    public void write(DataOutput stream) throws IOException {
        stream.writeBoolean(enabled);
        if(!enabled) return;
        for (int i = 0; i < s; i++) {
            stream.writeFloat(energyValues[i]);
            stream.writeFloat(massValues[i]);
        }
    }

    @Override
    public void read(DataInput stream) throws IOException {
        enabled = stream.readBoolean();
        if(!enabled) return;
        for (int i = 0; i < s * 2; i++) {
            energyValues[i] = stream.readFloat();
            massValues[i] = stream.readFloat();
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
