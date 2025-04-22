package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3268";
    private static final String BUILD_DATE = "04/22/2025 04:38:49 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
