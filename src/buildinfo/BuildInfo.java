package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3538";
    private static final String BUILD_DATE = "06/19/2025 08:15:02 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
