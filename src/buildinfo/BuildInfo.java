package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3224";
    private static final String BUILD_DATE = "04/17/2025 07:55:41 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
