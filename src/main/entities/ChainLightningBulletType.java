package main.entities;

import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import main.graphics.ModPal;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;

import static main.entities.ModDamage.list;

public class ChainLightningBulletType extends BulletType {

    public float width, segmentLength, arc, distanceDamageFalloff;
    public int chainLightning, branches;

    public ChainLightningBulletType() {
        super();
        instantDisappear = true;
        lifetime = 1;
        despawnEffect = Fx.none;
        hitEffect = Fx.hitLancer;
        keepVelocity = false;
        hittable = false;
        pierceDamageFactor = 0.85f;
        distanceDamageFalloff = 0.65f;
        width = 8;
        arc = 0.35f;
        segmentLength = 4;
        lightningColor = ModPal.glowCyan;
        lightningLength = 0;
        chainLightning = 1;
        branches = 2;
        hitSound = Sounds.spark;
        despawnSound= Sounds.none;
    }

    @Override
    protected float calculateRange() {
        return range;
    }
    @Override
    public float estimateDPS() {
        return super.estimateDPS() * Math.max((float)this.lightningLength / 10.0F, 1.0F);
    }

    @Override
    public void draw(Bullet b) {
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        Seq<Unit> units = Groups.unit.intersect(b.x - range, b.y - range, range * 2, range * 2);
        units.sort(u -> u.dst(b));
        list.clear();
        for (int i = 0; i < Math.min(chainLightning, units.size); i++) {
            Unit unit = units.get(i);
            float dst = unit.dst(b);
            if(dst > range) break;
            list.add(unit);
        }

        list.each(u -> {
            ModDamage.chain(new Vec2(b.x, b.y), u, new IntSeq(), hitSound, hitEffect, b.damage, b.damage, width, distanceDamageFalloff, pierceDamageFactor, branches, segmentLength, arc, lightningColor);
        });
    }
}
