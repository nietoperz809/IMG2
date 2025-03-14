package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2731";
    private static final String BUILD_DATE = "03/14/2025 04:14:03 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
