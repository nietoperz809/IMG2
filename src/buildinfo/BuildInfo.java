package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2840";
    private static final String BUILD_DATE = "03/16/2025 12:48:01 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
