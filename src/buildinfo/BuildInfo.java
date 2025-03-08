package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2639";
    private static final String BUILD_DATE = "03/07/2025 11:12:04 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
