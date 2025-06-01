package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3398";
    private static final String BUILD_DATE = "06/01/2025 05:25:28 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
