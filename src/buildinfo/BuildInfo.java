package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3506";
    private static final String BUILD_DATE = "06/10/2025 12:18:09 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
