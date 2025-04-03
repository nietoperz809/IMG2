package common;

public class Stepper {

    private final float from;
    private final float to;
    private final int steps;
    private final float st;

    public Stepper (float from, float to, int steps) {
        this.from = from;
        this.to = to;
        this.steps = steps;
        this.st = (to-from)/steps;
    }

    public float get (int n) {
        if (n < 0) n = 0;
        else if (n > steps) n = steps;
        return from + n*st;
    }

    public static void main(String[] args) {
        Stepper st = new Stepper(-2f,10f, 255);
        System.out.println(st.get(0));
        System.out.println(st.get(1));
        System.out.println(st.get(50));
        System.out.println(st.get(254));
        System.out.println(st.get(255));
        System.out.println(st.get(256));
    }
}
