package common;

public class UpDown {
    private final float[] vals;
    private int pos;

    public UpDown (float[] vals, int init) {
        this.vals = vals;
        pos = init;
    }

    public float current() {
        return vals[pos];
    }

    public float up() {
        if (pos < vals.length-1)
            pos++;
        return vals[pos];
    }

    public float down() {
        if (pos > 0)
            pos--;
        return vals[pos];
    }

}
