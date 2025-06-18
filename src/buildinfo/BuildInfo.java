package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3520";
    private static final String BUILD_DATE = "06/17/2025 06:37:57 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
