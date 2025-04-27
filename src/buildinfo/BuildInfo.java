package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3313";
    private static final String BUILD_DATE = "04/27/2025 02:03:55 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
