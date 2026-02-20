package thegrid;

import java.util.Stack;

public class GlobalImageStack {
    private static GlobalImageStack inst = null;
    private GlobalImageStack() {}
    private Stack<Integer> stack = new Stack<>();

    public static void push(int rowid) {
        get().stack.push(rowid);
    }

    public static int pop() {
        return get().stack.pop();
    }

//    public static int poppush() {
//        int n = pop();
//        push(n);
//        return n;
//    }

    public static GlobalImageStack get() {
        if (inst == null)
            inst = new GlobalImageStack();
        return inst;
    }
}
