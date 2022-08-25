package frostscape.world.upgrades;

import arc.struct.*;
import arc.struct.IntMap.Entry;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.type.upgrade.Upgrade;
import mindustry.game.Team;
import mindustry.io.SaveFileReader.CustomChunk;
import mindustry.io.SaveVersion;

import java.io.*;
import java.util.Iterator;

public class UpgradeHandler {

    public static Seq<Upgrade> upgrades = new Seq<Upgrade>();

    public UpgradeHandler(){

    }
}
