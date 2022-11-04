package frostscape.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.scene.ui.layout.Scl;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;

import static mindustry.Vars.headless;

public class FrostShaders {
    public static NamedShader pauseMenu, light;
    public static boolean loaded = false;
    public static FrameBuffer effectBuffer,
    //captures everything before ui starts rendering
    bufferScreen;
    public static void load(){
        if(headless) return;
        loaded = true;

        effectBuffer = new FrameBuffer(Core.graphics.getWidth(), Core.graphics.getHeight());
        bufferScreen = new FrameBuffer(Core.graphics.getWidth(), Core.graphics.getHeight());
        try {
            pauseMenu = new NamedShader("pauseMenu");
            light = new NamedShader("light");
        }
        catch (IllegalArgumentException error){
            loaded = false;
            Log.err("Failed to load Frostscape's shaders: " + error);
        }
    }

    public static void dispose(){
        if(!headless && loaded){
            pauseMenu.dispose();
            light.dispose();
        }
    }

    /** Shaders that the the*/
    public static class NamedShader extends Shader {
        public NamedShader(String name) {
            super(Core.files.internal("shaders/screenspace.vert"),
                    Vars.tree.get("shaders/" + name + ".frag"));
        }

        @Override
        public void apply() {
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_campos",
                    Core.camera.position.x,
                    Core.camera.position.y
            );
            setUniformf("u_resolution",
                    Core.graphics.getWidth(),
                    Core.graphics.getHeight()
            );
            setUniformf("u_drawCol", Draw.getColor().r,  Draw.getColor().g,  Draw.getColor().b,  Draw.getColor().a);
        }
    }
}
