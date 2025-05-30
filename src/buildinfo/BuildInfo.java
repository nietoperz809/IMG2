package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3364";
    private static final String BUILD_DATE = "05/30/2025 08:43:58 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
