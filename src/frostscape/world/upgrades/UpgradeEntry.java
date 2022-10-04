package frostscape.world.upgrades;

import arc.util.Log;
import frostscape.type.upgrade.Upgrade;
import mindustry.type.ItemStack;

//Used to store information about a type's upgrades
public class UpgradeEntry {
    public float researchTime, baseInstallTime;
    //Generates an array of floats used in upgrade stats. PUT THE INDEXES IN ORDER
    public static float[] genStats(int size, float base, int[] indexes, float[] values){
        float[] returnArr = new float[size];
        float current = base;
        int index = 0;
        for (int i = 0; i < size; i++) {
            Log.info(index);
            Log.info(index < indexes.length);
            if(index < indexes.length && indexes[index] == i) {
                current = values[index];
                index++;
            }
            returnArr[i] = current;
        }
        return returnArr;
    }

    public UpgradeEntry(Upgrade upgrade){
        this.upgrade = upgrade;
    }

    public Upgrade upgrade;
    public ItemStack[][] costs;
    //Deltas applied when using an upgrade based on stack. (Copied from status effects)
    public float[]
            damageMultiplier,
            healthMultiplier,
            speedMultiplier,
            reloadMultiplier,
            rangeMultiplier,
            buildSpeedMultiplier;
    public void initialiseDeltas(){
        //Quick way to fill all empty arrays
        float[][] empty = new float[6][];
        if(healthMultiplier == null) empty[0] = healthMultiplier = new float[stacks() + 1];
        if(damageMultiplier == null) empty[1] = damageMultiplier = new float[stacks() + 1];
        if(reloadMultiplier == null) empty[2] = reloadMultiplier = new float[stacks() + 1];
        if(rangeMultiplier == null) empty[3] = rangeMultiplier = new float[stacks() + 1];
        if(speedMultiplier == null) empty[4] = speedMultiplier = new float[stacks() + 1];
        if(buildSpeedMultiplier == null) empty[5] = buildSpeedMultiplier = new float[stacks() + 1];

        for (int i = 0; i < empty.length; i++) {
            if(empty[i] != null) for (int j = 0; j < empty[i].length; j++) {
                empty[i][j] = 1;
            }
        }
    }

    public int stacks(){
        return costs.length;
    }
}
