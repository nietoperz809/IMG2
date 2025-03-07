package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2628";
    private static final String BUILD_DATE = "03/07/2025 02:53:01 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
