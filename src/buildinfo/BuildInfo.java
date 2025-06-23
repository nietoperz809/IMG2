package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3539";
    private static final String BUILD_DATE = "06/22/2025 01:54:18 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
