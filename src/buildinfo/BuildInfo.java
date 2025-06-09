package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3499";
    private static final String BUILD_DATE = "06/09/2025 02:00:46 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
