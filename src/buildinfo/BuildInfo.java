package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3525";
    private static final String BUILD_DATE = "06/18/2025 07:23:05 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
