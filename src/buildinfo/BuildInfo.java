package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1824";
    private static final String BUILD_DATE = "10/31/2024 03:26:32 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
