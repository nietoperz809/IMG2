package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3447";
    private static final String BUILD_DATE = "06/06/2025 07:41:21 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
