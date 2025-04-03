package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2986";
    private static final String BUILD_DATE = "04/04/2025 12:33:06 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
