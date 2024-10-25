package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1723";
    private static final String BUILD_DATE = "10/26/2024 01:46:29 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
