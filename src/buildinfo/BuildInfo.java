package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2138";
    private static final String BUILD_DATE = "01/22/2025 04:25:54 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
