package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2892";
    private static final String BUILD_DATE = "04/02/2025 03:04:21 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
