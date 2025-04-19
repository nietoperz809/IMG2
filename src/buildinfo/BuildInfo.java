package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3249";
    private static final String BUILD_DATE = "04/19/2025 05:12:42 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
