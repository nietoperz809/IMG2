package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2323";
    private static final String BUILD_DATE = "01/29/2025 08:13:21 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
