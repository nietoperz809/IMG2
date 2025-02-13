package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2385";
    private static final String BUILD_DATE = "02/13/2025 06:26:54 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
