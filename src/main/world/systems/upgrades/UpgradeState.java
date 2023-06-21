package main.world.systems.upgrades;

import arc.util.io.Reads;
import arc.util.io.Writes;
import main.io.ItemIO;
import mindustry.type.ItemStack;

import java.util.Arrays;

import static main.world.systems.upgrades.UpgradeHandler.upgrades;

public class UpgradeState {
    public ItemStack[] cost, items;
    public Upgrade upgrade;
    //Level of the state. If it's -1 it has not been installed yet. Use getLevel to avoid confusion.
    public int level = -1;
    //The progress on installing an upgrade's next level.
    public float progress;
    public boolean
        //If the upgrade has been installed on the entity.
        installed = false,
        //If the upgrade is installing it's next level. Defaults to false.
        installing = false;

    //NOTE: Only to be used for reading data!
    public UpgradeState(){

    }
    public UpgradeState(Upgrade upgrade){
        this.upgrade = upgrade;
        this.level = -1;
        this.cost = ItemStack.empty;
        this.items = ItemStack.empty;
    }
    public UpgradeState(Upgrade upgrade, ItemStack[] cost){
        this.upgrade = upgrade;
        this.cost = cost;
        this.items = ItemStack.mult(cost, 0);
    }
    public int getLevel(){
        return Math.max(0, level);
    }

    public float progress(ProgressType type){
        float total = 0;
        switch (type){
            case PER_ITEM: {
                for (int i = 0; i < cost.length; i++) {
                    total = items[i].amount/cost[i].amount;
                }
                break;
            }
            case TOTAL: {
                float iitems = 0, citems = 0;
                for (int i = 0; i < cost.length; i++) {
                    iitems += items[i].amount;
                    citems += cost[i].amount;
                }
                total = iitems/citems;
            }
        }
        return total;
    }

    public enum ProgressType{
        TOTAL,
        PER_ITEM
    }

    public void write(Writes write){
        write.str(upgrade.name);
        write.i(level);
        write.f(progress);
        write.bool(installed);
        write.bool(installing);
        ItemIO.writeStacks(write, cost);
        ItemIO.writeStacks(write, items);
    }

    public UpgradeState read(Reads read){
        String name = read.str();
        upgrade = upgrades.find(u -> u.name.equals(name));
        level = read.i();
        progress = read.f();
        installed = read.bool();
        installing = read.bool();
        cost = ItemIO.readStacks(read);
        items = ItemIO.readStacks(read);
        return this;
    }

    @Override
    public String toString() {
        return "UpgradeState{" +
                "cost=" + Arrays.toString(cost) +
                ", items=" + Arrays.toString(items) +
                ", upgrade=" + upgrade +
                ", level=" + level +
                ", progress=" + progress +
                ", installed=" + installed +
                ", installing=" + installing +
                '}';
    }
}

