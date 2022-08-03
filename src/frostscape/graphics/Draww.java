package frostscape.graphics;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;

public class Draww {
    //draws a chain of sprites
    public static void drawChain(TextureRegion region, float x, float y, float endx, float endy, float drawRotation){
        float angleToEnd = Mathf.angle(endx - x, endy - y);
        float distance = Mathf.dst(x, y, endx, endy);
        float remainder = ((distance * 4) % region.height);

        for (int i = 0; i < Math.floor((distance)/region.height * 4); i++) {
            Tmp.v1.trns(angleToEnd, distance - i * region.height/4 - region.height/8).add(x ,y);
            Draw.rect(region, Tmp.v1.x, Tmp.v1.y, drawRotation + angleToEnd);
        }
        Tmp.v1.trns(drawRotation + angleToEnd + 90, remainder/8);
        Lines.line(x, y, x + Tmp.v1.x, y + Tmp.v1.y);
        Draw.rect(region, x + Tmp.v1.x, y + Tmp.v1.y, region.width/4, remainder/4, drawRotation + angleToEnd);
    }

}
