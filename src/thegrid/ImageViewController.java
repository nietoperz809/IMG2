package thegrid;

import javax.swing.*;
import java.util.ArrayList;

public class ImageViewController {

    private static final ArrayList <JFrame> _list = new ArrayList<>();

    public static void add(JFrame iv) {
        //System.out.println("ivm add: "+iv);
        _list.add(iv);
    }

    public static void remove(JFrame iv) {
        //System.out.println("ivm remove: "+iv);
        _list.remove(iv);
    }

    public static void killAllViews() {
        ArrayList<JFrame> cl = (ArrayList<JFrame>) _list.clone();
        for (JFrame iv: cl) {
            iv.dispose();
        }
    }
}
