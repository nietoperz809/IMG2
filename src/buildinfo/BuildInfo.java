package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2621";
    private static final String BUILD_DATE = "03/06/2025 07:27:36 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
