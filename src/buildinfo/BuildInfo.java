package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1446";
    private static final String BUILD_DATE = "09/25/2024 03:40:36 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
