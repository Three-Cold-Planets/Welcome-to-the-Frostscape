package main.graphics.trail;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import mindustry.graphics.Trail;

public class SmokeTrail extends Trail {

    //Used as a seed
    protected int trailNumb = 0;
    public SmokeTrail(int length) {
        super(length);
    }

    //Invert trail width
    @Override
    public void draw(Color color, float width) {
        Draw.color(color);
        float[] items = this.points.items;
        float lastAngle = this.lastAngle;
        float size = width / (float)(this.points.size / 3);

        for(int i = 0; i < this.points.size; i += 3) {
            float x1 = items[i];
            float y1 = items[i + 1];
            float w1 = items[i + 2];
            float x2;
            float y2;
            float w2;
            if (i < this.points.size - 3) {
                x2 = items[i + 3];
                y2 = items[i + 4];
                w2 = items[i + 5];
            } else {
                x2 = this.lastX;
                y2 = this.lastY;
                w2 = this.lastW;
            }

            float z2 = -Angles.angleRad(x1, y1, x2, y2);
            float z1 = i == 0 ? z2 : lastAngle;
            Draw.alpha(1 - (float)i/points.size);
            Log.info(1 - (float)i/points.size);
            if (!(w1 <= 0.001F) && !(w2 <= 0.001F)) {
                float cx = Mathf.sin(z1) * (float)i / 3.0F * size * w2;
                float cy = Mathf.cos(z1) * (float)i / 3.0F * size * w2;
                float nx = Mathf.sin(z2) * ((float)i / 3.0F + 1.0F) * size * w1;
                float ny = Mathf.cos(z2) * ((float)i / 3.0F + 1.0F) * size * w1;
                Fill.quad(x1 - cx, y1 - cy, x1 + cx, y1 + cy, x2 + nx, y2 + ny, x2 - nx, y2 - ny);
                lastAngle = z2;
            }
        }

        Draw.reset();
    }

    //Don't.
    @Override
    public void drawCap(Color color, float width) {

    }

    @Override
    public void update(float x, float y, float width) {
        if ((this.counter += Time.delta) >= 1.0F) {
            if (this.points.size > this.length * 3) {
                this.points.removeRange(0, 2);
            }

            this.points.add(x, y, width);
            this.counter %= 1.0F;
            this.trailNumb++;
        }

        this.lastAngle = -Angles.angleRad(x, y, this.lastX, this.lastY);
        this.lastX = x;
        this.lastY = y;
        this.lastW = width;
    }
}
