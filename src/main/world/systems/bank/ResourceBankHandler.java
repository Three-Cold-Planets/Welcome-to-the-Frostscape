package main.world.systems.bank;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.io.SaveFileReader;
import mindustry.world.Block;
import mindustry.world.modules.ItemModule;
import mindustry.world.modules.LiquidModule;
import mindustry.world.modules.PowerModule;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ResourceBankHandler implements SaveFileReader.CustomChunk {
    public static ResourceBankHandler instance;

    public static ResourceBankHandler get(){
        if(instance == null) instance = new ResourceBankHandler();
        return instance;
    }

    public static PowerModule power = new PowerModule();
    public static ItemModule items = new ItemModule();
    public static LiquidModule liquids = new LiquidModule();

    //Theese two handle all the logic involved with the graphs
    public static Block block;
    public static Building building;
    public static int powerCap, itemCap, liquidCap;

    public ResourceBankHandler(){

    }

    public static void init(){
        building = block.newBuilding();
        building.block = block;
        block.load();

        building.power = power;
        power.graph.add(building);

        building.liquids = liquids;
        building.items = items;
    }

    public static void setup(){
        block.consPower.capacity = powerCap;
        block.itemCapacity = itemCap;
        block.liquidCapacity = liquidCap;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Writes write = new Writes(dataOutput);
        write.i(powerCap);
        write.i(itemCap);
        write.i(liquidCap);
        power.write(write);
        items.write(write);
        liquids.write(write);
    }

    @Override
    public void read(DataInput dataInput) throws IOException {
        Reads read = new Reads(dataInput);;
        powerCap = read.i();
        itemCap = read.i();
        liquidCap = read.i();
        power.read(read);
        items.read(read);
        liquids.read(read);

        setup();
    }
}
