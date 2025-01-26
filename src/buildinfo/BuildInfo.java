package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2293";
    private static final String BUILD_DATE = "01/26/2025 02:36:13 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
