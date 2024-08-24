package common;

import java.util.prefs.Preferences;

public class PersistString {

    String defaultStr;
    String entry;


    public PersistString(String entry, String defaultStr) {
        this.defaultStr = defaultStr;
        this.entry = entry;
    }

    public String set(String s) {
        Preferences.userNodeForPackage(PersistString.class)
                .put(entry, s);
        return s;
    }

    public void reset() {
        set (defaultStr);
    }

    public String get() {
        return Preferences.userNodeForPackage(PersistString.class)
                .get(entry, defaultStr);
    }
    public String getDefault() {
        return defaultStr;
    }

}
