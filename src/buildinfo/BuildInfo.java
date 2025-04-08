package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3077";
    private static final String BUILD_DATE = "04/08/2025 02:39:27 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
