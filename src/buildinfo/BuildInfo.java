package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2828";
    private static final String BUILD_DATE = "03/15/2025 10:17:46 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
