package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2551";
    private static final String BUILD_DATE = "03/02/2025 05:47:01 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
