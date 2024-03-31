package thegrid;

import java.util.ArrayList;

public class ImageViewController {

    private ArrayList<ImageView> _list = new ArrayList<>();

    public void add(ImageView iv) {
        System.out.println("ivm add: "+iv);
        _list.add(iv);
    }

    public void remove(ImageView iv) {
        System.out.println("ivm remove: "+iv);
        _list.remove(iv);
    }

    public void killAll() {
        ArrayList<ImageView> cl = (ArrayList<ImageView>) _list.clone();
        for (ImageView iv: cl) {
            iv.dispose();
        }
    }
}
