package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2413";
    private static final String BUILD_DATE = "02/14/2025 04:12:08 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
