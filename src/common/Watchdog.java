package common;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Watchdog {

    interface User32 extends Library {
        User32 INSTANCE = Native.loadLibrary("User32", User32.class);
        short GetAsyncKeyState(int key);
        short GetKeyState(int key);

        static boolean isKeyPressed(int key)
        {
            return User32.INSTANCE.GetAsyncKeyState(key) == -32767;
        }
    }

    public static void start () {
        //Timer timer = new Timer();

        TimerTask task1 = new TimerTask() {
            int count = 0;
            final Point oldpt = new Point(-1,-1);

            public void run() {
                for (int key = 1; key < 256; key++)
                {
                    if (User32.isKeyPressed(key)) {
                        count = 0;
                        System.out.println("+++ Key pressed");
                        break;
                    }
                }

                Point pt = MouseInfo.getPointerInfo().getLocation();
                if (!pt.equals(oldpt)) {
                    count = 0;
                    System.out.println("+++ mouse moved");
                    oldpt.x = pt.x;
                    oldpt.y = pt.y;
                }

                if (count++ > 240 /* 2 minutes */) {
                    System.out.println("shutdown due to inactivity");
                    Tools.shutdown(new Frame());
                }
            }
        };

        new Timer().schedule(task1, 0,500);
    }
}
