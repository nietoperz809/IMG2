package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2673";
    private static final String BUILD_DATE = "03/10/2025 04:51:41 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
