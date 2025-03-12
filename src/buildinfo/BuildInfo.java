package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2704";
    private static final String BUILD_DATE = "03/12/2025 10:18:17 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
