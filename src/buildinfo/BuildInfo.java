package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3342";
    private static final String BUILD_DATE = "05/26/2025 08:37:59 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
