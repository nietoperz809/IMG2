package common;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;

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

    public static void hideConsoleWindow() {
        WinDef.HWND hw = Kernel32.INSTANCE.GetConsoleWindow();
        System.out.println("console: " + hw);
        if (hw != null) {
            User32.INSTANCE.ShowWindow(hw, 0);
        }
        //Kernel32.INSTANCE.FreeConsole(); // Detach from Console
    }

    public static void dialogToTop (@NotNull JDialog target) {
        String tit = target.getTitle();
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, tit);
        User32.INSTANCE.SetWindowPos
                (hwnd, HWND_MinusOne, 0, 0, 0, 0,
                        SWP_NOMOVE | SWP_NOSIZE | SWP_NOACTIVATE);
    }
}
