package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2076";
    private static final String BUILD_DATE = "01/16/2025 06:07:30 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
