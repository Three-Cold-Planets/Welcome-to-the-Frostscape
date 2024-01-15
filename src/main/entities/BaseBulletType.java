package main.entities;

import arc.func.Prov;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Trail;
import mindustry.logic.Ranged;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;

public class BaseBulletType extends BasicBulletType {

    public HomingType homing;
    public boolean useTrueSpeed;
    public float trueSpeed,
    rotationOffset;

    public Prov<Trail> trailProv;



    @Override
    public void init() {
        super.init();
        if(!useTrueSpeed) trueSpeed = speed;
        if(trailProv == null) trailProv = () -> new Trail(trailLength);
    }

    @Override
    public void updateTrail(Bullet b) {
        if (!Vars.headless && this.trailLength > 0) {
            if (b.trail == null) {
                b.trail = trailProv.get();
            }

            b.trail.length = this.trailLength;
            b.trail.update(b.x, b.y, this.trailInterp.apply(b.fin()) * (1.0F + (this.trailSinMag > 0.0F ? Mathf.absin(Time.time, this.trailSinScl, this.trailSinMag) : 0.0F)));
        }

    }

    @Override
    public void updateHoming(Bullet b) {
        homing.update(b);
    }

    public BaseBulletType(float speed, float damage, String bSprite){
        super(speed, damage, bSprite);
        homing = basicHomeDefault;
    }

    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data){
        Bullet bullet = Bullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.vel.trns(angle + rotationOffset, (useTrueSpeed ? trueSpeed : speed) * velocityScl);
        if(backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = drag;
        bullet.hitSize = hitSize;
        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
        bullet.add();

        if(keepVelocity && owner instanceof Velc) bullet.vel.add(((Velc) bullet.owner).vel());
        return bullet;
    }

    public interface HomingType{
        void update(Bullet b);
    }

    public interface Targeting{
        Vec2 targetPos();
    }

    public static class BasicHoming implements HomingType{

        public BasicHoming(boolean homeCollided){
            this.homeCollided = homeCollided;
        }
        public boolean homeCollided;
        @Override
        public void update(Bullet b) {
            if (b.type.homingPower > 1.0E-4F && b.time >= b.type.homingDelay) {
                float realAimX = b.aimX < 0.0F ? b.x : b.aimX;
                float realAimY = b.aimY < 0.0F ? b.y : b.aimY;
                Object target;
                if (b.type.heals()) {
                    target = Units.closestTarget(null, realAimX, realAimY, b.type.homingRange, (e) -> e.checkTarget(b.type.collidesAir, b.type.collidesGround) && e.team != b.team && !b.hasCollided(e.id),
                            (t) -> b.type.collidesGround && (t.team != b.team || t.damaged()) && (homeCollided || !b.hasCollided(t.id))
                    );
                } else if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && b.type.collidesGround && (homeCollided || !b.hasCollided(b.aimTile.build.id))) {
                    target = b.aimTile.build;
                } else {
                    target = Units.closestTarget(b.team, realAimX, realAimY, b.type.homingRange,
                            (e) -> e.checkTarget(b.type.collidesAir, b.type.collidesGround) && (homeCollided || !b.hasCollided(e.id)),
                            (t) -> b.type.collidesGround && (homeCollided || !b.hasCollided(t.id)));
                }

                if (target != null) {
                    b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo((Position)target), b.type.homingPower * Time.delta * 50.0F));
                }
            }
        }
    }
    
    public static class VelBasedHoming implements HomingType{
        public boolean limitRange;

        public VelBasedHoming(boolean limitRange){
            this.limitRange = limitRange;
        }

        public void update(Bullet b){

            if(!(b.owner instanceof Ranged) || b.time < b.type.homingDelay) return;

            float trueBulletSpeed = b.type instanceof BaseBulletType ? ((BaseBulletType) b.type).trueSpeed : b.type.speed;
            Tmp.v1.set(b.x, b.y);
            //handle modded cases of b owners first
            if(b.owner instanceof Targeting){
                Tmp.v1.set(((Targeting) b.owner).targetPos());
            }
            else if(b.owner instanceof TurretBuild) {
                Tmp.v1.set(((TurretBuild) b.owner).targetPos.x, ((TurretBuild) b.owner).targetPos.y);
            }
            else if (b.owner instanceof Unitc){
                Tmp.v1.set(((Unitc) b.owner).aimX(), ((Unitc) b.owner).aimY());
            }
            Tmp.v3.set(((Posc) b.owner()).x(), ((Posc) b.owner()).y());
            if(limitRange) Tmp.v1.sub(Tmp.v3).clamp(0, ((Ranged) b.owner).range()).add(Tmp.v3);
            b.vel.add(Tmp.v2.trns(b.angleTo(Tmp.v1), b.type.homingPower * Time.delta)).clamp(0, trueBulletSpeed);
            if(limitRange && b.dst(Tmp.v3.x, Tmp.v3.y) >= ((Ranged) b.owner).range() + trueBulletSpeed + 3) b.time += b.lifetime/100 * Time.delta;
            
            //essentualy goes to owner aim pos, without stopping homing
        }
    }

    public static HomingType
        basicHomeDefault = new BasicHoming(false),
        basicHomeCollided = new BasicHoming(true),
        velHomeDefault = new VelBasedHoming(false),
        velHomeRange = new VelBasedHoming(true);
}
