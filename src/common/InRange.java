package common;

public class InRange {
    private final int max;
    private final int min;

    public InRange (int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int filter (int in) {
        if (in < min)
            return min;
        if (in > max)
            return max;
        return in;
    }
}
