package frostscape.type;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.*;
import frostscape.content.Palf;
import frostscape.time.Stopwatch;
import frostscape.util.StatUtils;
import frostscape.world.meta.Family;
import frostscape.world.meta.stat.FrostStats;
import mindustry.Vars;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.type.UnitType.UnitEngine;
import mindustry.world.meta.Stat;
import rhino.*;

import java.lang.reflect.Field;

public class HollusUnitType extends UnitType{
    public Seq<Family> families;

    public HollusUnitType(String name) {
        super(name);
        outlineColor = Palf.quiteDarkOutline;
    }

    @Override
    public void init() {
        super.init();
        families.each(family -> family.members.add(this));
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.useCategories = true;

        if(families != null && families.size > 0){
            StatUtils.addFamilyStats(stats, families);
        }
    }

    public static class ActivationEngine extends UnitEngine {
        public float from, to, threshold, target;

        public ActivationEngine(float x, float y, float radius, float rotation, float from, float to, float threshold, float target){
            super(x, y, radius, rotation);
            this.from = from;
            this.to = to;
            this.threshold = threshold;
            this.target = target;
        }

        public void draw(Unit unit) {
            //take the smaller difference of the two
            float diff = 1 - Math.min(Angles.forwardDistance(unit.vel.angle(), unit.rotation)
                    , Angles.backwardDistance(unit.vel.angle(), unit.rotation))/360;
            float activation = Mathf.lerp(from, to, Mathf.clamp(Mathf.maxZero(unit.vel().len2() - threshold) / target, 0, 1)) * diff;
            float iradius = radius;

            radius *= activation;

            super.draw(unit);

            radius = iradius;
        }

    }
}
