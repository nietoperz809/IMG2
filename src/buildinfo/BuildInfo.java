package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3292";
    private static final String BUILD_DATE = "04/26/2025 11:19:51 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
