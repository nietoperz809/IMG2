package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1558";
    private static final String BUILD_DATE = "10/10/2024 11:53:37 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
