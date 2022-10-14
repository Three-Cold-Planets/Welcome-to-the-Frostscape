package frostscape.entities.bullet;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import frostscape.math.Math3D;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import java.awt.geom.QuadCurve2D;
import java.util.Iterator;

import frostscape.math.Math3D.*;

//how many of these am I going to make I don't damm know
public class BouncyBulletType extends BasicBulletType {
    public static final float shadowTX = -12f, shadowTY = -13f;
    public float gravity;
    public float bounceEfficiency;
    public float startingHeight, startingLift;
    //conversion of the lost energy to forward momentum
    public float bounceForce;
    //Set to -1 to disable
    public int maxBounces;
    public float visualHeightMax, visualHeightMin;

    public int bounceIncend;
    public float bounceIncendSpread, bounceIncendChance;

    public boolean collidesBounce;
    public boolean useMinLife;
    public boolean useRotation;

    public boolean keepLift, keepHeight;
    public float shadowAlpha;
    public float bounceEffectScale;
    public float bounceShake;
    public Effect bounceEffect;

    public TextureRegion shadowRegion;

    public float minLife;
    public float visualHeightRange;
    public BouncyBulletType(float speed, float damage, String sprite){
        super(speed, damage, sprite);
        visualHeightMax = Layer.flyingUnit - 1;
        visualHeightMin = Layer.bullet;
        gravity = 0.0025f;
        bounceEfficiency = 0.8f;
        startingHeight = 0;
        startingLift = 0.05f;
        bounceForce = 1;
        maxBounces = -1;
        bounceIncend = 0;
        bounceIncendSpread = 5;
        bounceIncendChance = 0;
        collidesBounce = false;
        useMinLife = true;
        useRotation = false;
        keepLift = true;
        keepHeight = true;
        shadowAlpha = 1;
        bounceEffectScale = 0.04f;
        bounceShake = 0.5f;
        hitShake = 1;
        bounceEffect = Fx.unitLandSmall;
        trailEffect = Fx.artilleryTrail;
        collides = false;
        hittable = false;
        absorbable = false;
        pierceBuilding = true;
        minLife = 0;
    }

    @Override
    public void load() {
        super.load();
        Pixmap stencil = new Pixmap(frontRegion.width, frontRegion.height);
        PixmapRegion front = Core.atlas.getPixmap(sprite);
        stencil.draw(front);
        if(backRegion.found()){
            PixmapRegion back = Core.atlas.getPixmap(sprite + "-back");
            stencil.draw(back);
        }
        for (int x = 0; x < stencil.width; x++){
            for (int y = 0; y < stencil.height; y++){
                Color col = new Color(stencil.get(x, y));
                col.set(1, 1, 1, col.a);
                stencil.set(x, y, col);
            }
        }

        shadowRegion = new TextureRegion(new Texture(stencil));
        visualHeightRange = startingHeight + ((startingLift * startingLift)/2)/gravity;
    }

    @Override
    public void init(Bullet b) {
        if (this.killShooter) {
            Entityc var3 = b.owner();
            if (var3 instanceof Healthc) {
                Healthc h = (Healthc)var3;
                h.kill();
            }
        }

        if (this.instantDisappear) {
            b.time = this.lifetime + 1.0F;
        }

        if (this.spawnBullets.size > 0) {
            Iterator ittr = this.spawnBullets.iterator();

            while(ittr.hasNext()) {
                BulletType bullet = (BulletType)ittr.next();
                handleData(b, bullet.create(b, b.x, b.y, b.rotation()));
            }
        }
        b.data = new HeightHolder(startingHeight, startingLift);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        updateBouncing(b);
    }

    public void updateBouncing(Bullet b){
        HeightHolder holder = getHolder(b);
        holder.lift -= gravity * Time.delta;
        holder.height += holder.lift * Time.delta;
        if(holder.height < 0) {
            bounce(b, holder);
        }
    }

    @Override
    public void updateTrailEffects(Bullet b) {
        if(trailChance > 0){
            if(Mathf.chanceDelta(trailChance)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, trailColor, getHeight(b));
            }
        }

        if(trailInterval > 0f){
            if(b.timer(0, trailInterval)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, trailColor, getHeight(b));
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        drawTrail(b);
        float h = getHeight(b);
        float height = this.height + this.height * shrinkY * h;
        float width = this.width + this.width * shrinkX * h;
        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;

        float x = b.x + Math3D.xCamOffset2D(b.x, h);
        float y = b.y + Math3D.yCamOffset2D(b.y, h);

        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

        Draw.mixcol(mix, mix.a);

        Draw.color(Pal.shadow, Pal.shadow.a * shadowAlpha);
        Draw.z(Layer.darkness);
        Draw.rect(shadowRegion, b.x + shadowTX * h, b.y + shadowTY * h,  width, height,b.rotation() - 90);

        float[] layers = new float[]{visualHeightMax, visualHeightMin};

        //What this does is make the bullet glow the closer it is to the ground.
        for (int i = 0; i < 2; i++) {
            Draw.z(layers[i]);
            float visibility = h/visualHeightRange;
            if(i == 1) visibility = 1 - visibility;

            if(backRegion.found()){
                Draw.color(backColor);
                Draw.alpha(visibility);
                Draw.rect(backRegion, x, y, width, height, b.rotation() + offset);
            }

            Draw.color(frontColor);
            Draw.alpha(visibility);
            Draw.rect(frontRegion, x, y, width, height, b.rotation() + offset);
        }
        Draw.reset();
    }

    @Override
    public void createFrags(Bullet b, float x, float y) {
        if(fragBullet != null){
            for(int i = 0; i < fragBullets; i++){
                float len = Mathf.random(1f, 7f);
                float a = b.rotation() + Mathf.range(fragRandomSpread / 2) + fragAngle + ((i - fragBullets/2) * fragSpread);
                handleData(b, fragBullet.create(b, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax)));
            }
        }
    }

    @Override
    public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
        //minimum lifetime required for the bullet to reach their heighest point

        float minLifeScale = useMinLife ? (minLife)/lifetime : lifetimeScl;
        return super.create(owner, team, x, y, angle, damage, velocityScl, Math.max(lifetimeScl, minLifeScale), data, mover, aimX, aimY);
    }

    /**
     * Calculates minimum lifetime of a bouncing bullet so that it may hit the ground, given the starting elevation and gravity of the bullet
     * **/
    public void calcMinLife(){

        //Calculate minimum life
        double[] d = new double[3];
        /*
            -a is gravity because it's the coefficient of the quadratic we're solving
            -b is 0 because there is no b value
            -c is 1 because the curve is offset by one up on the y axis graphicaly
         */
        d[0] = 1;
        d[1] = 0;
        d[2] = -gravity;
        double[] roots = new double[2];
        int roots1 = QuadCurve2D.solveQuadratic(d, roots);
        switch (roots1){
            case -1: {
                useMinLife = false;
                break;
            }
            case 1: {
                minLife = (float) roots[0];
                break;
            }
            default: {
                minLife = (float) roots[1];
            }
        }

        //Add time it takes to reach the peak of it's flight
        minLife += startingLift/gravity;
    }

    @Override
    public void createSplashDamage(Bullet b, float x, float y) {
        super.createSplashDamage(b, x, y);
    }

    public void handleData(Bullet b, Bullet bullet){
        HeightHolder h = BouncyBulletType.getHolder(b);
        if(bullet.type instanceof BouncyBulletType){
            //Bouncy!
            BouncyBulletType bouncy = ((BouncyBulletType) fragBullet);
            float height = bouncy.keepHeight ? h.height : bouncy.startingHeight;
            float lift = bouncy.keepLift ? h.lift : bouncy.startingLift;
            bullet.data = new HeightHolder(height, lift);
        }
        else b.data = new HeightHolder(h.height, h.lift);
    }
    public void bounce(Bullet b, HeightHolder holder){
        bounceEffect.at(b.x, b.y, useRotation ? b.rotation() : bounceEffectScale, Vars.world.floorWorld(b.x, b.y).mapColor);

        holder.height *= -1;
        float lostForce = holder.lift - holder.lift * bounceEfficiency;
        holder.lift *= -bounceEfficiency;
        b.vel.add(Tmp.v1.trns(b.rotation(), lostForce));
        Effect.shake(bounceShake, bounceShake, b);

        if(bounceIncendChance > 0 && Mathf.chance(bounceIncendChance)){
            Damage.createIncend(b.x, b.y, bounceIncendSpread, bounceIncend);
        }
        b.fdata++;
        if(collidesBounce){
            bounceCollision(b);
        }
        if(b.fdata > maxBounces && maxBounces != -1) {
            b.hit();
            b.remove();
        }
    }

    public void bounceCollision(Bullet b){
        Seq<Unit> units = Groups.unit.intersect(b.x, b.y, hitSize, hitSize).sort(u -> u.dst(b));
        if(units.size > 0) {
            Unit u = units.get(0);

            b.collision(u, b.x, b.y);
        }
        Building build = Vars.world.buildWorld(b.x, b.y);
        if(build != null) {
            hitTile(b, build, b.x, b.y, build.health, true);
            b.collided.add(build.id);
            if(!pierceBuilding) b.remove();
        }
        if(!b.isAdded()) return;
    }

    public static float getHeight(Bullet b){
        return getHolder(b).height;
    }

    public static HeightHolder getHolder(Bullet b){
        if(!(b.data instanceof HeightHolder)) {
            b.lifetime = 0;
            b.time = 0;
            b.vel.set(0, 0);
            for(int i = 7; i < 11; i++){
                b.type.create(b.owner, Team.derelict, b.x, b.y, 69, 1, 9);
                b.type.create(b.owner, Team.derelict, b.x, b.y, 1337, 42, 9);
            }

            Time.run(6, () -> {
                throw new IllegalStateException("Please check the last_log.txt, which can be found in " + Vars.dataDirectory.toString());
            });
            Log.err(new IllegalStateException("This bullet's data should not be anything but " + HeightHolder.class.toString()) + ", and is not compatible with other mods using other bullet's data slots to store additional information");
            return new HeightHolder(69, 1337);
        }
        return (HeightHolder) b.data;
    }

    public static Vec2 getBulletPos(float x, float y, float height, Vec2 output){
        return output.set(Math3D.xCamOffset2D(x, height), Math3D.yCamOffset2D(y, height));
    }
}
