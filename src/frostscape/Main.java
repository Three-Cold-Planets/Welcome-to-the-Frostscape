package frostscape;

import arc.*;
import arc.util.*;
import frostscape.content.FrostBullets;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

public class Main extends Mod{

    public Main(){
        float current = Time.millis();
        Log.format("Loaded Main in. {0}", (Time.millis() - current));
        String p = "p";
    }


    public void loadContent(){
        FrostContentLoader.load();
    }

}
