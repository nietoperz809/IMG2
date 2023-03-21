package thegrid;

import java.util.ArrayList;
import java.util.Collections;

class UniqueRng {
    private final ArrayList<Integer> num1 = new ArrayList<>();
    private int idx = 0;

    public UniqueRng(int n) {

        for (int s = 0; s < n; s++) {
            num1.add(s);
        }

        Collections.shuffle(num1);
        //System.out.println(num1);
    }

    int getNext() {
        idx++;
        if (idx == num1.size())
            idx = 0;
        return num1.get(idx);
    }

    int getPrev() {
        idx--;
        if (idx == -1)
            idx = num1.size()-1;
        return num1.get(idx);
    }
}
