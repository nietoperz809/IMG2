package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3478";
    private static final String BUILD_DATE = "06/07/2025 03:50:10 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
