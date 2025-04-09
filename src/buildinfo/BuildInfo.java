package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3119";
    private static final String BUILD_DATE = "04/09/2025 08:41:13 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
