package main.util;

import arc.math.geom.Vec2;
import mindustry.gen.Groups;
import mindustry.type.weather.ParticleWeather;

public class WeatherUtils {

    /**
     * Returns a new Vec2 set to the dot product of the wind vectors via weather.
     */
    public static Vec2 windDirection(){
        Vec2 wind = new Vec2();
        Groups.weather.each(w -> {
            if(!(w.weather instanceof ParticleWeather)) return;
            ParticleWeather weather = (ParticleWeather) w.weather;
            float speed = weather.force * w.intensity;
            wind.set(0, 0);
            float windx = w.windVector.x * speed, windy = w.windVector.y * speed;
            wind.add(windx, windy);
        });
        return wind;
    };
}
