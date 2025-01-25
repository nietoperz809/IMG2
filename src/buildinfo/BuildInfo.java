package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2266";
    private static final String BUILD_DATE = "01/25/2025 10:41:18 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
