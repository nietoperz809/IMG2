package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3411";
    private static final String BUILD_DATE = "06/01/2025 06:46:49 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
