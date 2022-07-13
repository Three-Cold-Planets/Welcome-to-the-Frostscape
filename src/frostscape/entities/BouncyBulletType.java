package frostscape.entities;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.ItemTurret;

import java.awt.geom.QuadCurve2D;

//how many of these am I going to make I don't damm know
public class BouncyBulletType extends BasicBulletType {
    public static final float shadowTX = -12, shadowTY = -13;

    public float gravity;
    public float bounceEfficiency;
    public float startingHeight, startingLift;
    //conversion of the lost energy to forward momentum
    public float bounceForce;
    //Set to -1 to disable
    public int maxBounces;

    public int bounceIncend;
    public float bounceIncendSpread, bounceIncendChance;

    public boolean useMinLife;
    public boolean useRotation;
    public float shadowAlpha;
    public float bounceEffectScale;
    public float bounceShake;
    public Effect bounceEffect;

    public TextureRegion shadowRegion;

    public float minLife;

    private float visualHeightRange;

    public BouncyBulletType(float speed, float damage, String sprite){
        super(speed, damage, sprite);
        gravity = 0.0025f;
        bounceEfficiency = 0.8f;
        startingHeight = 0;
        startingLift = 0.05f;
        bounceForce = 1;
        maxBounces = -1;
        bounceIncend = 0;
        bounceIncendSpread = 5;
        bounceIncendChance = 0;
        useMinLife = true;
        useRotation = false;
        shadowAlpha = 1;
        bounceEffectScale = 0.04f;
        bounceShake = 0.5f;
        hitShake = 1;
        bounceEffect = Fx.unitLandSmall;
        trailEffect = Fx.artilleryTrail;
        collides = false;
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
        super.init(b);
        b.data = new HeightHolder(startingHeight, startingLift);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        HeightHolder holder = (HeightHolder) b.data;
        holder.lift -= gravity * Time.delta;
        holder.height += holder.lift;
        if(holder.height < 0) {
            bounce(b, holder);
        }
    }

    @Override
    public void draw(Bullet b) {
        drawTrail(b);
        float h = getHeight(b);
        float height = this.height * ((1f - shrinkY) + shrinkY * h);
        float width = this.width * ((1f - shrinkX) + shrinkX * h);
        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;

        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

        Draw.mixcol(mix, mix.a);

        Draw.color(Pal.shadow, Pal.shadow.a * shadowAlpha);
        Draw.z(Layer.darkness);
        Draw.rect(shadowRegion, b.x + shadowTX * h, b.y + shadowTY * h,  width, height,b.rotation() - 90);

        float[] layers = new float[]{Layer.flyingUnit - 1, Layer.bullet};

        //What this oes is make the bullet glow the closer it is to the ground.
        for (int i = 0; i < 2; i++) {
            Draw.z(layers[i]);
            float visibility = h/visualHeightRange;
            if(i == 1) visibility = 1 - visibility;

            if(backRegion.found()){
                Draw.color(backColor);
                Draw.alpha(visibility);
                Draw.rect(backRegion, b.x, b.y, width, height, b.rotation() + offset);
            }

            Draw.color(frontColor);
            Draw.alpha(visibility);
            Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation() + offset);
        }
        Draw.reset();

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

    public void setBounces(int amount){

    }

    @Override
    public void createSplashDamage(Bullet b, float x, float y) {
        super.createSplashDamage(b, x, y);
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
        if(b.fdata > maxBounces && maxBounces != -1) {
            b.hit();
            b.remove();
        }
    }

    public float getHeight(Bullet b){
        return ((HeightHolder) b.data).height;
    }

    protected class HeightHolder{
        public float height;
        public float lift;

        public HeightHolder(float height, float lift){
            this.height = height;
            this.lift = lift;
        }
    }
}
