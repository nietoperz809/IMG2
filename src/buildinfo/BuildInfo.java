package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2378";
    private static final String BUILD_DATE = "02/07/2025 10:06:20 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
