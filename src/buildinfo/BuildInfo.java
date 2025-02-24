package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2489";
    private static final String BUILD_DATE = "02/24/2025 06:12:28 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
