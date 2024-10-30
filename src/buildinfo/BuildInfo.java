package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1795";
    private static final String BUILD_DATE = "10/30/2024 11:11:17 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
