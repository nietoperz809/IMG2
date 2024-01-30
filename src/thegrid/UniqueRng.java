package thegrid;

import java.util.ArrayList;
import java.util.Collections;

class UniqueRng {
    private final ArrayList<Integer> _list = new ArrayList<>();
    private int idx = 0;

    /**
     * Constructor
     * @param n number of elements
     */
    public UniqueRng (int n, boolean shuffle) {
        for (int s = 0; s < n; s++) {
            _list.add(s);
        }
        if (shuffle)
            Collections.shuffle(_list);
    }

    public UniqueRng (int n) {
        this (n, true);
    }

    public void setIndex (int i) {
        idx = i;
    }

    /**
     * Get next element
     * @return
     */
    int getNext() {
        idx++;
        if (idx >= _list.size())
            idx = 0;
        return _list.get(idx);
    }

    /**
     * Get previous element
     * @return
     */
    int getPrev() {
        idx--;
        if (idx <= -1)
            idx = _list.size()-1;
        return _list.get(idx);
    }
}
