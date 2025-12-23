package common;

import java.util.Collection;
import java.util.TreeSet;

public class Csv {
    public static String normalizeCSVString(String in) {
        TreeSet<String> set = getSetFromCSVString(in);
        return CsvStringFromSet(set);
    }

    /**
     *
     * @param s1 Source (will be changed)
     * @param s2 elements already in s1 are deleted, all others are added
     */
    public static void combineSpecial (TreeSet<String> s1, Collection<String> s2) {
        for (String s : s2) {
            if (s1.contains(s)) {
                s1.remove(s);
            } else {
                s1.add(s);
            }
        }
    }

    public static TreeSet<String> getSetFromCSVString(String csv) {
        TreeSet<String> tset = new TreeSet<>();
        if (csv == null)
            return tset; // return empty treeset if input is null
        String[] arr = csv.replace(" ","").split(",");
        for (int n = 0; n < arr.length; n++) {
            arr[n] = arr[n].trim();
            if (arr[n].length() > 1) // ignore single-char strings
                tset.add(arr[n]);
        }
        tset.remove("");
        return tset;
    }

    public static String CsvStringFromSet(TreeSet<String> tset) {
        StringBuilder sb = new StringBuilder();
        for (String s : tset) {
            sb.append(s).append(", ");
        }
        String s2 = sb.toString();
        if (s2.endsWith(", ")) {
            s2 = s2.substring(0, s2.length() - 2);
        }
        return s2;
    }
}
