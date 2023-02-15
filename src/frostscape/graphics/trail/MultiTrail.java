package frostscape.graphics.trail;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.graphics.Trail;

public class MultiTrail extends Trail {
    public Seq<Trail> children;


    public MultiTrail(int length) {
        super(length);
    }

    @Override
    public void update(float x, float y, float width) {
        super.update(x, y, width);
        children.each(t -> t.update(x, y, width));
    }

    @Override
    public void draw(Color color, float width) {
        super.draw(color, width);
        children.each(t -> t.draw(color, width));
    }

    @Override
    public void drawCap(Color color, float width) {
        super.drawCap(color, width);
        children.each(t -> t.drawCap(color, width));
    }
}
