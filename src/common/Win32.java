package common;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import java.awt.*;

import static com.sun.jna.platform.win32.WinUser.*;

public class Win32 {

    public static final HWND HWND_MinusOne = new HWND(new Pointer(-1));

    static {
        if (!System.getProperty("os.name").startsWith("Windows")) {
            MsgBox.Error("Please run this only on your windows box\n" +
                    "App will close now");
            System.exit(-1);
        }
    }

    public static void hideConsoleWindow(boolean hide) {
        WinDef.HWND hw = Kernel32.INSTANCE.GetConsoleWindow();
        System.out.println("console: " + hw);
        if (hw != null) {
            User32.INSTANCE.ShowWindow(hw, hide ? 0 : 5);
        }
    }

    public static HWND getHwnd(Window w) {
//        HWND hwnd = new HWND();
//        hwnd.setPointer (Native.getWindowPointer(w));
//        return hwnd;
        return new HWND(Native.getWindowPointer(w));
    }

    public static void dialogToTop (@NotNull JDialog target) {
        WinDef.HWND hwnd = getHwnd(target);
        User32.INSTANCE.SetWindowPos
                (hwnd, HWND_MinusOne, 0, 0, 0, 0,
                        SWP_NOMOVE | SWP_NOSIZE /*| SWP_NOACTIVATE*/);
    }

//    public static boolean CopyFile (String src, String target) {
//        return Kernel32.INSTANCE.CopyFile(src, target, false);
//    }

//    public static boolean CopyFileEx (String src, String target) {
//        return Kernel32.INSTANCE.CopyFileEx
//    }

}

