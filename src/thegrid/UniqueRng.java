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
    public UniqueRng(int n) {
        for (int s = 0; s < n; s++) {
            _list.add(s);
        }
        Collections.shuffle(_list);
    }

    public void reset() {
        int n = _list.size();
        _list.clear();
        for (int s = 0; s < n; s++) {
            _list.add(s);
        }
        idx = 0;
    }

//    public void setPointer (int n) {
//        idx = n % _list.size();
//    }
//
//    public int findElement (int e) {
//        for (int n=0; n<_list.size(); n++) {
//            if (_list.get(n) == e) {
//                return n;
//            }
//        }
//        return -1; // not found
//    }

    /**
     * Get next element
     * @return
     */
    int getNext() {
        idx++;
        if (idx == _list.size())
            idx = 0;
        return _list.get(idx);
    }

    /**
     * Get previous element
     * @return
     */
    int getPrev() {
        idx--;
        if (idx == -1)
            idx = _list.size()-1;
        return _list.get(idx);
    }
}
