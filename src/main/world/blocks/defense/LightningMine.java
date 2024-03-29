package main.world.blocks.defense;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import main.entities.ModDamage;
import main.graphics.ModPal;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

public class LightningMine extends UpgradeableMine{
    protected static boolean unitFound;
    protected static Seq<Unit> list = new Seq<>();
    protected static int index = 0;
    public int lightning, branches;
    public int chain;
    public float damage,
            //Damage percent lost when damaging
            pierceDamageFactor,
            //Damage lost per world unit traveled. Applied after pierce damage factor
            distanceDamageFalloff, segmentLength;
    public float width, arc;
    public float range, activationRange;

    public int sectors;
    public float sectorFrac, sectorStroke;
    public Color sectorColor = ModPal.hunter;
    public Effect hitEffect = Fx.hitLancer;

    public float warmupSpeed, warmDownSpeed;
    public Color color;

    public LightningMine(String name) {
        super(name);
        lightning = 3;
        branches = 2;
        chain = 2;
        width = 8;
        arc = 0.35f;
        activationRange = 74;
        pierceDamageFactor = 0.85f;
        distanceDamageFalloff = 0.65f;
        damage = 85;
        segmentLength = 4;
        sectorFrac = 0.05f;
        sectorStroke = 3.5f;
        warmupSpeed = 0.015f;
        warmDownSpeed = 0.025f;
        tileDamage = 0.8f;
        color = ModPal.glowCyan;
    }

    @Override
    public void load() {
        super.load();
        if(range == 0) range = damage/distanceDamageFalloff;
        if(sectors == 0) sectors = ((int) range/16) + 3;
    }

    public class LightningMineBuild extends UpgradeableMineBuild{

        public Seq<Unit> targets = new Seq<>();

        @Override
        public void updateTile() {
            super.updateTile();
            unitFound = false;
            float trange = (activated() ? range : activationRange) * rangeMultiplier;
            Units.nearby(x - trange, y - trange, trange * 2, trange * 2, u -> {
                float dst = u.dst(this);
                if(!canTarget(u)) return;
                if(dst > trange) return;
                unitFound = true;
            });
            boolean canActivate = unitFound == true;
            warmup = Mathf.approachDelta(warmup, canActivate ? 1 : 0, canActivate ? warmupSpeed * speedMultiplier : warmDownSpeed * speedMultiplier);

            if(warmup >= 1 && timer.get(timerDamage, cooldown/speedMultiplier)) triggered();
        }

        @Override
        public void draw() {
            super.draw();
            Lines.stroke(6);
            Draw.z(Layer.effect);

            float progress = Interp.smooth.apply(warmup);
            Lines.stroke(progress * sectorStroke);
            Draw.color(sectorColor);
            for (int i = 0; i < sectors; i++) {
                Lines.arc(x, y, Mathf.lerp(activationRange * rangeMultiplier, range * rangeMultiplier, progress), sectorFrac, i * 360/sectors + Time.time);
            }
        }

        public void drawSelect() {
            Drawf.dashCircle(x, y, range * rangeMultiplier, team.color);
            Drawf.dashCircle(x, y, activationRange * rangeMultiplier, team.color);
            targets.each(u -> {
                Drawf.square(u.x, u.y, u.hitSize, 45, Pal.accent);
            });
        }

        @Override
        public void triggered() {
            float trange = range * rangeMultiplier;
            Seq<Unit> units = Groups.unit.intersect(x - trange, y - trange, trange * 2, trange * 2);
            units.sort(u -> u.dst(this));
            list.clear();
            for (int i = 0; i < Math.min(lightning, units.size); i++) {
                Unit unit = units.get(i);
                float dst = unit.dst(this);
                if(dst > trange) break;
                list.add(unit);
                if(!targets.contains(unit)) targets.add(unit);
            }

            index = 0;
            list.each(u -> {
                index++;
                Time.run(15 + index * 5, () -> {
                    if(targets.contains(u)) targets.remove(u);
                    if(u.dst(this) > trange) return;
                    damage(tileDamage);
                    ModDamage.chain(this, u, new IntSeq(), shootSound, hitEffect, damage * damageMultiplier, damage * damageMultiplier, width, distanceDamageFalloff, pierceDamageFactor, branches, segmentLength, arc, color);
                });
            });
        }
    }
}
