package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2224";
    private static final String BUILD_DATE = "01/24/2025 08:09:45 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
