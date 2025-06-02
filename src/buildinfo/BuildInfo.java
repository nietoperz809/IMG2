package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3417";
    private static final String BUILD_DATE = "06/02/2025 03:18:56 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
