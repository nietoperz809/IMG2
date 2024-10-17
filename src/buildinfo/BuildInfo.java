package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1626";
    private static final String BUILD_DATE = "10/17/2024 11:22:18 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
