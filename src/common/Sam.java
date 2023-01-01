package common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

public class Sam {
    static Method meth;

    /*
     * Load the sam!
     */
    static
    {
        try
        {
            byte[] clbytes = Tools.extractResource ("SamClass16");
            ByteArrayClassLoader bac = new ByteArrayClassLoader (clbytes);
            Class<?> cl = bac.loadClass ("samtool.SamClass");
            meth = cl.getMethod("xmain", PrintStream.class, String[].class);
        }
        catch (Exception e)
        {
            System.out.println ("Init failed: "+e);
        }
    }

    private static byte[] doSam (String txt) throws Exception
    {
        String[] arg = {"-stdout","dummy",txt};
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        PrintStream p = new PrintStream (ba);
        meth.invoke(null, p, (Object) arg);
        return ba.toByteArray ();
    }

    public static void speak (String in) {
        try {
            byte[] result = doSam (in);
            Tools.playWave (result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
