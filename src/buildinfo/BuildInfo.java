package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2419";
    private static final String BUILD_DATE = "02/14/2025 06:34:31 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
