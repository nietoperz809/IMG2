package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2842";
    private static final String BUILD_DATE = "03/18/2025 03:32:06 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
