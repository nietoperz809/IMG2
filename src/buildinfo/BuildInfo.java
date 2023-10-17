package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1121";
    private static final String BUILD_DATE = "10/17/2023 04:39:15 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
