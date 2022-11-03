package frostscape.graphics;

import arc.Core;
import arc.Events;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.gl.FrameBuffer;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.graphics.Shaders;

import static mindustry.Vars.headless;

public class FrostShaders {
    public static boolean loaded = false;
    public static FrameBuffer buffer,
    //captures everything before ui starts rendering
    bufferScreen;
    public static void load(){
        if(headless) return;
        loaded = true;

        buffer = new FrameBuffer(Core.graphics.getWidth(), Core.graphics.getHeight());
        bufferScreen = new FrameBuffer(Core.graphics.getWidth(), Core.graphics.getHeight());
        try {

        }
        catch (IllegalArgumentException error){
            loaded = false;
            Log.err("Failed to load Frostscape's shaders: " + error);
        }

        /*
        Events.run(EventType.Trigger.draw, () -> {
            bufferScreen.begin(Color.white);
        });
        Events.run(EventType.Trigger.postDraw, () -> {
            bufferScreen.end();
            Blending.disabled.apply();
            bufferScreen.blit(Shaders.screenspace);
        });

         */

    }

    public static void dispose(){
        if(!headless && loaded){
        }
    }
}
