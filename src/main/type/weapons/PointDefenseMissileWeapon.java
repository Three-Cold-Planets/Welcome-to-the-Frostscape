package main.type.weapons;

import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.util.Tmp;
import mindustry.entities.units.*;
import mindustry.gen.*;

/**
 * A point defense gun that shoots missiles.
 * */
public class PointDefenseMissileWeapon extends BaseWeapon{

    public PointDefenseMissileWeapon(String name){
        super(name);
    }

    public PointDefenseMissileWeapon(){
    }

    {
        predictTarget = true;
        autoTarget = true;
        controllable = false;
        rotate = true;
        useAmmo = false;
        useAttackRange = false;
    }

    @Override
    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground){
        return Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b -> b.team != unit.team && b.type().hittable, b -> b.dst2(Tmp.v1) * (Angles.angleDist(b.angleTo(unit), b.rotation())/90));
    }

    @Override
    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range){
        return !(target.within(unit, range) && target.team() != unit.team && target instanceof Bullet bullet && bullet.type != null && bullet.type.hittable);
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
        if(!(mount.target instanceof Bullet target)) return;
        super.shoot(unit, mount, shootX, shootY, rotation);
    }
}
