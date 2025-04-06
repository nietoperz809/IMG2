package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3055";
    private static final String BUILD_DATE = "04/07/2025 12:38:25 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
