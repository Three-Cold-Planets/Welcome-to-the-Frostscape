package frostscape.game;

import arc.Events;
import arc.assets.Loadable;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.io.SaveFileReader.CustomChunk;
import mindustry.io.SaveVersion;

import java.io.*;

import static mindustry.Vars.state;

/**
 * A class which handles scripted sector controllers, see {@link SectorController}
 */
public class ScriptedSectorHandler implements Loadable, CustomChunk {

    public static final String saveKey = "frostscape-controller";
    //A list of all scripted sectors
    public static Seq<ScriptedSector> scriptedSectors = Seq.with();

    //A list of all controllers
    public static Seq<SectorController> controllers = Seq.with();

    //Used in the case that no valid controller is found. Unmapped.
    public static SectorController defaultController = new DefaultController("default");

    //Current controller
    public SectorController controller = defaultController;

    //Current sector preset. Can be null.
    public @Nullable ScriptedSector preset;

    public boolean hasRead = false;

    private ScriptedSector sector;

    public ScriptedSectorHandler(){
        SaveVersion.addCustomChunk("frostscape-SCH", this);
        Events.run(EventType.SaveLoadEvent.class, () -> {
            //Fallback for the editor
            if(!hasRead) reRead();
        });

        Events.run(EventType.WaveEvent.class, () -> {
            if(controller != null) controller.wave();
        });

        Events.run(Trigger.update, () -> {
            if(controller != null) controller.update();
        });
    }

    @Override
    public void write(DataOutput stream) throws IOException {
        Log.info("Writing sector controller");
        Writes write = new Writes(stream);
        write.str(controller.name);
        controller.write(new Writes(stream));
        hasRead = false;
    }

    @Override
    public void read(DataInput stream) throws IOException {
        Log.info("Reading sector controller");
        Reads reads = new Reads(stream);
        String lastName = reads.str();
        Log.info(lastName);
        SectorController last = controllers.find(c -> c.name.equals(lastName));

        String current = state.rules.tags.get(saveKey, "");
        controller = controllers.find(c -> c.name.equals(current));

        Log.info(current);

        Log.info(last);
        Log.info(controller);

        //If the controllers don't match. Get the last controller to read the data and then reset it.
        if(last != controller) {
            last.read(reads);
            last.reset();
            Log.err("Sector controllers " + controller + " in map " + state.map.file.name() + " conflicts with previously saved controller of " + last);
            return;
        }
        if(controller == null) return;
        controller.read(reads);
        Log.info("Read controller successfully");
        hasRead = true;
    }

    public void reRead(){
        Log.info("Reading tags again");
        String current = state.rules.tags.get(saveKey, "");
        Log.info(current);
        if(current == "") return;
        controller = controllers.find(c -> c.name.equals(current));
        Log.info(controller);
    }
    

    @Override
    public boolean shouldWrite() {
        return controller != null;
    }
}