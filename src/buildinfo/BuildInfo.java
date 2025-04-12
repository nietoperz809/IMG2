package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3172";
    private static final String BUILD_DATE = "04/12/2025 03:32:51 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
