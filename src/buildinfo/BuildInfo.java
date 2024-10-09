package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1512";
    private static final String BUILD_DATE = "10/09/2024 04:52:01 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
