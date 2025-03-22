package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2872";
    private static final String BUILD_DATE = "03/22/2025 05:15:40 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
