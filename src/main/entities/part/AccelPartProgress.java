package main.entities.part;

import arc.math.Mathf;
import mindustry.entities.part.DrawPart;

public class AccelPartProgress implements DrawPart.PartProgress {
    public float startingDisplacement;
    public float startingVel;
    public float acceleration;
    public float min;
    public float max;
    public float time;
    public DrawPart.PartProgress parent;

    public AccelPartProgress(DrawPart.PartProgress parent){
        startingDisplacement = 0;
        startingVel = 0;
        acceleration = 0;
        min = 0;
        max = 0;
        time = 0;
        this.parent = parent;
    }

    public AccelPartProgress(float startingDisplacement, float startingVel, float acceleration, float min, float max, float time, DrawPart.PartProgress parent){
        this.startingDisplacement = startingDisplacement;
        this.startingVel = startingVel;
        this.acceleration = acceleration;
        this.min = min;
        this.max = max;
        this.time = time;
        this.parent = parent;
    }

    @Override
    public float get(DrawPart.PartParams p) {
        float scaledTime = parent.get(p) * time;
        //Area under a triangle is base * width * 1/2, so since the area under the velocity graph is displacement, it can be gotten by 1/2 * time * (time * accel)
        float displacement = startingDisplacement + startingVel * scaledTime + (1/2.0f) * acceleration * scaledTime * scaledTime;
        float clampedDisplacement = Mathf.clamp(displacement, min, max);
        return clampedDisplacement/max;
    }
}
