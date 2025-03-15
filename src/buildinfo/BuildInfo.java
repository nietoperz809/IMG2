package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2825";
    private static final String BUILD_DATE = "03/15/2025 08:26:02 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
