package main.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.graphics.CacheLayer;

public class ModCacheLayer {
    public static ShaderLayer ice;

    public static void init(){
        ice = new ShaderLayer(FrostShaders.ice);
    }
    public static class ShaderLayer extends CacheLayer {
        @Nullable
        public Shader shader;

        public ShaderLayer(Shader shader) {
            this.shader = shader;
        }

        public void begin() {
            if (Core.settings.getBool("animatedwater")) {
                Vars.renderer.blocks.floor.endc();
                Vars.renderer.effectBuffer.begin();
                Core.graphics.clear(Color.clear);
                Vars.renderer.blocks.floor.beginc();
            }
        }

        public void end() {
            if (Core.settings.getBool("animatedwater")) {
                Vars.renderer.blocks.floor.endc();
                Vars.renderer.effectBuffer.end();
                Vars.renderer.effectBuffer.blit(this.shader);
                Vars.renderer.blocks.floor.beginc();
            }
        }
    }
}
