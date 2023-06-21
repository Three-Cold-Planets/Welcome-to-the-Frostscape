package main.math;

import arc.math.Interp;

public class MultiInterp implements Interp {
    final float[] timings;
    final Interp[] interps;
    private Interp tinterp;
    private float tmp;

    public MultiInterp(float[] timings, Interp[] interps){
        this.timings = timings;
        this.interps = interps;
    }

    @Override
    public float apply(float a) {
        for (int i = 0; i < timings.length; i++) {
            if(i == timings.length - 1 || a < timings[i + 1]) {
                if(i == timings.length-1) tmp = 1;
                else tmp = timings[i + 1];

                return interps[i].apply((a-timings[i])/(tmp - timings[i]));
            }
        }
        return interps[timings.length - 1].apply(a);
    }
}
