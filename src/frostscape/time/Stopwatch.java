package frostscape.time;

import arc.struct.Queue;
import arc.util.Strings;
import arc.util.Time;

public class Stopwatch {
    public Queue<TickData> labels = new Queue();
    private float last, cachedWait, totalWait;
    private boolean paused = false;

    public Stopwatch(){
        reset();
        return;
    }

    public void reset(){
        last = 0;
        cachedWait = 0;
        totalWait = 0;
        labels.clear();
        paused = true;
    }

    public void start(){
        if(!paused) return;
        paused = false;
        last = Time.millis();
    }

    public void tick(String label){
        totalWait = elapsed() + cachedWait;
        last = Time.millis();

        labels.add(new TickData(totalWait, last, label));
    }

    public void pause(){
        if(paused) return;
        paused = true;
        cachedWait = elapsed() + cachedWait;
    }

    public float elapsed(){
        return Time.millis() - last;
    }

    public static class TickData{
        public float timeTilLast;
        public float time;
        public String label;

        public TickData(float timeTilLast, float last, String label) {
            this.timeTilLast = timeTilLast;
            this.time = last;
            this.label = label;
        }
    }
}
