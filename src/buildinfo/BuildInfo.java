package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3153";
    private static final String BUILD_DATE = "04/11/2025 10:03:18 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
