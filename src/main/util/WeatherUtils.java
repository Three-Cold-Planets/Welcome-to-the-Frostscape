package main.util;

import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.gen.Groups;
import mindustry.type.weather.ParticleWeather;

public class WeatherUtils {
    public static Vec2 wind = new Vec2();

    /**
     * Returns a new Vec2 set to the dot product of the wind vectors via weather.
     */
    public static Vec2 windDirection(){
        return wind;
    };

    public static void updateWind(){
        Tmp.v1.set(0, 0);
        Groups.weather.each(w -> {
            if(!(w.weather instanceof ParticleWeather)) return;
            ParticleWeather weather = (ParticleWeather) w.weather;
            float speed = weather.force * w.intensity;
            float windx = w.windVector.x * speed, windy = w.windVector.y * speed;
            Tmp.v1.add(windx, windy);
        });
        wind.lerpDelta(Tmp.v1, 0.1f);
    }
}
