package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1314";
    private static final String BUILD_DATE = "01/30/2024 10:51:21 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
