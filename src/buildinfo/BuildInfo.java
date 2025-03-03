package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2595";
    private static final String BUILD_DATE = "03/03/2025 05:00:20 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
