package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2476";
    private static final String BUILD_DATE = "02/20/2025 05:04:18 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
