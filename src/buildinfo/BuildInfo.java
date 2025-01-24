package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2254";
    private static final String BUILD_DATE = "01/24/2025 08:21:48 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
