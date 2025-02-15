package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2424";
    private static final String BUILD_DATE = "02/15/2025 03:52:09 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
