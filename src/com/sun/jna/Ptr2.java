package com.sun.jna;

public class Ptr2 {
    public long peer;
    public Ptr2(Pointer p) {
        peer = p.peer;
    }
}
