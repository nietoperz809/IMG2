package thegrid;

import java.util.ArrayList;

public class ImageViewController {

    private static final ArrayList<ImageView> _list = new ArrayList<>();

    public static void add(ImageView iv) {
        //System.out.println("ivm add: "+iv);
        _list.add(iv);
    }

    public static void remove(ImageView iv) {
        //System.out.println("ivm remove: "+iv);
        _list.remove(iv);
    }

    public static void killAllViews() {
        ArrayList<ImageView> cl = (ArrayList<ImageView>) _list.clone();
        for (ImageView iv: cl) {
            iv.dispose();
        }
    }
}
