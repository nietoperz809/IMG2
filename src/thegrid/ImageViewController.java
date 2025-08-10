package thegrid;

import database.DBHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static common.Tools.buildQueryForGrid;

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
        @SuppressWarnings("unchecked")
        ArrayList<JFrame> cl = (ArrayList<JFrame>) _list.clone();
        for (JFrame iv: cl) {
            iv.dispose();
        }
    }

    public static void combineGrids() {
        Set<Integer> hset = new HashSet<>();
        for (JFrame iv: _list) {
            if (iv instanceof TheGrid gr) {
                int si = gr.imageL.size();
                for (DBHandler.NameID nid : gr.imageL.allFiles) {
                    hset.add(nid.rowid());
                }
                gr.dispose();
            }
        }
        String q = buildQueryForGrid(hset);
        (new Thread(() -> new TheGrid(q, "WORKER"))).start();
    }

}
