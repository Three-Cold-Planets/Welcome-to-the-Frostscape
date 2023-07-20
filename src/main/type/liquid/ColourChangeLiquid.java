package main.type.liquid;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.game.EventType;
import mindustry.graphics.MultiPacker;
import mindustry.type.Liquid;

public class ColourChangeLiquid extends Liquid {

    public TextureRegion[] regions;

    public int colState;

    public float transitionTime = 180;
    public Color[] colors;

    /** If >0, this liquid is animated. */
    public int frames = 0;
    /** Number of generated transition frames between each frame */
    public int transitionFrames = 0;
    /** Ticks in-between animation frames. */
    public float frameTime = 5f;

    public ColourChangeLiquid(String name) {
        super(name);
    }

    @Override
    public void loadIcon(){
        super.loadIcon();

        //animation code ""borrowed"" from Project Unity - original implementation by GlennFolker and sk7725
        if(frames > 0){
            regions = new TextureRegion[frames * (transitionFrames + 1)];

            if(transitionFrames <= 0){
                for(int i = 1; i <= frames; i++){
                    regions[i - 1] = Core.atlas.find(name + i);
                }
            }else{
                for(int i = 0; i < frames; i++){
                    regions[i * (transitionFrames + 1)] = Core.atlas.find(name + (i + 1));
                    for(int j = 1; j <= transitionFrames; j++){
                        int index = i * (transitionFrames + 1) + j;
                        regions[index] = Core.atlas.find(name + "-t" + index);
                    }
                }
            }

            fullIcon = new TextureRegion(fullIcon);
            uiIcon = new TextureRegion(uiIcon);

            Events.run(EventType.Trigger.update, () -> {
                int frame = (int)(Time.globalTime / frameTime) % regions.length;

                fullIcon.set(regions[frame]);
                uiIcon.set(regions[frame]);

                int col = (int)(Time.globalTime / transitionTime) % colors.length;

                color = colors[col].cpy().lerp(colors[(col + 1) % colors.length], (Time.globalTime / transitionTime) % 1);
            });
        }
    }

    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);

        //create transitions
        if(frames > 0 && transitionFrames > 0){
            var pixmaps = new PixmapRegion[frames];

            for(int i = 0; i < frames; i++){
                pixmaps[i] = Core.atlas.getPixmap(name + (i + 1));
            }

            for(int i = 0; i < frames; i++){
                for(int j = 1; j <= transitionFrames; j++){
                    float f = (float)j / (transitionFrames + 1);
                    int index = i * (transitionFrames + 1) + j;

                    Pixmap res = Pixmaps.blend(pixmaps[i], pixmaps[(i + 1) % frames], f);
                    packer.add(MultiPacker.PageType.main, name + "-t" + index, res);
                }
            }
        }
    }
}
