package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3434";
    private static final String BUILD_DATE = "06/06/2025 04:03:33 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
