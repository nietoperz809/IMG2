package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1935";
    private static final String BUILD_DATE = "11/10/2024 07:10:06 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
